import java.io.*;
class Main{
	public static void main(String[] args) throws IOException{
		final WelcomeInterface wi = new WelcomeInterface();
		wi.show();
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                if(wi.ui != null){
                	while(true){
             			System.out.println("Trying to logout....");
              			boolean done = Connection.logout(wi.ui.userName);
						if(done){
							System.out.println("Logged out.");
							break;
						}
                	}
                }
            }
        });
	}
}