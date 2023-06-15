package client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;



public class ElectionGui {
	// the frame
	private JFrame frame;
	
	private Panel loginPanel;
	private Panel choiceHeadingPanel;
	private Panel electionChoicePanel;

	// the components
	private Label labelLogin;
	
	private Label labelUsername;
	private TextField textFieldUsername;
	
	private Label labelPassword;
	private JPasswordField textFieldPassword;
	private Button btnLogin;

	private Label labelElectionChoices;
	private String[] electionChoices; 
	
	private Button btnElection;
	
	private String userElection;
	
	

	// the text fields
	
//	private JTextField dataTextField = new JTextField(50);
//	private JTextField startHashCodeTextField = new JTextField(50);
//	private JTextField prefixHashCodeTextField = new JTextField(50);
//	private JTextField nonceTextField = new JTextField(10);
//	private JTextField timeTextField = new JTextField(10);
//	private JTextField statusTextField = new JTextField(50);

	// the buttons
//	private JButton calculateButton = new JButton("Calculate");
//	private JButton exitButton = new JButton("Exit");

	public ElectionGui() {
		
		frame = new JFrame("Beispielwahl 2023");
		
		loginPanel = new Panel();
		choiceHeadingPanel = new Panel();
		electionChoicePanel = new Panel();

		// the components
		labelLogin = new Label("Login: ");
		
		labelUsername = new Label("Username: ");
		textFieldUsername = new TextField(30);
		
		labelPassword = new Label("Password: ");
		textFieldPassword = new JPasswordField(20);
		
		btnLogin = new Button("Anmelden");

		labelElectionChoices = new Label("Wahl Moeglichkeiten: ");
		electionChoices = new String[] {"CDU", "FDP", "CSU", "Gruene", "AFD"};
		
		btnElection = new Button("waehlen");
		
		// settings
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//		frame.setSize(d.width / 2, d.height / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(0, 1,0,0));
		frame.setLocationRelativeTo(null);
		
		// adding components to panels
		loginPanel.add(labelLogin);
		loginPanel.add(labelUsername);
		loginPanel.add(textFieldUsername);
		loginPanel.add(labelPassword);
		loginPanel.add(textFieldPassword);
		loginPanel.add(btnLogin);
		
		choiceHeadingPanel.setLayout(new FlowLayout());
		choiceHeadingPanel.add(labelElectionChoices);
		
		GridLayout electionChoiceLayout = new GridLayout(electionChoices.length +1, 2, 0, 0);
		electionChoicePanel.setLayout(electionChoiceLayout);
		ButtonGroup groupBtnElectionChoices = new ButtonGroup();
		for (int i=0; i<electionChoices.length; i++) {
			JRadioButton rbtnChoice = new JRadioButton();
			groupBtnElectionChoices.add(rbtnChoice);
			electionChoicePanel.add(rbtnChoice);
			rbtnChoice.setHorizontalAlignment(SwingConstants.CENTER);

			rbtnChoice.setHorizontalTextPosition(SwingConstants.RIGHT);
			rbtnChoice.setText(electionChoices[i]);
		}
		
		electionChoicePanel.add(btnElection);
		

		// adding panels to frame
		frame.add(loginPanel);
		frame.add(choiceHeadingPanel);
		frame.add(electionChoicePanel);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});

		btnElection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userElection = getSelectedButton(electionChoicePanel).getText().toString();
				System.out.println("UserElection: "+ userElection);
				System.exit(0);
			}
		});
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoginUser(textFieldUsername, textFieldPassword);
			}
		});

		frame.pack();
		frame.setVisible(true);

	}

	public JRadioButton getSelectedButton(Panel panel) {
		JRadioButton btn = new JRadioButton();
//		Object[] selectedButtons = groupBtnElectionChoices.getSelection().getSelectedObjects();

		for (int i=0; i<panel.getComponentCount(); i++) {
			if (panel.getComponent(i) instanceof JRadioButton) {
				JRadioButton b = (JRadioButton) panel.getComponent(i);
				if (b.isSelected()) {
					btn = b;
				}
			}
		}
		
		return btn;
	}
	public void LoginUser(TextField textFieldUsername, JPasswordField textFieldPassword) {
		String username = textFieldUsername.getText().toString();
		String password = textFieldPassword.getPassword().toString();
		// ToDo: Check if Username and Password in Table BlockchainUser and if Keys are generated.
		System.out.println("Username: " + username + " Password: " + password);
	}
}
