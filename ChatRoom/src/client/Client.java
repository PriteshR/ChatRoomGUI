package client;

import java.net.*;
import java.io.*;
import java.util.*;
import GUI.ClientGUI;
import main.ChatMessage;


public class Client  {

	private ObjectInputStream si;
	private ObjectOutputStream so;
	private Socket socket;

	private ClientGUI cg;
	
	private String server, username;
	private int port;

	Client(String server, int port, String username) {
		this(server, port, username, null);
	}

	public Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.cg = cg;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception ec) {
			display("msg:" + ec);
			return false;
		}
		
		String msg = username+ ", Welcome to the Chat.";
		display(msg);
	
		try
		{
			si  = new ObjectInputStream(socket.getInputStream());
			so = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e) {
			display("msg:"+e);
			return false;
		}

		new ListenFromServer().start();
		try
		{
			so.writeObject(username);
		}
		catch (IOException e) {
			display("msg:"+e);
			disconnect();
			return false;
		}
	
		return true;
	}

	private void display(String msg) {
		if(cg == null)
			System.out.println(msg);
		else
			cg.append(msg + "\n");	
	}
	
	public void sendMessage(ChatMessage msg) {
		try {
			so.writeObject(msg);
		}
		catch(IOException e) {
			display("msg:"+e);
		}
	}

	private void disconnect() {
		try { 
			if(si != null) si.close();
		}
		catch(Exception e) {}
		try {
			if(so != null) so.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
		
		if(cg != null) {
			cg.connectionFailed();
		}
	}
	
	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		switch(args.length) {
			case 3:
				serverAddress = args[2];
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					return;
				}
			case 1: 
				userName = args[0];

			case 0:
				break;
			default:
			return;
		}
		Client client = new Client(serverAddress, portNumber, userName);
		if(!client.start())
			return;
		
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.print("> ");
			String msg = scan.nextLine();
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				break;
			}
			else {
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		client.disconnect();	
	}

	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) si.readObject();
					if(cg == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						cg.append(msg);
					}
				}
				catch(IOException e) {
					display("Connection closed:"+e);
					if(cg != null) 
						cg.connectionFailed();
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
