package blu.controller;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blu.model.Contact;
import blu.view.MessagePanel;

/**
 * ListSelectionListener for the contact list that shows messages belonging to the selected number.
 * 
 * @author Torsten Casselt
 */
public class ContactListListener implements ListSelectionListener {

	MessagePanel messagePanel;
	
	public ContactListListener(MessagePanel mp) {
		messagePanel = mp;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(ListSelectionEvent e) {
		messagePanel.showMessages(((JList<Contact>)e.getSource()).getSelectedValue());
	}

}
