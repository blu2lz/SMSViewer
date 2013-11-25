package blu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import blu.model.Contact;
import blu.model.Message;
import blu.model.Messages;

/**
 * Panel that shows the messages for the selected number.
 * 
 * @author Torsten Casselt
 */
@SuppressWarnings("serial")
public class MessagePanel extends JPanel {
	
	private JPanel leftPanel, rightPanel, scrollPanePanel;
	private BoxLayout boxLayout;
	private Messages messages;
	private JScrollPane scrollPane;
	private boolean reverse;
	private Contact lastContact;
	
	public MessagePanel(HashMap<Number, Contact> numberMap) throws IOException, ParseException {
		messages = new Messages(numberMap);
		setLayout(new BorderLayout());
		scrollPanePanel = new JPanel();
		boxLayout = new BoxLayout(scrollPanePanel, BoxLayout.LINE_AXIS);
		scrollPanePanel.setLayout(boxLayout);
		scrollPane = new JScrollPane(scrollPanePanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		scrollPanePanel.add(leftPanel);
		scrollPanePanel.add(rightPanel);
		add(scrollPane, BorderLayout.CENTER);
		reverse = false;
	}
	
	/**
	 * Sets the sort order.
	 * 
	 * @param reverse	true if oldest message at the top, false if newest
	 */
	public void setSortOrder(boolean reverse) {
		this.reverse = reverse;
		// refresh message list if contact is selected
		if (lastContact != null) {
			showMessages(lastContact);
		}
	}
	
	/**
	 * Shows messages for given number.
	 * 
	 * @param contact
	 */
	public void showMessages(Contact contact) {
		lastContact = contact;
		// clear panels
		leftPanel.removeAll();
		rightPanel.removeAll();
		// get messages, put them in text areas and add them to a panel
		ArrayList<Message> messageList = messages.getMessages(contact);
		ListIterator<Message> messageListIterator = reverse ? messageList.listIterator(messageList.size()) : messageList.listIterator();
		while (reverse ? messageListIterator.hasPrevious() : messageListIterator.hasNext()) {
			Message m = reverse ? messageListIterator.previous() : messageListIterator.next();
			JTextArea textAreaLeft = new JTextArea(DateFormat.getDateTimeInstance().format(m.getDate()) + "\n\n" + m.getText());
			textAreaLeft.setEditable(false);
			textAreaLeft.setLineWrap(true);
			textAreaLeft.setWrapStyleWord(true);
			textAreaLeft.setMargin(new Insets(20,20,20,20));
			//textAreaLeft.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			JTextArea textAreaRight = new JTextArea(DateFormat.getDateTimeInstance().format(m.getDate()) + "\n\n" + m.getText());
			textAreaRight.setEditable(false);
			textAreaRight.setLineWrap(true);
			textAreaRight.setWrapStyleWord(true);
			textAreaRight.setMargin(new Insets(20,20,20,20));
			//textAreaRight.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			// make one message transparent for keeping distance to next message
			if (m.isReceived()) {
				textAreaRight.setBackground(new Color(0,0,0,0));
				textAreaRight.setForeground(new Color(0,0,0,0));
			} else {
				textAreaLeft.setBackground(new Color(0,0,0,0));
				textAreaLeft.setForeground(new Color(0,0,0,0));
			}
			leftPanel.add(Box.createVerticalStrut(10));
			leftPanel.add(textAreaLeft);
			rightPanel.add(Box.createVerticalStrut(10));
			rightPanel.add(textAreaRight);
		}
		leftPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(Box.createVerticalStrut(10));
		validate();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
		   public void run() { 
		       scrollPane.getVerticalScrollBar().setValue(0);
		   }
		});
	}
}
