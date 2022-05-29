package chat;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.io.DataOutputStream;



public class ChatClient extends JFrame {
  // IO streams
  DataOutputStream toServer = null;
  DataInputStream fromServer = null;
  JTextField textField = new JTextField();;
  JTextArea textArea = new JTextArea();
  Socket socket = null;
  boolean close=true;
  MessageListener ml;
  int no=0;
  
  public ChatClient() {
	  add(new JScrollPane(textField));
	  JPanel mainPanel = new JPanel();
	  mainPanel.setLayout(new BorderLayout());
	  mainPanel.add(new JLabel("Begin typing..."), BorderLayout.WEST);
	    textField.setPreferredSize(new Dimension(5,25));
	    mainPanel.add(textField, BorderLayout.CENTER);
	    textField.setHorizontalAlignment(JTextField.LEFT);
	    textField.addActionListener(new TextFieldListener());
	
	    setLayout(new BorderLayout());
	    add(mainPanel, BorderLayout.SOUTH);
	    
	    add(new JScrollPane(textArea), BorderLayout.CENTER);

	    setTitle("ChatClient");
	    setSize(400, 400);
	    this.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent evt) {
			closeChat();
		}
	});
	    setVisible(true);
	    createMenu();
	    
  }
  private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) ->{
			closeChat();
		});
		JMenuItem openItem = new JMenuItem("Connect");
		openItem.addActionListener(new OpenConnectionListener());
		menu.add(openItem);
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
  
  class OpenConnectionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			
			if(no==0) {
			socket = new Socket("localhost", 9898);
			textArea.append("Connection established :)"+"\n");
			Thread t= new Thread();
			ml= new MessageListener();
			no=1;
			}
			else {
				textArea.append("The connection is already established ;)"+"\n");
		} 
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			textArea.append("Failed to connect :("+"\n");
		}
	}
	  
  }
  
  class TextFieldListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    try {
		   
		      toServer = new DataOutputStream(socket.getOutputStream());
		    }
		    catch (IOException ex) {
		      textArea.append(ex.toString() + '\n');
		    }
	    
	    try {
	        String str = textField.getText().trim();
	        textArea.append("My msg:"+str+"\n");
	        toServer.writeUTF(str);
	        textField.setText("");
	        toServer.flush();
	      }
	      catch (IOException ex) {
	        System.err.println(ex);
	      }	    
	}
  }
	  

  public static void main(String[] args) {
    ChatClient c = new ChatClient();
    c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    c.setVisible(true);
  }
  
  public void closeChat() {
	  no=0;
	  if(socket==null)
		  System.exit(0);
	 try {
		 
		 toServer = new DataOutputStream(socket.getOutputStream());
		  toServer.writeUTF("Quit");
		  close=false;
		  
		  socket.close();
		System.exit(0);
	  }catch(Exception e1) {
			System.err.println("error");
  }		 
  }


class MessageListener extends Thread{
	
	MessageListener()
	{
		this.start();
	}
	public void run() {
	synchronized (socket){
	while(close) {
	try {
			fromServer = new DataInputStream(socket.getInputStream());
	}
	catch (IOException e){
		System.err.println(e);
	}
	try {
    	String str= fromServer.readUTF();
    	textArea.append(str);
    	textArea.append("\n");
    	textField.setText("");
    }
    
    catch(IOException ex) {
    	textArea.append(ex.toString()+"\n");
    }}
}}
}}