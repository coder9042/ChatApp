import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
class UserInterface{
	String userName;
	String password;
	JFrame frame;
	JTabbedPane chatTabbedPane;
	JTextField typeArea;
	JButton audioButton;
	JButton videoButton;
	JPanel inputPanel;

	JPanel friendsArea;
	JPanel friendsConnectPanel;
	JScrollPane friendsConnectScroller;
	JTextField searchField;
	JButton searchButton;
	JPanel searchPanel;

	JPanel groupArea;
	JPanel groupConnectPanel;
	JScrollPane groupConnectScroller;
	JTextField groupAddField;
	JButton groupAddButton;
	JPanel groupSearchPanel;

	JTextField statusField;
	Font font;
	Font font2;

	ArrayList<String> friendList;
	ArrayList<Group> groupList;

	AudioControlUI audioUI;

	ChatServer myServer;
	GroupChatServer myGroupServer;
	ArrayList<Client> clientsForTabs = new ArrayList<Client>();
	ArrayList<Boolean> clientClosedStatus = new ArrayList<Boolean>();

	static final String placeholder = "Type in your text here and press Enter to send.";
	public UserInterface(String userName, String password){
		this.userName = userName;
		this.password = password;
		myServer = new ChatServer(this);
		myGroupServer = new GroupChatServer(this);
	}
	public void show() {
		frame = new JFrame("CHAT APP -- " + userName);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(750, 500);
		frame.setResizable(false);

		font = new Font("Cambria", Font.BOLD, 14);
		font2 = new Font("Callibri", Font.PLAIN, 14);

		chatTabbedPane = new JTabbedPane();
		chatTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		//addTab("1");
		//addTab("2");

		typeArea = new JTextField(80);
		typeArea.setText(placeholder);
		typeArea.addFocusListener(new TypeAreaListener());
		typeArea.addKeyListener(new TypeAreaListener2());
		//typeArea.setLineWrap(true);
		typeArea.setFont(font2);
		typeArea.setBorder(BorderFactory.createEmptyBorder());

		audioButton = new JButton("", new ImageIcon("audio.png"));
		audioButton.setFocusPainted(false);
		audioButton.addActionListener(new AudioButtonListener());
		videoButton = new JButton("", new ImageIcon("video.png"));
		videoButton.setFocusPainted(false);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180), 2));
		buttonPanel.add(audioButton);
		buttonPanel.add(videoButton);

		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));

		inputPanel.add(typeArea);
		inputPanel.add(buttonPanel);

		JTextField friendsTitle = new JTextField(15);
		friendsTitle.setMaximumSize(new Dimension(250,20));
		friendsTitle.setText("FRIEND LIST");
		friendsTitle.setEditable(false);
		friendsTitle.setBackground(Color.WHITE);
		friendsTitle.setForeground(new Color(109, 132, 180));
		friendsTitle.setHorizontalAlignment(JTextField.CENTER);
		friendsTitle.setBorder(BorderFactory.createEmptyBorder());
		friendsTitle.setFont(font);


		searchField = new JTextField(10);
		searchField.setFont(font2);
		searchButton = new JButton("Add");
		searchButton.setFocusPainted(false);
		searchButton.setBackground(new Color(109, 132, 180));
		searchButton.setForeground(Color.WHITE);
		searchButton.addActionListener(new AddFriendListener());
		searchPanel = new JPanel();
		searchPanel.setMaximumSize(new Dimension(350, 40));
		searchPanel.setBackground(Color.WHITE);
		searchPanel.setBorder(BorderFactory.createEmptyBorder());
		searchPanel.add(searchField);
		searchPanel.add(searchButton);

		friendsConnectPanel = new JPanel();
		friendsConnectPanel.setLayout(new BoxLayout(friendsConnectPanel, BoxLayout.Y_AXIS));
		friendsConnectPanel.setBackground(Color.WHITE);
		friendsConnectPanel.setBorder(BorderFactory.createEmptyBorder());
		friendsConnectScroller = new JScrollPane(friendsConnectPanel);
		friendsConnectScroller.setBorder(BorderFactory.createEmptyBorder());
		showFriendList();

		JTextField groupsTitle = new JTextField(15);
		groupsTitle.setMaximumSize(new Dimension(250,20));
		groupsTitle.setText("MY GROUPS");
		groupsTitle.setEditable(false);
		groupsTitle.setBackground(Color.WHITE);
		groupsTitle.setForeground(new Color(109, 132, 180));
		groupsTitle.setHorizontalAlignment(JTextField.CENTER);
		groupsTitle.setBorder(BorderFactory.createEmptyBorder());
		groupsTitle.setFont(font);

		groupAddField = new JTextField(10);
		groupAddField.setFont(font2);
		groupAddButton = new JButton("Create");
		groupAddButton.setFocusPainted(false);
		groupAddButton.setBackground(new Color(109, 132, 180));
		groupAddButton.setForeground(Color.WHITE);
		groupAddButton.addActionListener(new CreateGroupListener());
		groupSearchPanel = new JPanel();
		groupSearchPanel.setMaximumSize(new Dimension(350, 40));
		groupSearchPanel.setBackground(Color.WHITE);
		groupSearchPanel.setBorder(BorderFactory.createEmptyBorder());
		groupSearchPanel.add(groupAddField);
		groupSearchPanel.add(groupAddButton);

		groupConnectPanel = new JPanel();
		groupConnectPanel.setLayout(new BoxLayout(groupConnectPanel, BoxLayout.Y_AXIS));
		groupConnectPanel.setBackground(Color.WHITE);
		groupConnectPanel.setBorder(BorderFactory.createEmptyBorder());
		groupConnectScroller = new JScrollPane(groupConnectPanel);
		groupConnectScroller.setBorder(BorderFactory.createEmptyBorder());
		showGroupList();


		statusField = new JTextField(15);
		statusField.setMaximumSize(new Dimension(250,20));
		statusField.setEditable(false);
		statusField.setBackground(Color.WHITE);
		statusField.setForeground(new Color(109, 132, 180));
		statusField.setHorizontalAlignment(JTextField.CENTER);
		statusField.setFont(new Font("Cambria", Font.BOLD, 10));
		statusField.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
		statusField.setText("Welcome!");

		final JTextField copyStatusField = statusField;
		new Thread(new Runnable(){
			public void run(){
				while(true){
					try{
						Thread.sleep(5000);
						checkSocketStatus();
						//refreshFriendList();
						copyStatusField.setText("");
					}
					catch(Exception e){
						//
					}
				}
			}
		}).start();

		friendsArea = new JPanel();
		friendsArea.setLayout(new BoxLayout(friendsArea, BoxLayout.Y_AXIS));
		friendsArea.setBackground(Color.WHITE);
		friendsArea.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
		friendsArea.add(friendsTitle);
		friendsArea.add(searchPanel);
		friendsArea.add(friendsConnectScroller);

		groupArea = new JPanel();
		groupArea.setLayout(new BoxLayout(groupArea, BoxLayout.Y_AXIS));
		groupArea.setBackground(Color.WHITE);
		groupArea.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
		groupArea.add(groupsTitle);
		groupArea.add(groupSearchPanel);
		groupArea.add(groupConnectScroller);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
		leftPanel.add(friendsArea);
		leftPanel.add(groupArea);
		leftPanel.add(statusField);
		//friendsArea.add(statusField);


		frame.getContentPane().add(BorderLayout.WEST, leftPanel);
		frame.getContentPane().add(BorderLayout.CENTER, chatTabbedPane);
		frame.getContentPane().add(BorderLayout.SOUTH, inputPanel);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure to logout and exit?", "Exit CHAT APP?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	for(Client c: clientsForTabs){
		        		c.closeConnection();
		        	}
		        	boolean done = Connection.logout(userName);
		        	if(done)
		            	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		            else{
		            	statusField.setText(Connection.error);
		            }
		        }
		        else{
		        	//
		        }
		    }
		});

		frame.setVisible(true);
	}
	//public void setOutputStream(DataOutputStream outToServer){
	//	this.outToServer = outToServer;
	//}
	public void addTab(String title, InetAddress ipAddress){
		int num_tabs = chatTabbedPane.getTabCount();
		for(int i=0;i<num_tabs;i++){
			String t = chatTabbedPane.getTitleAt(i);
			if(t.compareTo(title) == 0){
				chatTabbedPane.setSelectedComponent(chatTabbedPane.getComponentAt(i));
				statusField.setText("Already connected.");
				return ;
			}
		}

		JPanel chatArea = new JPanel();
		chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
		chatArea.setPreferredSize(new Dimension(90, 10000));
		//chatArea.setEditable(false);
		//chatArea.setLineWrap(true);
		//chatArea.setFont(font);
		JScrollPane chatScroller = new JScrollPane(chatArea);
		chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		ChatClient client = new ChatClient();
		boolean res = client.connect(ipAddress , chatArea);
		if(!res){
			statusField.setText("Unable to connect.");
			return ;
		}

		statusField.setText("Connected.");
		clientsForTabs.add(client);
		clientClosedStatus.add(false);

		chatTabbedPane.addTab(title, chatScroller);
		chatTabbedPane.setBackgroundAt(clientsForTabs.size()-1, new Color(109, 132, 180));
		chatTabbedPane.setSelectedComponent(chatScroller);

	}
	public void addTab(String title, Socket socket){
		JPanel chatArea = new JPanel();
		chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
		chatArea.setPreferredSize(new Dimension(90, 10000));
		//chatArea.setEditable(false);
		//chatArea.setLineWrap(true);
		//chatArea.setFont(font);
		JScrollPane chatScroller = new JScrollPane(chatArea);
		chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		ChatClient client = new ChatClient();
		client.setSocket(socket, chatArea);

		clientsForTabs.add(client);
		clientClosedStatus.add(false);

		chatTabbedPane.addTab(title, chatScroller);
		chatTabbedPane.setBackgroundAt(clientsForTabs.size()-1, new Color(109, 132, 180));
		chatTabbedPane.setSelectedComponent(chatScroller);
	}
	public void addGroupTab(String groupName, Socket socket, ArrayList<InetAddress> ipAddresses){
		System.out.println("group: " + groupName);
		boolean flag = false;
		int index = -1;
		int num_tabs = chatTabbedPane.getTabCount();
		for(int i=0;i<num_tabs;i++){
			String t = chatTabbedPane.getTitleAt(i);
			System.out.println("t: " + t);
			if(t.compareTo(groupName) == 0){
				chatTabbedPane.setSelectedComponent(chatTabbedPane.getComponentAt(i));
				index = i;
				flag = true;
			}
		}

		JPanel chatArea = new JPanel();

		if(flag == false){
			chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
			chatArea.setPreferredSize(new Dimension(90, 10000));
			//chatArea.setEditable(false);
			//chatArea.setLineWrap(true);
			//chatArea.setFont(font);
			JScrollPane chatScroller = new JScrollPane(chatArea);
			chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			

			GroupChatClient client = new GroupChatClient(groupName);
			client.setSocket(socket, chatArea, ipAddresses);

			clientsForTabs.add(client);
			clientClosedStatus.add(false);

			chatTabbedPane.addTab(groupName, chatScroller);
			chatTabbedPane.setBackgroundAt(clientsForTabs.size()-1, new Color(109, 132, 180));
			chatTabbedPane.setSelectedComponent(chatScroller);
		}
		else{
			GroupChatClient client = (GroupChatClient) clientsForTabs.get(index);
			client.setSocket(socket, chatArea, ipAddresses);
		}
	}
	private void showFriendList(){
		friendList = new ArrayList<String>();
		boolean val = Connection.getFriendList(userName);
		if(val){
			String response = Connection.response;
			if(response != null && response.compareTo("null") != 0){
				String ar[] = response.split(",");
				for(String x: ar){
					friendList.add(x);
					MyButton button = new MyButton(x);
					friendsConnectPanel.add(button);
					button.addActionListener(new StartChatListener(x));
					button.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
					button.setFocusPainted(false);
				}
			}
		}
		else{
			statusField.setText(Connection.error);
		}
	}
	private void refreshFriendList(){
		friendsConnectPanel.removeAll();
		showFriendList();
		friendsConnectPanel.validate();
	}
	private void showGroupList(){
		boolean val = Connection.getGroupList(userName);
		groupList = new ArrayList<Group>();
		if(val){
			String response = Connection.response;
			if(response != null && response.compareTo("null") != 0){
				String ar[] = response.split(",");
				int index = -1;
				for(String x: ar){
					groupList.add(new Group(x));
					index++;
					MyButton button = new MyButton(x);
					groupConnectPanel.add(button);
					button.addActionListener(new GroupButtonListener(x, index));
					button.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
					button.setFocusPainted(false);
				}
			}
		}
		else{
			statusField.setText(Connection.error);
		}
	}
	private void refreshGroupList(){
		groupConnectPanel.removeAll();
		showGroupList();
		groupConnectPanel.validate();
	}
	private void checkSocketStatus(){
		for(int i=0; i<clientsForTabs.size();i++){
			ChatClient c = (ChatClient) clientsForTabs.get(i);
			if(!clientClosedStatus.get(i) && c.isClosed()){
				clientClosedStatus.set(i, true);
				if (JOptionPane.showConfirmDialog(frame, 
				    chatTabbedPane.getTitleAt(i) + " is no longer connected. Do You wish to close this tab?", "INFO", 
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					clientsForTabs.remove(i);
					clientClosedStatus.remove(i);
					chatTabbedPane.remove(i);
				}
				else{
					chatTabbedPane.setTitleAt(i, "?" + chatTabbedPane.getTitleAt(i) + "?");
				}
			}
		}
	}
	class TypeAreaListener implements FocusListener{
		public void focusGained(FocusEvent e) {
			if (typeArea.getText().compareTo(placeholder) == 0) {
				typeArea.setText("");
			}
		}
		
		public void focusLost(FocusEvent e) {
			if (typeArea.getText().isEmpty()) {
				typeArea.setText(placeholder);
			}
		}
	}
	class TypeAreaListener2 implements KeyListener{
		boolean pressed = false;
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
				pressed = true;
		}
		public void keyReleased(KeyEvent e){
			if(pressed){
				String sendText = typeArea.getText();
				JScrollPane currScroller = (JScrollPane) chatTabbedPane.getSelectedComponent();
				JViewport viewport = currScroller.getViewport();
				/*JTextArea chatArea = (JTextArea) viewport.getView();
				String current = chatArea.getText();
				if(current.equals(""))
					chatArea.setText("ME: " + sendText);
				else{
					if(current.charAt(current.length()-1) == '\n')
						chatArea.setText(current + "ME: " + sendText);
					else
						chatArea.setText(current + "\nME: " + sendText);
				}*/
				JPanel chatArea = (JPanel) viewport.getView();
				//JTextField newTextArea = new JTextField(40);
				//int pixels = newTextArea.getFontMetrics(new Font("Cambria", Font.PLAIN, 15)).stringWidth("ME: " + sendText + "    ");
				//int cols = (int)(pixels / (newTextArea.getPreferredSize().getWidth() / 40)) + 5;
				//newTextArea.setColumns(cols);
				//newTextArea.setEditable(false);
				//newTextArea.setMaximumSize(new Dimension(1000, 20));
				//newTextArea.setFont(new Font("Cambria", Font.PLAIN, 15));


				int currentIndex = chatTabbedPane.getSelectedIndex();
				Client currentClient = clientsForTabs.get(currentIndex);
				currentClient.sendMessage(userName + ": " + sendText);

				sendText = "ME: " + sendText;

				if(sendText.length() > 72){
					while(sendText.length() > 0){
						if(sendText.length() > 72){
							JTextField newTextArea = new JTextField(40);
							newTextArea.setEditable(false);
							newTextArea.setMaximumSize(new Dimension(1200, 20));
							newTextArea.setFont(new Font("Cambria", Font.PLAIN, 15));
							newTextArea.setText(sendText.substring(0, 72));
							newTextArea.setBorder(BorderFactory.createEmptyBorder());
							chatArea.add(newTextArea);
							chatArea.validate();
							sendText = sendText.substring(72);
						}
						else{
							JTextField newTextArea = new JTextField(40);
							newTextArea.setEditable(false);
							newTextArea.setMaximumSize(new Dimension(1200, 20));
							newTextArea.setFont(new Font("Cambria", Font.PLAIN, 15));
							newTextArea.setBorder(BorderFactory.createEmptyBorder());
							newTextArea.setText(sendText);
							chatArea.add(newTextArea);
							chatArea.validate();
							sendText = "";
						}
					}
				}
				else{
					JTextField newTextArea = new JTextField(40);
					newTextArea.setEditable(false);
					newTextArea.setMaximumSize(new Dimension(1200, 20));
					newTextArea.setFont(new Font("Cambria", Font.PLAIN, 15));
					newTextArea.setBorder(BorderFactory.createEmptyBorder());
					newTextArea.setText(sendText);
					chatArea.add(newTextArea);
					chatArea.validate();
				}

				//newTextArea.setText("ME: " + sendText);
				

				typeArea.setText("");

				pressed = false;
			}
		}
		public void keyTyped(KeyEvent e){
			JTextField field = (JTextField) e.getSource();
			if(field.getText().length() > 100){
				field.setText(field.getText().substring(0, 100));
			}
		}
	}
	class AddFriendListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String friend = searchField.getText();
			if(friend == null || friend.equals("")){
				statusField.setText("Please specify a username.");
				return;
			}
			searchField.setText("");
			boolean val = Connection.addFriend(userName, friend);
			if(val){
				statusField.setText(Connection.response);
				refreshFriendList();
			}
			else{
				statusField.setText(Connection.error);
			}
		}
	}
	class CreateGroupListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String name = groupAddField.getText();
			if(name == null || name.equals("")){
				statusField.setText("Please specify a group name.");
				return;
			}
			groupAddField.setText("");
			boolean val = Connection.addGroup(userName, name);
			if(val){
				statusField.setText(Connection.response);
				refreshGroupList();
			}
			else{
				statusField.setText(Connection.error);
			}
		}
	}
	class GroupButtonListener implements ActionListener{
		int index;
		String name;
		public GroupButtonListener(String name, int index){
			this.index = index;
			this.name = name;
		}
		public void actionPerformed(ActionEvent e){
			Group group = groupList.get(index);
			group.populate(false);
			GroupManagerUI groupUI = new GroupManagerUI(userName, frame, group, friendList, chatTabbedPane, statusField, clientsForTabs, clientClosedStatus);
			groupUI.show();
		}
	}
	class StartChatListener implements ActionListener{
		String name;
		public StartChatListener(String name){
			this.name = name;
		}
		public void actionPerformed(ActionEvent e){
			boolean val = Connection.getIpAddress(name);
			if(val){
				statusField.setText("IP obtained.Connecting...");
				String ip = Connection.response;
				connectToUser(name, ip);
			}
			else{
				statusField.setText(Connection.error);
			}
		}
	}
	public void connectToUser(String username, String ipAddress){
		try{
			boolean val = Connection.checkLoggedIn(username);
			if(val){
				InetAddress ip = InetAddress.getByName(ipAddress);
				addTab(username, ip);
			}
			else{
				statusField.setText(username + " is not logged in.");
			}
		}
		catch(UnknownHostException e){
			statusField.setText("Unknown Host.");
		}
	}
	class AudioButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int currentIndex = chatTabbedPane.getSelectedIndex();
			Client currentClient = clientsForTabs.get(currentIndex);
			String toUser = chatTabbedPane.getTitleAt(currentIndex);

			JScrollPane currScroller = (JScrollPane) chatTabbedPane.getComponentAt(currentIndex);
			JViewport viewport = currScroller.getViewport();
			JPanel chatArea = (JPanel) viewport.getView();

			if(audioUI == null || audioUI.jobDone){
				audioUI = new AudioControlUI(frame, chatArea, currentClient, toUser);
				audioUI.show();
			}
		}
	}
}
class MyButton extends JButton{
	public MyButton(String title){
		super(title);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
	}
}
class AudioControlUI{
	JFrame frame;
	JButton button;
	JTextField statusField;

	byte[] recordedBytes;
	AudioHelper jack;

	String toUser;
	JFrame parentFrame;
	JPanel chatArea;
	Client client;

	boolean jobDone = false;
	boolean recordComplete = false;

	public AudioControlUI(JFrame parentFrame, JPanel chatArea, Client client, String toUser){
		this.parentFrame = parentFrame;
		this.chatArea = chatArea;
		this.client = client;
		this.toUser = toUser;
		jack = new AudioHelper();
	}
	public void show(){
		frame = new JFrame("Send Audio to " + toUser);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(350, 80);

		button = new JButton("Start");
		button.addActionListener(new AudioRecorder());
		statusField = new JTextField();
		statusField.setText("Click to start. Maximum allowed size: 5MB.");
		statusField.setHorizontalAlignment(JTextField.CENTER);
		statusField.setEditable(false);
		frame.getContentPane().add(BorderLayout.CENTER, button);
		frame.getContentPane().add(BorderLayout.SOUTH, statusField);
		frame.setResizable(false);



		Point parentLoc = parentFrame.getLocation();
		int xloc = (int) (parentLoc.getX() + parentFrame.getSize().width/2 - frame.getSize().width/2);
		int yloc = (int) (parentLoc.getY() + parentFrame.getSize().height/2 - frame.getSize().height/2);
		frame.setLocation(xloc, yloc);
		frame.setVisible(true);
	}
	class AudioRecorder implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(button.getText().compareTo("Start") == 0){
				button.setText("Recording...Click to stop.");
				new Thread(new Runnable(){
					public void run(){
						recordedBytes = jack.record();
					}
				}).start();
			}
			else if(button.getText().compareTo("Recording...Click to stop.") == 0){
				jack.stopped = true;
				new Thread(new Runnable(){
					public void run(){
						try{
							Thread.sleep(500);
						}
						catch(InterruptedException e){
							//
						}
						button.setText("Recorded...Click to send.");
						recordComplete = true;
					}
				}).start();
			}
			else if(button.getText().compareTo("Recorded...Click to send.") == 0 && recordComplete){
				frame.setVisible(false);
				client.sendAudio(recordedBytes);
				frame.dispose();
				addAudioButton(chatArea, recordedBytes, "Audio File Sent");
				jobDone = true;
			}
		}
	}
	public static void addAudioButton(JPanel panel, byte[] recordedBytes, String text){
		JButton playButton = new JButton(text, new ImageIcon("speakers.png"));
		playButton.setBackground(Color.WHITE);
		playButton.setFocusPainted(false);
		playButton.addActionListener(new AudioPlayer(recordedBytes));
		panel.add(playButton);
		panel.validate();
	}
}
class AudioPlayer implements ActionListener{
	byte[] stream;
	public AudioPlayer(byte[] stream){
		this.stream = stream;
	}
	public void actionPerformed(ActionEvent e){
		new Thread(new Runnable(){
			public void run(){
				AudioHelper.play(stream);
			}
		}).start();
	}
}
class Group{
	private String groupName;
	private ArrayList<String> members = new ArrayList<String>();
	private ArrayList<ChatClient> clients = new ArrayList<>();
	public Group(String groupName){
		this.groupName = groupName;
	}
	public String getName(){
		return groupName;
	}
	public void addMember(String name){
		members.add(name);
	}
	public String getMember(int index){
		return members.get(index);
	}
	public ArrayList<String> getMembers(){
		return members;
	}
	public void populate(boolean force){
		if(members.size() == 0 || force){
			members = new ArrayList<String>();
			boolean val = Connection.getGroupMembers(groupName);
			if(val){
				String mem[] = (Connection.response).split(",");
				for(String x: mem)
					members.add(x);
			}
		}
	}
}
class GroupManagerUI{
	String userName;
	Group group;
	ArrayList<String> friendList;

	JFrame parentFrame;
	JFrame frame;

	JTabbedPane parentTabbedPane;
	ArrayList<Client> clientsForTabs;
	ArrayList<Boolean> clientClosedStatus;

	JTextField statusField;

	JPanel mainPanel;
	JPanel leftPanel;
	JTextArea currentMembers;
	JPanel rightPanel;
	JPanel innerPanel;
	JScrollPane rightScroller;
	JButton addMember;

	JButton startButton;

	ArrayList<String> checkBoxSelection = new ArrayList<String>();

	public GroupManagerUI(String userName, JFrame parentFrame, Group group, ArrayList<String> friendList, JTabbedPane parentTabbedPane, JTextField statusField, ArrayList<Client> clientsForTabs, ArrayList<Boolean> clientClosedStatus){
		this.userName = userName;
		this.parentFrame = parentFrame;
		this.parentTabbedPane = parentTabbedPane;
		this.group = group;
		this.clientsForTabs = clientsForTabs;
		this.clientClosedStatus = clientClosedStatus;
		this.statusField = statusField;
		this.friendList = new ArrayList<String>();
		for(String x: friendList){
			this.friendList.add(x);
		}
	}
	private void showGroupMembers(){
		String members = "";
		for(String x: group.getMembers())
			members += x + "\n";
		currentMembers.setText(members);
	}
	private void updateFriendList(){
		for(String x: group.getMembers()){
			friendList.remove(x);
		}
	}
	public void setFriendList(){
		innerPanel.removeAll();
		System.out.println("removed");
		for(String x : friendList){
			System.out.println(x);
			JCheckBox chk = new JCheckBox(x);
			chk.addItemListener(new CheckboxItemListener());
			innerPanel.add(chk);
		}
		innerPanel.validate();
		innerPanel.repaint();
		System.out.println("validated");
	}
	public void show(){
		frame = new JFrame("CHAT APP -- " + group.getName());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 300);
		frame.setResizable(false);

		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(Color.WHITE);

		currentMembers = new JTextArea();
		showGroupMembers();
		currentMembers.setEditable(false);
		JScrollPane scroller = new JScrollPane(currentMembers);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JTextField leftLabel = new JTextField("Current Members");
		leftLabel.setMaximumSize(new Dimension(250,200));
		leftLabel.setEditable(false);
		leftLabel.setBackground(Color.WHITE);
		leftLabel.setForeground(new Color(109, 132, 180));
		leftLabel.setHorizontalAlignment(JTextField.CENTER);
		leftLabel.setBorder(BorderFactory.createEmptyBorder());
		leftLabel.setFont(new Font("Cambria", Font.BOLD, 14));

		leftPanel.add(leftLabel);
		leftPanel.add(scroller);


		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBackground(Color.WHITE);

		JTextField rightLabel = new JTextField("Add Members");
		rightLabel.setMaximumSize(new Dimension(250,200));
		rightLabel.setEditable(false);
		rightLabel.setBackground(Color.WHITE);
		rightLabel.setForeground(new Color(109, 132, 180));
		rightLabel.setHorizontalAlignment(JTextField.CENTER);
		rightLabel.setBorder(BorderFactory.createEmptyBorder());
		rightLabel.setFont(new Font("Cambria", Font.BOLD, 14));

		rightPanel.add(rightLabel);

		innerPanel= new JPanel();
		innerPanel.setBackground(Color.WHITE);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		rightScroller = new JScrollPane(innerPanel);
		rightScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		updateFriendList();
		setFriendList();

		rightPanel.add(rightScroller);

		addMember = new JButton("Add");
		addMember.addActionListener(new AddActionListener());
		
		rightPanel.add(addMember);


		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);

		startButton = new JButton("Start Group Chat");
		startButton.addActionListener(new GroupChatStartListener());

		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, startButton);

		Point parentLoc = parentFrame.getLocation();
		int xloc = (int) (parentLoc.getX() + parentFrame.getSize().width/2 - frame.getSize().width/2);
		int yloc = (int) (parentLoc.getY() + parentFrame.getSize().height/2 - frame.getSize().height/2);
		frame.setLocation(xloc, yloc);
		frame.setVisible(true);
	}
	public void addGroupTab(String title, ArrayList<InetAddress> ipAddresses){
		frame.setVisible(false);
		frame.dispose();

		int num_tabs = parentTabbedPane.getTabCount();
		for(int i=0;i<num_tabs;i++){
			String t = parentTabbedPane.getTitleAt(i);
			if(t.compareTo(title) == 0){
				parentTabbedPane.setSelectedComponent(parentTabbedPane.getComponentAt(i));
				statusField.setText("Already connected.");
				return ;
			}
		}

		JPanel chatArea = new JPanel();
		chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
		chatArea.setPreferredSize(new Dimension(90, 10000));
		//chatArea.setEditable(false);
		//chatArea.setLineWrap(true);
		//chatArea.setFont(font);
		JScrollPane chatScroller = new JScrollPane(chatArea);
		chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


		

		GroupChatClient client = new GroupChatClient(title);
		boolean res = client.connect(ipAddresses , chatArea);
		if(!res){
			statusField.setText("Unable to connect.");
			return ;
		}

		statusField.setText("Connected.");
		clientsForTabs.add(client);
		clientClosedStatus.add(false);

		parentTabbedPane.addTab(title, chatScroller);
		parentTabbedPane.setBackgroundAt(clientsForTabs.size()-1, new Color(109, 132, 180));
		parentTabbedPane.setSelectedComponent(chatScroller);

	}
	class GroupChatStartListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//
			ArrayList<InetAddress> ipAddresses = new ArrayList<InetAddress>();
			ArrayList<String> members = group.getMembers();
			for(String name: members){
				if(name.equals(userName))
					continue;
				boolean val = Connection.getIpAddress(name);
				if(val){
					statusField.setText("Obtaining IP Addresses...");
					String ip = Connection.response;
					try{
						ipAddresses.add(InetAddress.getByName(ip));
					}
					catch(UnknownHostException exc){
						//
					}
				}
				else{
					statusField.setText(Connection.error);
				}
			}
			statusField.setText("Connectng...Please wait..");
			addGroupTab(group.getName(), ipAddresses);
		}
	}
	class AddActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JButton button = (JButton) e.getSource();
			if(button.getText().equals("Add")){
				Connection.addGroupMember(group.getName(), checkBoxSelection);
				group.populate(true);
				showGroupMembers();
				updateFriendList();
				setFriendList();
			}
		}
	}
	class CheckboxItemListener implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			JCheckBox cb = (JCheckBox) e.getSource();
			if(cb.isSelected()){
				checkBoxSelection.add(cb.getText());
			}
			else{
				checkBoxSelection.remove(cb.getText());
			}
		}
	}
}