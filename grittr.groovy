
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import SQLite.Database;
import SQLite.Stmt;

import twitter4j.*;
import twitter4j.http.*;


public class GrittR {

	public static String VERSION = "GrittR 1.0";
	
	private static Twitter twitter;
	private static ExtendedUser user;
	private static AccessToken accessToken;
	private static String method;
	private static String username;
	private static String password;
	private static String[] arguments;
	private static String argStr;
	
	
	/**************************************************************************
	twitter "wrapper" methods 
	**************************************************************************/

	public static void getDirectMessages() {
		def messagesList;
		try {
			messagesList = twitter.getDirectMessages();
			println AinsiColors.MAJENTA_ON_DEFAULT + 
				"\nDirect messages \n" + 
				AinsiColors.NORMAL;
			messagesList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${it.getSender().getScreenName()} \n${it.getText()} \n";
			}		
		} 
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to get your direct messages ...sorry" + 
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	public static void getSentDirectMessages() {
		def messagesList;
		try {
			println AinsiColors.MAJENTA_ON_DEFAULT + 
				"\nSent messages \n" + 
				AinsiColors.NORMAL;
			messagesList = twitter.getSentDirectMessages();
			messagesList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getSender().getScreenName() } \n${it.getText()} \n";
			}		
		} 
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to get your sent messages ...sorry" + 
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	public static void getFavorites() {
		def statusList;
		try {
			statusList = twitter.getFavorites();
			println AinsiColors.MAJENTA_ON_DEFAULT + 
				"\nFavorite tweets \n" + 
				AinsiColors.NORMAL;
			displayStatusMessage(statusList);		
		} 
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to get your favorite tweets ...sorry" +
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	public static void getFollowers() {
		def userList;
		try {
			println AinsiColors.MAJENTA_ON_DEFAULT + 
				"\nFollowers \n" +
				AinsiColors.NORMAL;
			userList = twitter.getFollowers(twitter.getUserId());
			userList.each  {
				println "${ it.getScreenName() }";
			}
		} 
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to list your followers ... sorry" +
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	public static void getFriendsTimeline() {
		def statusList;
		try {
			statusList = twitter.getFriendsTimeline();
			println AinsiColors.MAJENTA_ON_DEFAULT + 
				"\nFriends timeline \n" + 
				AinsiColors.NORMAL;
			displayStatusMessage(statusList);
			
		}
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to list your friend's timeline ... sorry" + 
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	public static getMentions() {
		def statusList;
		try {
			statusList = twitter.getMentions();
			println AinsiColors.MAJENTA_ON_DEFAULT + 
				"\n@${twitter.getUserId()} \n" + 
				AinsiColors.NORMAL;
			displayStatusMessage(statusList);
		}
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to list your mentions timeline ... sorry" + 
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	static void getPublicTimeline() {
		def statusList;
		try {
			println AinsiColors.MAJENTA_ON_DEFAULT +  
				"\nPublic timeline \n" + 
				AinsiColors.NORMAL;
			statusList = twitter.getPublicTimeline();
			displayStatusMessage(statusList);
		}
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to list public timeline ... sorry" +
				AinsiColors.NORMAL;
				
			ex.printStackTrace();
		}
		finally { System.exit(0); }
	}
	
	public static void getUserTimeline() {
		def statusList;
		try {
			println AinsiColors.MAJENTA_ON_DEFAULT +  
				"\nYour timeline \n" + 
				AinsiColors.NORMAL;
			statusList = twitter.getUserTimeline(twitter.getUserId(), new Date());
			displayStatusMessage(statusList);
		}
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT + 
				"Unable to list your user timeline ... sorry" + 
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	static void updateStatus(String newStatus) {		
		if(newStatus.length() >= 139) 		
			newStatus = newStatus.substring(0, 139);

		def status;
		try {
			status = twitter.updateStatus(newStatus);
			println AinsiColors.CYAN_ON_DEFAULT +  
				"\nSuccessfully updated your status to : ${status.getText()}" + 
				" - [ID: ${status.getId()}]" + 
				AinsiColors.NORMAL;
		}
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT +  
				"Unable to update your status... sorry" +
				AinsiColors.NORMAL;
		}
		finally { System.exit(0); }
	}
	
	/**************************************************************************/
	
	public static void authenticateUser(String username, String password) {
		if(username == null || password == null) {
			println AinsiColors.RED_ON_DEFAULT + 
				"You must supply your USERNAME and " + 
				"PASSWORD for the method : ${method}" +
				AinsiColors.NORMAL;
			System.exit(1);
		}
		 		
		println "Logging in as ${username}";
		
		try {
			twitter = new Twitter(username, password);
			
			user = twitter.verifyCredentials();
			println "Logged in as ${user.getScreenName()}";
			
			try {
				AccessToken at = loadAccessToken(user.getId());
				if(at != null)
					twitter.setOAuthAccessToken(at);
					
				customizeClient(twitter);
			} 
			catch (Exception ex) {
				ex.printStackTrace();
			}				
		} 
		catch(Exception ex) {
			println AinsiColors.RED_ON_DEFAULT + 
				"Unable to verify your twitter credentials ...sorry" +
				AinsiColors.NORMAL;
			System.exit(1);
		}
	}
	
	// TODO : implement oauth support...
	
	public static void authorizeClient() {
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
          			println "Unable to get the access token." + 
          				AinsiColors.NORMAL;
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
    		// twitter.setOAuthAccessToken(at.getToken(), at.getTokenSecret());
    		// accessToken = loadAccessToken(user.getId());
        	twitter.setOAuthAccessToken(at);	
    		println "GrittR is authorized...";
    	}
  	}
  	
  	public static void storeAccessToken(int userId, AccessToken at) {
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
				println "Error creating tokens table ...sorry" + 
					AinsiColors.NORMAL;
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
			try {
    			stmt = db.prepare(sb.toString());
				while(stmt.step()) { }
			}
			catch(Exception ex) {
				println "Error storing your access token ...sorry" + 
					AinsiColors.NORMAL;
			}
    	}
    	catch(Exception ex) {
    		println "Unable to store access token ...sorry" +
    			AinsiColors.NORMAL;
    	}
  	}
  	 
  	public static AccessToken loadAccessToken(int userId) {
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
    			println "Error while retreiving your access token ...sorry" +
    				AinsiColors.NORMAL;
    		}
    		
    	}
    	catch(Exception ex) {
    		println "Unable to retreive access token ...sorry" +
    			AinsiColors.NORMAL;
    	}
    	return accessToken;
  	}
  	

	/**************************************************************************/	
	
	public static void displayStatusMessage(List<Status> statusList) { 
    	statusList.each {
    		println "${AinsiColors.CYAN_ON_DEFAULT}" + 
    				"${GrittR.humanDate(it.getCreatedAt())} " + 
    				"${AinsiColors.GREEN_ON_DEFAULT} " + 
					"${it.getUser().getScreenName()} " +
					"${AinsiColors.MAJENTA_ON_DEFAULT} "  + 
					"[ID: ${it.getId()}]" + 
					"${AinsiColors.BLUE_ON_DEFAULT} " + 
					"\n${it.getText()}\n" +
					"${AinsiColors.NORMAL} ";
    	}    	
    }
		
	public static String helpString() {
		StringBuffer sb = new StringBuffer();
		sb.append(AinsiColors.GREEN_ON_DEFAULT);
		sb.append("\n");
		sb.append(GrittR.VERSION);
		sb.append("\n");
		sb.append("groovy grittr method [(opt) arguments]\n\n");
		sb.append("Example usage :\n");
		sb.append("groovy grittr about \n");
		sb.append("groovy grittr public \n");
		sb.append("groovy grittr direct USERNAME PASSWORD \n");
		sb.append("groovy grittr favorites USERNAME PASSWORD \n");
		sb.append("groovy grittr followers USERNAME PASSWORD \n");
		sb.append("groovy grittr friends USERNAME PASSWORD \n");
		sb.append("groovy grittr mentions USERNAME PASSWORD \n");
		sb.append("groovy grittr sent USERNAME PASSWORD \n");
		sb.append("groovy grittr update USERNAME PASSWORD 'your new status' \n");
		sb.append("groovy grittr user USERNAME PASSWORD \n");
		sb.append(AinsiColors.NORMAL);
		
       return sb.toString();
	}
	
	
	public static String parseMethod(String method) {
		// some methods need "authentication", others dont
		switch(method) {
        	case "about":
        		printAbout();
        		break;
        	
        	case "public":
        		getPublicTimeline();
        		break;
                	
        	case "authorize":
        		authenticateUser(username, password);
        		authorizeClient();
        		break;
        	        	
        	case "direct":
        		authenticateUser(username, password);
        		getDirectMessages();
        		break;
        		
        	case "favorites":
        		authenticateUser(username, password);
        		getFavorites();
        		break;
  
        	case "followers":
        		authenticateUser(username, password);
        		getFollowers();
        		break;
        	
        	case "friends":
        		authenticateUser(username, password);
        		getFriendsTimeline();
        		break;
        		
        	case "mentions":
        		authenticateUser(username, password);
        		getMentions();
        		break;
        		
        	case "sent":
        		authenticateUser(username, password);
        		getSentDirectMessages();
        		break;
        
        	case "update":
        		authenticateUser(username, password);
        		updateStatus(argStr);
        		break;
        		
        	case "user":
        		authenticateUser(username, password);
        		getUserTimeline();
        		break;
        		
        	default:
        		println GrittR.helpString();
        		break;
    	}
	}
	
	public static String humanDate(date) {
		return new SimpleDateFormat("yy/MM/dd").format(date);
	}
	
	public static void customizeClient(Twitter twitter) {
		twitter.setClientURL("http://github.com/julien/grittr/tree/master");
		twitter.setClientVersion("1.0");
		twitter.setSource("grittr");  	
		twitter.setUserAgent("grittr");
	}
	
	public static void printAbout() {
		println AinsiColors.GREEN_ON_DEFAULT + 
		    "url : ${twitter.getClientURL()}\n" +
        	"version : ${twitter.getClientVersion()}\n" +
        	"source : ${twitter.getSource()}\n" +
        	"user agent : ${twitter.getUserAgent()}" +
        	AinsiColors.NORMAL;
	}
	
	
	/**************************************************************************/
    public static void main(String[] args) {
		
		if(args.length > 0) {
			method = args[0];
			
			if (args.length > 1) 
				username = args[1];
			else
			 	username = "";
			
			if(args.length > 2) 
				password = args[2];
			else
				password = null;
				
			
    		if(args.length > 3) {
    			arguments = new String[args.length - 3];
    			System.arraycopy(args, 3, arguments, 0, args.length - 3);
        		argStr = arguments.join(" ");
        	}
        	
        	twitter = new Twitter(); 
        	customizeClient(twitter);
        	        	
			parseMethod(method);
			// println "${AinsiColors.GREEN_ON_DEFAULT}" + 
			// "method : ${method}, args : ${argStr}${AinsiColors.NORMAL}"; 
        } 
        else {
        	println GrittR.helpString();
        	System.exit(1);        	
        }
		
		// TODO : implement oauth support... 
        // accessToken = loadAccessToken(user.getId());
        // if(accessToken != null) 
        // 	twitter.setOAuthAccessToken(accessToken); 
    }
    
    /**************************************************************************/
}


/* a simple utility class to print 
  out "colored" messages in the console" */
class AinsiColors {
    public static String NORMAL =  "\u001b[0m";
	public static String REVERSE = "\u001b[7m";
	// colors on "default" console background
	public static String BLUE_ON_DEFAULT =    "\u001b[34;49m";
	public static String CYAN_ON_DEFAULT =    "\u001b[36;49m";
	public static String GREEN_ON_DEFAULT =   "\u001b[32;49m";
	public static String MAJENTA_ON_DEFAULT = "\u001b[35;49m";
	public static String RED_ON_DEFAULT =     "\u001b[31;49m";
	public static String YELLOW_ON_DEFAULT =  "\u001b[36;49m";
    public static String WHITE_ON_DEFAULT =   "\u001b[37;49m";
}


