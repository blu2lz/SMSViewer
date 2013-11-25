package blu.model;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import blu.main.SMSViewerMain;

import ezvcard.Ezvcard;
import ezvcard.VCard;

/**
 * List model for contact list that contains all contacts.
 * 
 * @author Torsten Casselt
 */
public class ContactListModel implements ListModel<Contact> {

	private ArrayList<Contact> contactList = new ArrayList<Contact>();
	private ArrayList<ListDataListener> listenerList = new ArrayList<ListDataListener>();
	
	/**
	 * Creates list model by reading contacts from file contacts.csv.
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public ContactListModel() throws IOException {
		// search directory for vcards
		File home = new File(SMSViewerMain.DIRECTORY);
		File[] contactFiles = home.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".vcf");
			}
		});
		// read all vcards
		for (File file : contactFiles) {
			VCard vcard = Ezvcard.parse(file).first();
			contactList.add(new Contact(vcard.getStructuredName().getGiven(), vcard.getStructuredName().getFamily(), vcard.getTelephoneNumbers()));
		}
	}
	
	@Override
	public int getSize() {
		return contactList.size();
	}

	@Override
	public Contact getElementAt(int index) {
		return contactList.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listenerList.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(l);
	}

}
