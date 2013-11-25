package blu.view;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JList;
import javax.swing.JPanel;

import blu.model.Contact;

/**
 * Panel where you can choose the contact to show the messages from.
 * 
 * @author Torsten Casselt
 */
@SuppressWarnings("serial")
public class NavigationPanel extends JPanel {
	
	public NavigationPanel(JList<Contact> contactList) throws IOException {
		setLayout(new BorderLayout());
		add(contactList);
	}
}
