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
	JTextField statusField;
	Font font;
	Font font2;

	AudioControlUI audioUI;

	ChatServer myServer;
	ArrayList<ChatClient> clientsForTabs = new ArrayList<ChatClient>();
	ArrayList<Boolean> clientClosedStatus = new ArrayList<Boolean>();

	static final String placeholder = "Type in your text here and press Enter to send.";
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

		statusField = new JTextField(15);
		statusField.setMaximumSize(new Dimension(250,20));
		statusField.setEditable(false);
		statusField.setBackground(Color.WHITE);
		statusField.setForeground(new Color(109, 132, 180));
		statusField.setHorizontalAlignment(JTextField.CENTER);
		statusField.setFont(new Font("Cambria", Font.BOLD, 10));
		statusField.setBorder(BorderFactory.createEmptyBorder());

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
		friendsArea.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
		friendsArea.add(friendsTitle);
		friendsArea.add(searchPanel);
		friendsArea.add(friendsConnectScroller);
		friendsArea.add(statusField);


		frame.getContentPane().add(BorderLayout.WEST, friendsArea);
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

		JPanel chatArea = new JPanel();
		chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
		chatArea.setPreferredSize(new Dimension(90, 2000));
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
		chatArea.setPreferredSize(new Dimension(90, 2000));
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
				ChatClient currentClient = clientsForTabs.get(currentIndex);
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
			ChatClient currentClient = clientsForTabs.get(currentIndex);
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
	ChatClient client;

	boolean jobDone = false;
	boolean recordComplete = false;

	public AudioControlUI(JFrame parentFrame, JPanel chatArea, ChatClient client, String toUser){
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