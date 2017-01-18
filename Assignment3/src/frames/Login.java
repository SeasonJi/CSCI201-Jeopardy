package frames;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import game_logic.GameData;
import listeners.ExitWindowListener;
import listeners.TextFieldFocusListener;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;

import java.io.Serializable;

public class Login extends JFrame{
	public static final long serialVersionUID = 1L; 
	
	private JPanel topPanel, centerPanel, botPanel;
	private JLabel topLabel, titleLabel, userInfoLabel;
	private JTextField usernameJTF, passwordJTF;
	private JButton loginButton, createAccountButton;
	
	private boolean haveNames;
	
	private HashMap<String, String> users = new HashMap<String, String>();
	
	public Login()
	{
		super("Jeopardy");
		setSize(600, 600);
		setLocation(200, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getUserData();
		
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	public void initializeComponents()
	{
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(3,1));
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		botPanel = new JPanel();
		botPanel.setLayout(new GridLayout(1,2));
		
		topLabel = new JLabel("login or create an account to play");
		titleLabel = new JLabel("Jeopardy");
		userInfoLabel = new JLabel("");
		
		usernameJTF = new JTextField();
		usernameJTF.setText("Please enter username");
		passwordJTF = new JTextField();
		passwordJTF.setText("Please enter password");
		
		loginButton = new JButton("Login");
		loginButton.setEnabled(false);
		createAccountButton = new JButton("Create Account");
		createAccountButton.setEnabled(false);
	}
	
	public void createGUI()
	{	
		//top panel/////////////////////////////////////////
		topPanel.setBackground(new Color(135,206,250));
		
		topLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userInfoLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(topLabel);
		topPanel.add(titleLabel);
		topPanel.add(userInfoLabel);
		
		add(topPanel, BorderLayout.NORTH);
		////////////////////////////////////////////////////
		
		//center panel//////////////////////////////////////
		centerPanel.setBackground(new Color(135,206,250));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 300;
		gbc.ipady = 30;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		centerPanel.add(usernameJTF, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 20;
		gbc.ipady = 20;
		JLabel blank = new JLabel("");
		centerPanel.add(blank, gbc);
	
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.ipadx = 300;
		gbc.ipady = 30;
		centerPanel.add(passwordJTF, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        ////////////////////////////////////////////////////
        
        //bot panel////////////////////////////////////////
        botPanel.setPreferredSize(new Dimension(50, 50));
        botPanel.setBackground(new Color(135,206,250));
        
        loginButton.setSize(new Dimension(15, 20));
        botPanel.add(loginButton);
        
        createAccountButton.setSize(new Dimension(15, 20));
        botPanel.add(createAccountButton);
        
        add(botPanel, BorderLayout.SOUTH);
        //////////////////////////////////////////////////		
	}
	
	public void addEvents()
	{	//clear original text when clicked
		usernameJTF.addFocusListener(new TextFieldFocusListener("Please enter username", usernameJTF));
		usernameJTF.getDocument().addDocumentListener(new MyDocumentListener());
		passwordJTF.addFocusListener(new TextFieldFocusListener("Please enter password", passwordJTF));
		passwordJTF.getDocument().addDocumentListener(new MyDocumentListener());
		
		loginButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{	
				String inname = usernameJTF.getText().trim();
				String inword = passwordJTF.getText().trim();
				if(users.containsKey(inname) == false)
				{
					userInfoLabel.setText("this username and password combination does not exist...");
					return;
				}
				else
				{
					if(users.get(inname).trim().equals(inword))
					{
						StartWindowGUI sw = new StartWindowGUI();
						sw.setVisible(true);
					}
					else
					{
						userInfoLabel.setText("this username and password combination does not exist");
						return;
					}
				}
			}
		});
		
		createAccountButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				String inname = usernameJTF.getText().trim();
				String inword = passwordJTF.getText().trim();
				if(users.containsKey(inname) == false)
				{
					userInfoLabel.setText("");
					users.put(inname, inword);
					try {
						ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("o.txt"));
						oos.writeObject(users);
						oos.close();
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
				{
					userInfoLabel.setText("this username already exists");
					return;
				}
			}
		});
	}
	
	//updates and returns the haveNames member variable of whether all teams have been named
	private boolean haveNames()
	{
		haveNames = true;
		//check to see if all relevant text fields have text in them
		if(usernameJTF.getText().equals("")	|| usernameJTF.getText().equals("Please enter username") )
		{
			haveNames = false;
		}
		else if(passwordJTF.getText().equals("") || passwordJTF.getText().equals("Please enter password"))
		{
			haveNames = false;
		}
		return haveNames;
	}
	
	//document listener; in each method, simply checking whether the user can start the game
	private class MyDocumentListener implements DocumentListener
	{
		@Override
		public void insertUpdate(DocumentEvent e) {
			loginButton.setEnabled(haveNames());
			createAccountButton.setEnabled(haveNames());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			loginButton.setEnabled(haveNames());
			createAccountButton.setEnabled(haveNames());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			loginButton.setEnabled(haveNames());
			createAccountButton.setEnabled(haveNames());
		}
	}
	
	public void getUserData()
	{
		try {
			FileInputStream fis = new FileInputStream("o.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			users = (HashMap<String, String>)ois.readObject();

			for(String key : users.keySet())
			{
				System.out.println(key + " " + users.get(key));
			}
			
			ois.close();
			fis.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args)
	{
		Login login = new Login();
		login.setVisible(true);
	}
}
