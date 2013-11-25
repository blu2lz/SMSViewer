package blu.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import blu.controller.ContactListListener;
import blu.model.Contact;
import blu.model.ContactListModel;
import blu.view.MessagePanel;
import blu.view.NavigationPanel;
import ezvcard.types.TelephoneType;

/**
 * Main class of this program.
 * 
 * @author Torsten Casselt
 */
@SuppressWarnings("serial")
public class SMSViewerMain extends JFrame {

	public static String DIRECTORY;
	final public static String SETTINGS_DIRECTORY = System.getProperty("user.home") + "/.smsviewer";
	final public static String SETTINGS = SETTINGS_DIRECTORY + "/settings";
	private JPanel contentPane;
	private MessagePanel messagePanel;
	private JMenu mainMenu;
	private JMenuItem exitMenuItem, folderChangeMenuItem;
	private JRadioButtonMenuItem rbMenuItem;

	private static void chooseDirectory() {
		JFileChooser chooser = new JFileChooser();
		// choose folders only
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			DIRECTORY = chooser.getSelectedFile().getAbsolutePath();
			File userSettings = new File(SETTINGS);
			Properties props = new Properties();
			// set new directory
			props.setProperty("userdir", DIRECTORY);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(userSettings);
			} catch (FileNotFoundException fnfe) {
				JOptionPane.showMessageDialog(null, fnfe.getMessage()
							+ " User settings not writable!", "Error",
							JOptionPane.ERROR_MESSAGE);
				fnfe.printStackTrace();
			}
			try {
				props.store(out, null);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(null, ioe.getMessage()
						+ " Settings could not be written to file!",
						"Error", JOptionPane.ERROR_MESSAGE);
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// check if program folder exists, else create
				File programFolder = new File(SETTINGS_DIRECTORY);
				if (!programFolder.exists()) {
					if (!programFolder.mkdirs()) {
						JOptionPane.showMessageDialog(null, "Programmordner " + SETTINGS_DIRECTORY + " konnte nicht erstellt werden!", "Error", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					}
				}
				// read user directory
				File userSettings = new File(SETTINGS);
				FileInputStream in = null;
				PropertyResourceBundle settingsBundle;
				if (userSettings.exists()) {
					// read user settings and use them
					try {
						in = new FileInputStream(SETTINGS);
						settingsBundle = new PropertyResourceBundle(in);
						DIRECTORY = settingsBundle.getString("userdir");
						in.close();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, e.getMessage()
								+ "Kann Usersettings nicht lesen!", "Error",
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						System.exit(1);
					}
				} else {
					// ask for user directory
					chooseDirectory();
					if (DIRECTORY == null) {
						JOptionPane.showMessageDialog(null, "Kein Ordner festgelegt, beende...", "Information",
								JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
				}
				try {
					SMSViewerMain frame = new SMSViewerMain();
					frame.setVisible(true);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Eine oder mehrere Datei(en) konnten nicht gelesen werden!", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.exit(1);
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, "Die CSV-Datei mit den Nachrichten konnte nicht eingelesen werden!", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}
	
	/**
	 * Builds main menu.
	 */
	private void buildMenu() {
		// create menu
		JMenuBar menuBar = new JMenuBar();
		mainMenu = new JMenu("Menü");
		mainMenu.setMnemonic(KeyEvent.VK_M);
		menuBar.add(mainMenu);

		// menu entry for changing folder
		folderChangeMenuItem = new JMenuItem("Speicherort festlegen", KeyEvent.VK_S);
		folderChangeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		folderChangeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDirectory();
			}
		});
		mainMenu.add(folderChangeMenuItem);

		// menu entries for changing sort order
		mainMenu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("Neue Nachrichten zuerst");
		rbMenuItem.setSelected(true);
		rbMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		rbMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				messagePanel.setSortOrder(false);
			}
		});
		group.add(rbMenuItem);
		mainMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Alte Nachrichten zuerst");
		rbMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
				ActionEvent.ALT_MASK));
		rbMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				messagePanel.setSortOrder(true);
			}
		});
		group.add(rbMenuItem);
		mainMenu.add(rbMenuItem);

		// menu entry for exiting the program
		mainMenu.addSeparator();
		exitMenuItem = new JMenuItem("Beenden", KeyEvent.VK_X);
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
				ActionEvent.ALT_MASK));
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainMenu.add(exitMenuItem);

		setJMenuBar(menuBar);
	}

	/**
	 * Create the frame.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public SMSViewerMain() throws IOException, ParseException {
		// set look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (InstantiationException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		buildMenu();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		JList<Contact> contactList = new JList<Contact>(new ContactListModel());
		// create hash map for numbers and contacts for quick search for contact to number
		ListModel<Contact> contactListModel = contactList.getModel();
		HashMap<Number, Contact> numberMap = new HashMap<Number, Contact>();
		for (int i = 0; i < contactListModel.getSize(); i++) {
			Contact c = contactListModel.getElementAt(i);
			List<TelephoneType> numbers = c.getNumbers();
			for (int j = 0; j < c.getNumberCount(); j++) {
				numberMap.put(Long.parseLong(numbers.get(j).getValue()), c);
			}
		}
		// create main panel for messages with scroll pane
		messagePanel = new MessagePanel(numberMap);
		contactList.addListSelectionListener(new ContactListListener(messagePanel));
		// create navigation panel for contacts with scroll pane
		NavigationPanel navigationPanel = new NavigationPanel(contactList);
		JScrollPane navigationScrollPane = new JScrollPane(navigationPanel);
		navigationScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		navigationScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigationScrollPane, messagePanel);
		splitPane.setOneTouchExpandable(true);
		contentPane.add(splitPane, BorderLayout.CENTER);
		setContentPane(contentPane);
	}

}
