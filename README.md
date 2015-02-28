INTRUCTIONS
===========

There are two folders:

- AuthenticationServer/
- App/

Steps
-----

- Compile AuthenticationServer/AuthenticationServer.java and set it running. This will act as main server. Note down its IP.
	The steps are:

	- cd AuthenticationServer/
	- javac AuthenticationServer.java
	- java AuthenticationServer.java

- Next goto App/. In Connection.java, set the above noted IP as the value of `serverIP` variable. From the dll/ folder, copy the required .dll from x64/x86 depending upon whether your system is 64-bit/32-bit. Then compile all the .java files in App/.
	The steps are:

	- cd App/
	- In Connection.java: static final String serverIP = "Server IP Address";
	- cp dll/x64/opencv_java2410.dll opencv_java2410.dll or cp dll/x86/opencv_java2410.dll opencv_java2410.dll
	- javac \*.java

- Run the application and enjoy.
	The steps are:

	- java Main