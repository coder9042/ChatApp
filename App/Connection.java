import java.io.*;
import java.util.*;
import java.net.*;
class Connection{
	static final String serverIP = "172.16.11.214";
	static final int serverPort = 6789;
	static String error = "";
	static String response = "";
	private static synchronized Socket connectToServer(){
		Socket socket = null;
		try{
			socket = new Socket(serverIP, serverPort);
		}
		catch(IOException e){
			e.printStackTrace();
		}

		return socket;
	}
	public static synchronized boolean signUp(String email, String username, String password){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "1," + email + "," + username + "," + password + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Success") == 0)
					return true;
				else{
					error = response;
					return false;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean login(String username, String password){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "2," + username + "," + password + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Success") == 0)
					return true;
				else{
					error = response;
					return false;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean logout(String username){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "7," + username + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Success") == 0)
					return true;
				else{
					error = response;
					return false;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			error = "Excpetion occured.";
			return false;
		}
	}
	public static  synchronized boolean checkLoggedIn(String username){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "8," + username + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Success") == 0)
					return true;
				else{
					error = response;
					return false;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			error = "Excpetion occured.";
			return false;
		}
	}
	public static synchronized boolean getFriendList(String username){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "3," + username + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				return true;
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean getGroupList(String username){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "9," + username + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				return true;
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean addFriend(String username, String friend){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "4," + username + "," + friend + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Success") == 0){
					return true;
				}
				else{
					error = response;
					return false;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean addGroup(String username, String name){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "10," + username + "," + name + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Success") == 0){
					return true;
				}
				else{
					error = response;
					return false;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean addGroupMember(String groupName, ArrayList<String> friendName){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "11," + groupName;
				for(String f: friendName){
					outText += "," + f;
				}
				outText += "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Success") == 0){
					return true;
				}
				else{
					error = response;
					return false;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean getGroupMembers(String groupName){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "12," + groupName + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Failed") == 0){
					error = response;
					return false;
				}
				else{
					return true;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean getIpAddress(String username){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "5," + username + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Failed") == 0){
					error = response;
					return false;
				}
				else{
					return true;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public static synchronized boolean getUserName(String ipAddress){
		error = "";
		Socket socket = connectToServer();
		try{
			if(socket == null){
				error = "Error in connecting to the server...";
				return false;
			}
			else{
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String outText = "6," + ipAddress + "\n";
				outToServer.writeBytes(outText);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = inFromServer.readLine();
				socket.close();
				if(response.compareTo("Failed") == 0){
					error = response;
					return false;
				}
				else{
					return true;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
}