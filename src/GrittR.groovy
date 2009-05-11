
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.Status;
import twitter4j.http.AccessToken;


public class GrittR {

	public static String VERSION = "GrittR v0.1a";
	
	private static Twitter twitter;
	
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
				"- [ID: ${status.getId()}";
		}
		catch(Exception ex) {
			println "Unable to update your status... sorry";
		}
		finally { System.exit(0); }
	}
	
	static AccessToken getOAuthAccessToken() {
		try {
			twitter.getOAuthAccessToken("JDy3FSsqzDpBYS9Xtvp1AA", "AftY58IPpEctiiIQpFaDuDuGKJHDbmnZGiyqrA12E");
			println "Authorized...";	
		} catch(Exception ex) {
			println "Unable to get oAuth request token ... sorry";
		}
		finally { System.exit(0); }
	}
	
	
	static void setOAuthAccessToken(String token, String tokenSecret) {
		twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));
		
		getOAuthAccessToken(token, tokenSecret);
		
		/*
	 *   Consumer key
      JDy3FSsqzDpBYS9Xtvp1AA
    	* Consumer secret
      AftY58IPpEctiiIQpFaDuDuGKJHDbmnZGiyqrA12E
    * Request token URL
      http://twitter.com/oauth/request_token
    * Access token URL
      http://twitter.com/oauth/access_token
    * Authorize URL
      http://twitter.com/oauth/authorize *We support hmac-sha1 signatures. We do not support the plaintext signature method.

		
		*/
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
		twitter.setOAuthAccessToken("JDy3FSsqzDpBYS9Xtvp1AA", "AftY58IPpEctiiIQpFaDuDuGKJHDbmnZGiyqrA12E"); 
		twitter.setSource("GrittR");  
		twitter.setUserAgent("GrittR");
        // getOAuthAccessToken();
        
        try {
        	twitter.verifyCredentials();
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
        	/*case "authorize":
        		setOAuthAccessToken(username, password);
        		break;*/
        
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








