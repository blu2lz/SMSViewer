package blu.model;

import java.util.Date;

/**
 * Represents one message.
 * 
 * @author Torsten Casselt
 */
public class Message {

	private String messageText;
	private boolean received;
	private Date date;
	
	/**
	 * Creates a message.
	 * 
	 * @param mt	text of the message
	 * @param r		true if user received message, false if user sent message
	 * @param d		date the message was transmitted
	 */
	public Message(String mt, boolean r, Date d) {
		messageText = mt;
		received = r;
		date = d;
	}
	
	public String getText() {
		return messageText;
	}
	
	public boolean isReceived() {
		return received;
	}
	
	public Date getDate() {
		return date;
	}
}
