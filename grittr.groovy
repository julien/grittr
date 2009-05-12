
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import SQLite.Database;
import SQLite.Stmt;

import twitter4j.*;
import twitter4j.http.*;

public class GrittR {

	public static String VERSION = "GrittR v0.1a";
	
	private static Twitter twitter;
	private static ExtendedUser user;
	private static AccessToken accessToken;
	
	/**************************************************************************
	twitter "wrapper" methods 
	**************************************************************************/
	
	
	static void getDirectMessages() {
		def messagesList;
		try {
			messagesList = twitter.getDirectMessages();
			println "Direct messages \n";
			messagesList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getSender().getScreenName() } \n${it.getText()} \n";
			}		
		} 
		catch(Exception ex) {
			println "Unable to get your direct messages ...sorry";
		}
		finally { System.exit(0); }
	}
	
	static void getSentDirectMessages() {
		def messagesList;
		try {
			println "Sent messages \n";
			messagesList = twitter.getSentDirectMessages();
			messagesList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getSender().getScreenName() } \n${it.getText()} \n";
			}		
		} 
		catch(Exception ex) {
			println "Unable to get your sent messages ...sorry";
		}
		finally { System.exit(0); }
	}
	
	static void getFavorites() {
		def statusList;
		try {
			statusList = twitter.getFavorites();
			println "Favorite tweets \n";
			displayStatusMessage(statusList);		
		} 
		catch(Exception ex) {
			println "Unable to get your favorite tweets ...sorry";
		}
		finally { System.exit(0); }
	}
	
	static void getFollowers() {
		def userList;
		try {
			println "Followers \n";
			userList = twitter.getFollowers(twitter.getUserId());
			userList.each  {
				println "${ it.getScreenName() }";
			}
		} 
		catch(Exception ex) {
			println "Unable to list your followers ... sorry";
		}
		finally { System.exit(0); }
	}
	
	static void getFriendsTimeline() {
		def statusList;
		try {
			statusList = twitter.getFriendsTimeline();
			println "Friends timeline \n";
			displayStatusMessage(statusList);
			
		}
		catch(Exception ex) {
			println "Unable to list your friend's timeline ... sorry";
		}
		finally { System.exit(0); }
	}
	
	static getMentions() {
		def statusList;
		try {
			statusList = twitter.getMentions();
			println "@${twitter.getUserId()} \n";
			displayStatusMessage(statusList);
		}
		catch(Exception ex) {
			println "Unable to list your mentions timeline ... sorry";
		}
		finally { System.exit(0); }
	}
	
	static void getUserTimeline() {
		def statusList;
		try {
			println "Your timeline \n";
			statusList = twitter.getUserTimeline(twitter.getUserId(), new Date());
			displayStatusMessage(statusList);
		}
		catch(Exception ex) {
			println "Unable to list your user timeline ... sorry";
		}
		finally { System.exit(0); }
	}
	
	static void updateStatus(String newStatus) {		
		if(newStatus.length() >= 139) 		
			newStatus = newStatus.substring(0, 139);

		def status;
		try {
			status = twitter.updateStatus(newStatus);
			println "Successfully updated your status to : ${status.getText()}" + 
				"- [ID: ${status.getId()}]";
		}
		catch(Exception ex) {
			println "Unable to update your status... sorry";
		}
		finally { System.exit(0); }
	}
	
	/**************************************************************************/
	
	static void authorizeClient() {
		twitter.setOAuthConsumer("JDy3FSsqzDpBYS9Xtvp1AA", "AftY58IPpEctiiIQpFaDuDuGKJHDbmnZGiyqrA12E"); 
    	RequestToken requestToken = twitter.getOAuthRequestToken();
    	AccessToken accessToken = null;
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	while (null == accessToken) {
      		println "Open the following URL and grant access to your account:";
      		println requestToken.getAuthorizationURL();
     		println "Hit enter when it's done.[Enter]:";
     		
     		br.readLine();
     		
       		try {
        		accessToken = requestToken.getAccessToken();
      		} 
      		catch (TwitterException te) {
        		if( 401 == te.getStatusCode()) {
          			println "Unable to get the access token.";
       			}
       			else {
          			te.printStackTrace();
        		}
      		}
    	}
    	
    	AccessToken at = loadAccessToken(user.getId());
    	
    	if(at == null) {
    		// save accessToken for future reference.
    		storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
    	}
    	else {
    		println "GrittR is authorized...";
    		twitter.setOAuthAccessToken(at.getToken(), at.getTokenSecret());
    		//
    		twitter.getOAuthRequestToken(at.getToken(), at.getTokenSecret());
    	}
  	}
  	
  	static void storeAccessToken(int userId, AccessToken at) {
    	def db, sb, stmt;
    	try {
    		db = new Database();
    		db.open("grittr.db", 0755);
    		
    		sb = new StringBuffer();
    		sb.append("create table if not exists tokens (");
    		sb.append("id integer primary key autoincrement, ");
    		sb.append("userid varchar(255) not null, ");
    		sb.append("token varchar(255) not null, ");
    		sb.append("tokenSecret varchar(255) not null)");
		
    		try {
    			stmt = db.prepare(sb.toString());
				while(stmt.step()) { }
			}
			catch(Exception ex) {
				println "Error creating tokens table ...sorry";
			}

			sb = new StringBuffer();
			sb.append("insert into tokens values (");
			sb.append("NULL, ");
			sb.append("'" + userId.toString() + "'");
			sb.append(", ");
			sb.append("'" + at.getToken() + "'");
			sb.append(", ");
			sb.append("'" + at.getTokenSecret() + "'");
			sb.append(")");
			
			println sb.toString();   
			
			try {
    			stmt = db.prepare(sb.toString());
				while(stmt.step()) { }
			}
			catch(Exception ex) {
				println "Error storing your access token ...sorry";
			}
    	}
    	catch(Exception ex) {
    		println "Unable to store access token ...sorry";
    	}
  	}
  	
  	static AccessToken loadAccessToken(int userId) {

    	def db, sb, stmt;
    	AccessToken accessToken = null;
    	String token, tokenSecret = null;
    	
    	try {
    		db = new Database();
    		db.open("grittr.db", 0755);
    		
    		sb = new StringBuffer();
    		sb.append("select * from tokens where userid = ");
    		sb.append("'" + userId.toString() + "'");
    		

    		try {
    			stmt = db.prepare(sb.toString());
    			while(stmt.step()) { 
    				stmt.column_count().times {
						if(stmt.column_origin_name(it).equalsIgnoreCase("token")) 
							token = stmt.column_string(it);
						
						if(stmt.column_origin_name(it).equalsIgnoreCase("tokenSecret")) 
							tokenSecret = stmt.column_string(it);
						
					}
    			}
    			accessToken = new AccessToken(token, tokenSecret);
    		}
    		catch(Exception ex) {
    			println "Error while retreiving your access token ...sorry";
    		}
    		
    	}
    	catch(Exception ex) {
    		println "Unable to retreive access token ...sorry";
    	}
    	return accessToken;
  	}

	/**************************************************************************/	
	
	static void displayStatusMessage(List<Status> statusList) { 
    	statusList.each {
    		println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${it.getUser().getScreenName()} [ID: ${it.getId()}]" + 
					"\n${it.getText()}\n";
    	}    	
    }
		
	static String helpString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append(GrittR.VERSION);
		sb.append("\n");
		sb.append("At least 3 arguments must be specified\n");
		sb.append("groovy GrittR username password method [(opt) arguments]\n\n");
		sb.append("Example usage :\n");
		sb.append("groovy GrittR username password direct\n");
		sb.append("groovy GrittR username password favorites\n");
		sb.append("groovy GrittR username password followers\n");
		sb.append("groovy GrittR username password friends\n");
		sb.append("groovy GrittR username password mentions\n");
		sb.append("groovy GrittR username password sent\n");
		sb.append("groovy GrittR username password update 'your new status'\n");
       return sb.toString();
	}
	
	static String humanDate(date) {
		return new SimpleDateFormat("yy/MM/dd").format(date);
	}

	/**************************************************************************/
    static void main(String[] args) {

        if(args.length < 3) {
        	println GrittR.helpString();
        	System.exit(0);
        }
        
        def username, password, method, arguments, argumentString;
        
        username = args[0];
        password = args[1];
        
        twitter = new Twitter(username, password);
        twitter.setClientURL("http://github.com/julien/grittr/tree/master");
		twitter.setClientVersion("1.0");
		twitter.setUserAgent("GrittR");

        try {
        	user = twitter.verifyCredentials();
        	accessToken = loadAccessToken(user.getId());
        	if(accessToken != null) {
        		twitter.setOAuthAccessToken(accessToken);
        		twitter.setSource("GrittR");  	
        	}
        	
        } 
        catch(Exception ex) {
        	println "Failed to log in to twitter, check your credentials";
        	System.exit(0);
        }
        
        method = args[2]
        arguments = new String[args.length - 3];
        System.arraycopy(args, 3, arguments, 0, args.length - 3);
        argumentString = arguments.join(" ");
        
        // println "method : ${method}, args : ${argumentString}";  
        
        switch(method) {
        	case "authorize":
        		authorizeClient();
        		break;
        
        	case "direct":
        		getDirectMessages();
        		break;
        		
        	case "favorites":
        		getFavorites();
        		break;
  
        	case "followers":
        		getFollowers();
        		break;
        	
        	case "friends":
        		getFriendsTimeline();
        		break;
        		
        	case "mentions":
        		getMentions();
        		break;
        		
        	case "sent":
        		getSentDirectMessages();
        		break;
        
        	case "update":
        		updateStatus(argumentString);
        		break;
        		
        	case "user":
        		getUserTimeline();
        		break;
        		
        	default:
        		println GrittR.helpString();
        		break;
        }
    }
    
    /**************************************************************************/
    

}








