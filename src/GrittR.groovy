import twitter4j.Paging;
import twitter4j.Twitter;

class GrittR {
	
	private static twitter;
	
	static void update(newStatus) {
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
	
	static void getFriendsTimeline() {
		def statusList;
		try {
			statusList = twitter.getFriendsTimeline();
			statusList.each  {
				println "${ it.getCreatedAt() } - ${ it.getUser().getScreenName() } : ${ it.getText() }";
				println " ";
			}
		}
		catch(Exception ex) {
			println "Unable to list your friend's timeline ... sorry";
		}
		finally { System.exit(0); }
	}
	
	static void getFollowers() {
		def userList;
		try {
			userList = twitter.getFollowers(new Paging(20));
			userList.each  {
				println "${ it.getScreenName() }";
				println " ";
			}
		} 
		catch(Exception ex) {
			println "Unable to list your followers ... sorry";
		}
		finally { System.exit(0); }
	}
	
    static main(String[] args) {
        
        if(args.length < 3) {
        	println "At least 3 arguments must be specified : username password method [(opt) arguments]";
        	System.exit(0);
        }
        
        def username, password, method, arguments, argumentString;
        
        username = args[0];
        password = args[1];
        
        // first thing is to verify the credentials
        twitter = new Twitter(username, password);
        twitter.setClientVersion("1");
        twitter.setUserAgent("GrittR");
        twitter.setClientURL("http://punkscum.org");

        try {
        	twitter.verifyCredentials();
        } 
        catch(Exception ex) {
        	println "Failed to log in to twitter, check your credentials";
        	System.exit(0);
        }
        
        // check which method the user wants to call
        method = args[2]
        arguments = new String[args.length - 3];
        System.arraycopy(args, 3, arguments, 0, args.length - 3);
        argumentString = arguments.join(" ");
        // println "method : ${method}, args : ${argumentString}";
        
        switch(method) {
        	case "followers":
        		getFollowers();
        		break;
        	
        	case "friends":
        		getFriendsTimeline();
        		break;
        
        	case "update":
        		update(argumentString);
        		break;
        }
    }
}



