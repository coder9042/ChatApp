import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
class UserInterface{
	static final String placeholder = "Type in your text here...";
	String userName;
	String password;
	JFrame frame;
	JTabbedPane chatTabbedPane;
	JTextArea typeArea;
	JPanel friendsArea;
	JPanel friendsConnectPanel;
	JScrollPane friendsConnectScroller;
	JTextField searchField;
	JButton searchButton;
	JPanel searchPanel;
	JTextField statusField;
	Font font;
	Font font2;

	ChatServer myServer;
	ArrayList<ChatClient> clientsForTabs = new ArrayList<ChatClient>();
	ArrayList<Boolean> clientClosedStatus = new ArrayList<Boolean>();
	public UserInterface(String userName, String password){
		this.userName = userName;
		this.password = password;
		myServer = new ChatServer(this);
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

		typeArea = new JTextArea(2, 100);
		typeArea.setText(placeholder);
		typeArea.addFocusListener(new TypeAreaListener());
		typeArea.addKeyListener(new TypeAreaListener2());
		typeArea.setLineWrap(true);
		typeArea.setFont(font2);

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
		//searchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchPanel.add(searchField);
		searchPanel.add(searchButton);

		friendsConnectPanel = new JPanel();
		friendsConnectPanel.setLayout(new BoxLayout(friendsConnectPanel, BoxLayout.Y_AXIS));
		friendsConnectPanel.setBackground(Color.WHITE);
		//friendsConnectPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		friendsConnectScroller = new JScrollPane(friendsConnectPanel);
		showFriendList();

		statusField = new JTextField(15);
		statusField.setMaximumSize(new Dimension(250,20));
		statusField.setEditable(false);
		statusField.setBackground(Color.WHITE);
		statusField.setForeground(new Color(109, 132, 180));
		statusField.setHorizontalAlignment(JTextField.CENTER);
		statusField.setFont(new Font("Cambria", Font.BOLD, 10));

		final JTextField copyStatusField = statusField;
		new Thread(new Runnable(){
			public void run(){
				while(true){
					try{
						Thread.sleep(5000);
						checkSocketStatus();
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
		friendsArea.add(friendsTitle);
		friendsArea.add(searchPanel);
		friendsArea.add(friendsConnectScroller);
		friendsArea.add(statusField);


		frame.getContentPane().add(BorderLayout.WEST, friendsArea);
		frame.getContentPane().add(BorderLayout.CENTER, chatTabbedPane);
		frame.getContentPane().add(BorderLayout.SOUTH, typeArea);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure to logout and exit?", "Exit CHAT APP?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	for(ChatClient c: clientsForTabs){
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

		JTextArea chatArea = new JTextArea(250, 25);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setFont(font);
		JScrollPane chatScroller = new JScrollPane(chatArea);
		chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
		chatTabbedPane.setSelectedComponent(chatScroller);

	}
	public void addTab(String title, Socket socket){
		JTextArea chatArea = new JTextArea(250, 25);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setFont(font);
		JScrollPane chatScroller = new JScrollPane(chatArea);
		chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		ChatClient client = new ChatClient();
		client.setSocket(socket, chatArea);

		clientsForTabs.add(client);
		clientClosedStatus.add(false);

		chatTabbedPane.addTab(title, chatScroller);
		chatTabbedPane.setSelectedComponent(chatScroller);
	}
	private void showFriendList(){
		boolean val = Connection.getFriendList(userName);
		if(val){
			String response = Connection.response;
			if(response != null && response.compareTo("null") != 0){
				String ar[] = response.split(",");
				for(String x: ar){
					MyButton button = new MyButton(x);
					friendsConnectPanel.add(button);
					button.addActionListener(new StartChatListener(x));
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
	private void checkSocketStatus(){
		for(int i=0; i<clientsForTabs.size();i++){
			ChatClient c = clientsForTabs.get(i);
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
				JTextArea chatArea = (JTextArea) viewport.getView();
				//chatArea.setText(chatArea.getText() + "\nME: " + sendText);
				String current = chatArea.getText();
				if(current.equals(""))
					chatArea.setText("ME: " + sendText);
				else{
					if(current.charAt(current.length()-1) == '\n')
						chatArea.setText(current + "ME: " + sendText);
					else
						chatArea.setText(current + "\nME: " + sendText);
				}
				typeArea.setText("");

				int currentIndex = chatTabbedPane.getSelectedIndex();
				ChatClient currentClient = clientsForTabs.get(currentIndex);
				currentClient.sendMessage(sendText);

				pressed = false;
			}
		}
		public void keyTyped(KeyEvent e){
			//
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
}
class MyButton extends JButton{
	public MyButton(String title){
		super(title);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
	}
}