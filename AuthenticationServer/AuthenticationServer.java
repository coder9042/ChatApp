import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
class AuthenticationServer{
	static String error = "";
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = new ServerSocket(6789);
		String ipAddress = serverSocket.getInetAddress().getLocalHost().toString();
		System.out.println("Server running at " + ipAddress + ":6789");
		while(true){
			Socket connectionSocket = serverSocket.accept();
			String clientIP = connectionSocket.getInetAddress().toString();
			System.out.println("Connection accepted from " + clientIP + ":" + connectionSocket.getPort());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String input = inFromClient.readLine();
			if(input.charAt(0) == '1'){
				boolean value = signUp(input);
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				if(value)
					outToClient.writeBytes("Success\n");
				else
					outToClient.writeBytes(error+"\n");
			}
			else if(input.charAt(0) == '2'){
				boolean value = login(input, clientIP);
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				if(value)
					outToClient.writeBytes("Success\n");
				else
					outToClient.writeBytes(error+"\n");
			}
			else if(input.charAt(0) == '3'){
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes(friendList(input) + "\n");
			}
			else if(input.charAt(0) == '4'){
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				boolean value = addFriend(input);
				if(value)
					outToClient.writeBytes("Success\n");
				else
					outToClient.writeBytes(error+"\n");
			}
			else if(input.charAt(0) == '5'){
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes(getIP(input) + "\n");
			}
			else if(input.charAt(0) == '6'){
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes(getUserName(input) + "\n");
			}
			else if(input.charAt(0) == '7'){
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				if(logout(input))
					outToClient.writeBytes("Success\n");
				else
					outToClient.writeBytes(error+"\n");
			}
			else if(input.charAt(0) == '8'){
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				if(isLoggedIn(input))
					outToClient.writeBytes("Success\n");
				else
					outToClient.writeBytes(error+"\n");
			}
			connectionSocket.close();
		}
	}
	private static String sha1(String input){
		MessageDigest mDigest;
		StringBuffer sb = new StringBuffer();
		try{
			mDigest = MessageDigest.getInstance("SHA1");
			byte[] result = mDigest.digest(input.getBytes());
			for (int i = 0; i < result.length; i++) {
			    sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
			}
		}
		catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
        return sb.toString();
    }
	private static boolean signUp(String input){
		try{
			String data[] = input.split(",");
			String email = data[1];
			String username = data[2];
			String password = data[3];

			File usrFile = new File("data/usernames.txt");
			File emailFile = new File("data/emails.txt");

			// Checking for duplicates

			BufferedReader in1 = new BufferedReader(new FileReader(usrFile));
			BufferedReader in2 = new BufferedReader(new FileReader(emailFile));
			String line = null;
			while((line = in2.readLine()) != null){
				if(line.compareTo(email) == 0){
					error = "Email Id already registered.";
					return false;
				}
			}
			while((line = in1.readLine()) != null){
				if(line.compareTo(username) == 0){
					error = "Username already in use.";
					return false;
				}
			}
			in1.close();
			in2.close();

			// Making new records

			PrintWriter p1 = new PrintWriter(new BufferedWriter(new FileWriter(usrFile, true)));
			PrintWriter p2 = new PrintWriter(new BufferedWriter(new FileWriter(emailFile, true)));
			p1.println(username);
			p1.close();
			p2.println(email);
			p2.close();

			File newFile = new File("data/users/"+username+".txt");
			PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
			p.println(email);
			p.println(username);
			p.println(sha1(password));
			p.close();

			File newFile2 = new File("data/users/"+username+"_friends.txt");
			p = new PrintWriter(new BufferedWriter(new FileWriter(newFile2)));
			p.close();

			File newFile3 = new File("data/users/"+username+"_status.txt");
			p = new PrintWriter(new BufferedWriter(new FileWriter(newFile3)));
			p.println(0);
			p.close();
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			error = "Server Error";
			return false;
		}
	}
	public static boolean login(String input, String clientIP){
		try{
			String data[] = input.split(",");
			String username = data[1];
			String password = sha1(data[2]);

			File usrFile = new File("data/usernames.txt");
			File emailFile = new File("data/emails.txt");

			// Checking for existence

			BufferedReader in1 = new BufferedReader(new FileReader(usrFile));
			BufferedReader in2 = new BufferedReader(new FileReader(emailFile));
			String line = null;
			boolean flag = false;
			int flag2 = 0;
			while((line = in1.readLine()) != null){
				if(line.compareTo(username) == 0){
					flag = true;
					break;
				}
			}
			while(!flag && (line = in2.readLine()) != null){
				flag2++;
				if(line.compareTo(username) == 0){
					flag = true;
					break;
				}
			}
			in1.close();
			in2.close();
			if(flag2 > 0){
				in1 = new BufferedReader(new FileReader(usrFile));
				while((line = in1.readLine()) != null){
					flag2 --;
					if(flag2 == 0){
						username = line;
						break;
					}
				}
			}
			if(!flag){
				error = "Username/Email doesn't exists.";
				return false;
			}

			// Checking password

			File myFile = new File("data/users/"+username+".txt");

			BufferedReader in = new BufferedReader(new FileReader(myFile));
			int c = 0;
			while((line = in.readLine()) != null){
				c++;
				if(c == 3){
					if(line.compareTo(password) != 0){
						error = "Incorrect Password.";
						return false;
					}
				}
			}
			in.close();

			File myFile3 = new File("data/users/"+username+"_status.txt");

			PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(myFile3)));
			p.println(1);
			p.close();


			p = new PrintWriter(new BufferedWriter(new FileWriter(myFile, true)));
			p.println(clientIP);
			p.close();

			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			error = "Server Error";
			return false;
		}
	}
	private static boolean isLoggedIn(String input){
		String data[] = input.split(",");
		String username = data[1];

		File myFile3 = new File("data/users/"+username+"_status.txt");

		try{
			BufferedReader in = new BufferedReader(new FileReader(myFile3));
			String line = in.readLine();
			in.close();
			if(line.equals("1"))
				return true;
			else
				return false;
		}
		catch(IOException e){
			e.printStackTrace();
			return true;
		}
	}
	private static boolean logout(String input){
		String data[] = input.split(",");
		String username = data[1];
		File myFile3 = new File("data/users/"+username+"_status.txt");
		try{
			PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(myFile3)));
			p.println(0);
			p.close();
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	private static String friendList(String input){
		String data[] = input.split(",");
		String username = data[1];
		
		File myFile = new File("data/users/"+username+"_friends.txt");
		String res = "";

		try{
			BufferedReader in = new BufferedReader(new FileReader(myFile));
			String line = in.readLine();
			res = line;
			while((line = in.readLine()) != null){
				res += "," + line;
			}
			in.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return res;
	}
	private static boolean addFriend(String input){
		String data[] = input.split(",");
		String username = data[1];
		String friendName = data[2];

		File usrFile = new File("data/usernames.txt");
		BufferedReader in;
		PrintWriter p;
		String line = null;
		try{
			in = new BufferedReader(new FileReader(usrFile));
			boolean flag = false;
			while((line = in.readLine()) != null){
				if(line.compareTo(friendName) == 0){
					flag = true;
					break;
				}
			}
			in.close();
			if(!flag){
				error = "Username '"+ friendName +"' not found.";
				return false;
			}

			File myFile = new File("data/users/"+username+"_friends.txt");

			flag = false;
			line = null;
			in = new BufferedReader(new FileReader(myFile));
			while((line = in.readLine()) != null){
				if(line.compareTo(friendName) == 0){
					flag = true;
					break;
				}
			}
			in.close();

			if(flag){
				error = "Already in friend list.";
				return false;
			}

			p = new PrintWriter(new BufferedWriter(new FileWriter(myFile, true)));
			p.println(friendName);
			p.close();

			return true;

		}
		catch(IOException e){
			e.printStackTrace();
			error = "Server Error.";
			return false;
		}
	}
	private static String getIP(String input){
		String data[] = input.split(",");
		String username = data[1];

		File myFile = new File("data/users/"+username+".txt");
		BufferedReader in;
		try{
			in = new BufferedReader(new FileReader(myFile));
			String lastLine = null;
			String line = null;
			int c = 0;
			while((line = in.readLine()) != null){
				c++;
				if(c > 3){
					lastLine = line;
				}
			}
			in.close();
			if(lastLine == null || lastLine.equals(""))
				return "Failed";
			return lastLine.substring(1);
		}
		catch(IOException e){
			e.printStackTrace();
			return "Failed";
		}
	}
	private static String getUserName(String input){
		String data[] = input.split(",");
		String ipAddress = data[1].substring(1);

		File myFile = new File("data/usernames.txt");
		BufferedReader in;
		try{
			in = new BufferedReader(new FileReader(myFile));
			String result = null;
			String line = null;
			while((line = in.readLine()) != null){
				String ip = getIP("5,"+line);
				if(ip.compareTo(ipAddress) == 0){
					result = line;
					break;
				}
			}
			in.close();
			if(result == null || result.equals(""))
				return "Failed";
			return result;
		}
		catch(IOException e){
			e.printStackTrace();
			return "Failed";
		}
	}
}