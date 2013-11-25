package blu.model;

import java.util.List;

import ezvcard.types.TelephoneType;

/**
 * This class represents a contact with its properties (first name, last name, number).
 * 
 * @author Torsten Casselt
 */
public class Contact {

	private String firstName, lastName, displayString;
	private List<TelephoneType> numbers;
	
	/**
	 * Creates a contact.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param list		phone numbers of this contact
	 */
	public Contact(String firstName, String lastName, List<TelephoneType> list) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.numbers = list;
		displayString = (lastName != null ? lastName : "") + (lastName != null && firstName != null ? ", " : "") + (firstName != null ? firstName : "");
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public List<TelephoneType> getNumbers() {
		return numbers;
	}
	
	public int getNumberCount() {
		return numbers.size();
	}
	
	/**
	 * Sets the displayed string in the contact list to a new value.
	 * 
	 * @param d	
	 */
	public void setDisplayString(String d) {
		displayString = d;
	}
	
	public String toString() {
		return displayString;
	}
}
