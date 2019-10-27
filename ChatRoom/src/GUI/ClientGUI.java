package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import client.Client;
import main.ChatMessage;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField tf;
	private JTextField tfServer, tfPort;
	private JButton login, logout;
	private JTextArea ta;
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;

	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		JPanel north = new JPanel(new GridLayout(3,1));
		JPanel sap = new JPanel(new GridLayout(1,5, 2, 3));
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		sap.add(new JLabel("Port Number:  "));
		sap.add(tfPort);
		sap.add(new JLabel(""));
		north.add(sap);

		label = new JLabel("Enter Your Name Here:", SwingConstants.LEFT);
		north.add(label);
		tf = new JTextField("Anonymous");
		tf.setBackground(Color.WHITE);
		north.add(tf);
		add(north,BorderLayout.NORTH);

		ta = new JTextArea("Welcome to the Chat room\n", 10, 10);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Exit");
		logout.addActionListener(this);
		logout.setEnabled(false);

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

	}

	public void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}

	public void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		label.setText("Enter Your Username Below");
		tf.setText("Anonymous");
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		tf.removeActionListener(this);
		connected = false;
	}
	
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
		}
		
		if(connected) {
			if(tf.getText().length()>0) {
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));
			}
			tf.setText("");
			return;
		}
		

		if(o == login) {
			String username = tf.getText().trim();
			if(username.length() == 0)
				return;
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;
			}

			client = new Client(server, port, username, this);
			
			if(!client.start()) 
				return;
			tf.setText("");
			label.setText("You can type your message below:");
			connected = true;
			
			login.setEnabled(false);
			logout.setEnabled(true);
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			tf.addActionListener(this);
		}

	}

	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);
	}

}
