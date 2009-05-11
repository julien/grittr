import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.Status;

class GrittR {

	public static String VERSION = "GrittR v0.1a";
	
	private static Twitter twitter;
	
	/**************************************************************************
	twitter "wrapper" methods 
	**************************************************************************/
	
	/** not working right now
	static void getCurrentTrends() {
		def statusList;
		try {
			statusList = twitter.getCurrentTrends();
			statusList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getUser().getScreenName() } \n" + 
					"${it.getText()} \n"; 
			}
		}
		catch(Exception ex) {
			println "Unable to get current trends ...sorry";
		}
		finally { System.exit(0); }
	}
	
	static void getDailyTrends() {
		def statusList;
		try {
			statusList = twitter.getDailyTrends();
			statusList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getUser().getScreenName() } \n" + 
					"${it.getText()} \n"; 
			}
		}
		catch(Exception ex) {
			println "Unable to get daily trends ...sorry";
		}
		finally { System.exit(0); }
	}
	*/
	
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
			statusList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getUser().getScreenName() } \n${it.getText()} \n";
			}		
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
			println "Friends messages \n";
			statusList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getUser().getScreenName() } \n${it.getText()} \n";
			}
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
			statusList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getUser().getScreenName() } \n${it.getText()} \n";
			}
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
			statusList.each  {
				println "${GrittR.humanDate(it.getCreatedAt())} " + 
					"${ it.getUser().getScreenName() } \n${it.getText()} \n"; 
			}
		}
		catch(Exception ex) {
			println "Unable to list your user timeline ... sorry";
		}
		finally { System.exit(0); }
	}
	
	static void updateStatus(String newStatus) {
		// make sure we don't send more than 140 chars
		newStatus = newStatus.substring(0, 139);
		def status;
		try {
			status = twitter.updateStatus(newStatus);
			println "Successfully updated your status to : ${status.getText()}";
		}
		catch(Exception ex) {
			println "Unable to update your status... sorry";
		}
		finally { System.exit(0); }
	}
	
	/**************************************************************************/	
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
        	
        	/*
        	case "current":
        		getCurrentTrends();
        		break;
        	
        	case "daily":
        		getDailyTrends();
        		break;
        	*/
        	
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
        }
    }
}








