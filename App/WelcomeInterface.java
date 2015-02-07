import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
class WelcomeInterface{
	JFrame frame;
	JPanel mainPanel;
	JPanel togglePanel;
	JTextField signupName;
	JPasswordField signupPwd;
	JTextField signupEmail;
	JTextField usrName;
	JPasswordField usrPwd;
	JTextField errorField;
	JButton signup;
	JButton login;
	JButton submit;
	Font font;

	UserInterface ui;

	static final String usrNamePlaceholder = "Username or Email";
	static final String usrPwdPlaceholder = "Password";
	static final String signupNamePlaceholder = "Username";
	static final String signupPwdPlaceholder = "Password";
	static final String signupEmailPlaceholder = "Email Id";

	public void show(){

		/*title = new JTextField(20);
		title.setText("CHAT APP");
		title.setEditable(false);
		title.setBackground(Color.WHITE);
		title.setHorizontalAlignment(JTextField.CENTER);*/

		font = new Font("Cambria", Font.BOLD, 14);

		// Toggle Buttons

		signup = new JButton("Sign Up");
		signup.setPreferredSize(new Dimension(140, 40));
		signup.setBackground(Color.WHITE);
		signup.setForeground(Color.BLACK);
		signup.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
		signup.setFocusPainted(false);
		signup.setFont(font);

		login = new JButton("Log In");
		login.setPreferredSize(new Dimension(140, 40));
		login.setBackground(new Color(109, 132, 180));
		login.setForeground(Color.WHITE);
		login.setBorder(BorderFactory.createEmptyBorder());
		login.setFocusPainted(false);
		login.setFont(font);

		ToggleListener tl = new ToggleListener();
		PressListener cl = new PressListener();
		signup.addActionListener(tl);
		signup.addItemListener(cl);
		login.addActionListener(tl);
		login.addItemListener(cl);

		// Toggle Panel

		togglePanel = new JPanel();
		togglePanel.add(login);
		togglePanel.add(signup);
		togglePanel.setBackground(Color.WHITE);


		// Text Field for taking inputs from the user
		
		signupName = new JTextField(signupNamePlaceholder, 22);
		signupPwd = new JPasswordField(signupPwdPlaceholder, 22);
		signupEmail = new JTextField(signupEmailPlaceholder, 22);
		usrName = new JTextField(usrNamePlaceholder, 22);
		usrPwd = new JPasswordField(usrPwdPlaceholder, 22);

		usrName.addFocusListener(new PlaceholderListener(1));
		usrPwd.addFocusListener(new PlaceholderListener(2));
		signupName.addFocusListener(new PlaceholderListener(3));
		signupPwd.addFocusListener(new PlaceholderListener(4));
		signupEmail.addFocusListener(new PlaceholderListener(5));

		usrName.setFont(font);
		usrPwd.setFont(font);
		signupName.setFont(font);
		signupPwd.setFont(font);
		signupEmail.setFont(font);


		// Error Field & Submit Button

		errorField = new JTextField(22);
		//errorField.setBackground(Color.BLACK);
		//errorField.setForeground(Color.WHITE);
		errorField.setHorizontalAlignment(JTextField.CENTER);
		errorField.setBorder(BorderFactory.createEmptyBorder());

		submit = new JButton("Log In");
		submit.setBackground(new Color(109, 132, 180));
		submit.setForeground(Color.WHITE);
		submit.setFocusPainted(false);
		submit.setFont(font);
		submit.addActionListener(new SubmitListener());


		// Main Panel where these fields are added

		mainPanel = new JPanel();
		mainPanel.setBackground(Color.WHITE);
		mainPanel.add(usrName);
		mainPanel.add(usrPwd);
		mainPanel.add(errorField);

		// Adding contents to the Main Frame

		frame = new JFrame("CHAT APP");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 220);
		frame.setResizable(false);
		frame.add(BorderLayout.NORTH, togglePanel);
		frame.add(BorderLayout.CENTER, mainPanel);
		frame.add(BorderLayout.SOUTH, submit);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

		frame.setVisible(true);
	}
	public void changeTogglePanel(boolean val){
		if(val){
			frame.setVisible(false);
			mainPanel.removeAll();
			mainPanel.add(signupEmail);
			mainPanel.add(signupName);
			mainPanel.add(signupPwd);
			mainPanel.add(errorField);
			errorField.setText("");
			mainPanel.validate();
			frame.add(BorderLayout.CENTER, mainPanel);
			submit.setText("Create an account");
			login.setBackground(Color.WHITE);
			login.setForeground(Color.BLACK);
			login.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
			signup.setBackground(new Color(109, 132, 180));
			signup.setForeground(Color.WHITE);
			frame.setVisible(true);
		}
		else{
			frame.setVisible(false);
			mainPanel.removeAll();
			mainPanel.add(usrName);
			mainPanel.add(usrPwd);
			mainPanel.add(errorField);
			errorField.setText("");
			mainPanel.validate();
			frame.add(BorderLayout.CENTER, mainPanel);
			submit.setText("Log In");
			signup.setBackground(Color.WHITE);
			signup.setForeground(Color.BLACK);
			signup.setBorder(BorderFactory.createLineBorder(new Color(109, 132, 180)));
			login.setBackground(new Color(109, 132, 180));
			login.setForeground(Color.WHITE);
			frame.setVisible(true);
		}
	}
	class PlaceholderListener implements FocusListener{
		private int value;
		public PlaceholderListener(int value){
			this.value = value;
		}
		public void focusGained(FocusEvent e) {
			if (value == 1 && usrName.getText().compareTo(usrNamePlaceholder) == 0) {
				usrName.setText("");
			}
			else if (value == 2 && (new String(usrPwd.getPassword())).compareTo(usrPwdPlaceholder) == 0) {
				usrPwd.setText("");
			}
			else if (value == 3 && signupName.getText().compareTo(signupNamePlaceholder) == 0) {
				signupName.setText("");
			}
			else if (value == 4 && (new String(signupPwd.getPassword())).compareTo(signupPwdPlaceholder) == 0) {
				signupPwd.setText("");
			}
			else if (value == 5 && signupEmail.getText().compareTo(signupEmailPlaceholder) == 0) {
				signupEmail.setText("");
			}
		}
		
		public void focusLost(FocusEvent e) {
			if (value == 1 && usrName.getText().isEmpty()) {
				usrName.setText(usrNamePlaceholder);
			}
			else if (value == 2 && (new String(usrPwd.getPassword())).isEmpty()) {
				usrPwd.setText(usrPwdPlaceholder);
			}
			else if (value == 3 && signupName.getText().isEmpty()) {
				signupName.setText(signupNamePlaceholder);
			}
			else if (value == 4 && (new String(signupPwd.getPassword())).isEmpty()) {
				signupPwd.setText(signupPwdPlaceholder);
			}
			else if (value == 5 && signupEmail.getText().isEmpty()) {
				signupEmail.setText(signupEmailPlaceholder);
			}
		}
	}
	class PressListener implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			JButton b = (JButton) e.getSource();
			b.setBackground(Color.BLACK);
		}
	}
	class ToggleListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JButton b = (JButton) e.getSource();
			changeTogglePanel(b.getText().compareTo("Sign Up") == 0);
		}
	}
	class SubmitListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			errorField.setText("");
			String buttonType = submit.getText();
			if(buttonType.compareTo("Log In") == 0){
				String userName = usrName.getText();
				String pwd = new String(usrPwd.getPassword());
				if(userName.equals("") || userName.equals(usrNamePlaceholder) || pwd.equals("") || pwd.equals(usrPwdPlaceholder)){
					errorField.setText("None of the field(s) should be empty.");
					return ;
				}
				boolean response = Connection.login(userName, pwd);
				if(response){
					errorField.setText("Success!!!");
					frame.setVisible(false);
					frame.dispose();
					//try{
						//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
						//ChatApp app = new ChatApp();
						//System.out.println("Do you want to connect?\n1.Yes\n2.No. Wait for connection.");
						//int inp = Integer.parseInt(br.readLine());
						//if(inp == 1){
						//	System.out.println("Enter port:");
						//	int p = Integer.parseInt(br.readLine());
						//	//app.createClient(p);
					//	}
					//}
					//catch(IOException exc){
					//	exc.printStackTrace();
					//}
					System.out.println("Logged In");
					ui = new UserInterface(userName, pwd);
					ui.show();
				}
				else{
					errorField.setText(Connection.error);
				}
			}
			else if(buttonType.compareTo("Create an account") == 0){
				String email = signupEmail.getText();
				String userName = signupName.getText();
				String pwd = new String(signupPwd.getPassword());
				if(email.equals("") || email.equals(signupEmailPlaceholder) || userName.equals("") || userName.equals(signupNamePlaceholder) || pwd.equals("") || pwd.equals(signupPwdPlaceholder)){
					errorField.setText("None of the field(s) should be empty.");
					return ;
				}
				boolean response = Connection.signUp(email, userName, pwd);
				if(response){
					errorField.setText("Success!!!");
				}
				else{
					errorField.setText(Connection.error);
				}
			}
		}
	}
}