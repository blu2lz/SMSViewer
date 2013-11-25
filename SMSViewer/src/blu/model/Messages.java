package blu.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import blu.main.SMSViewerMain;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Saves messages for each number in a hash map of array lists.
 * 
 * @author Torsten Casselt
 */
public class Messages {

	HashMap<Contact,ArrayList<Message>> messages = new HashMap<Contact,ArrayList<Message>>();

	public Messages(HashMap<Number, Contact> numberMap) throws IOException, ParseException {
		// search directory for messages
		File home = new File(SMSViewerMain.DIRECTORY);
		File[] contactFiles = home.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".csv");
			}
		});
		// read all message files
		for (File file : contactFiles) {
			CSVReader reader = new CSVReader(new FileReader(file));
		    String[] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		    	// parse transmission date and number
	            DateFormat df = new SimpleDateFormat("yyyy.MM.dd kk:mm");
	            Date transmissionDate =  df.parse(nextLine[5]);
	            long number = Long.parseLong(nextLine[nextLine[1].equals("READ,RECEIVED") ? 2 : 3]);
	            ArrayList<Message> messageList;
	            Contact contact = numberMap.get(number);
	            // contact exists for given number
	            if (contact != null) {
		            // if at least one message already exists for that number, take old array list. Else, create a new one.
		            if ((messageList = messages.get(contact)) == null) {
		            	messageList = new ArrayList<Message>();
		            }
		            messageList.add(new Message(nextLine[7], nextLine[1].equals("READ,RECEIVED") ? true : false, transmissionDate));
			    	messages.put(contact, messageList);
	            } else {
	            	// contact does not exist, so save only number
	            	//TODO: how to show numbers without contacts?
	            }
		    }
		    reader.close();
		}
	}

	public ArrayList<Message> getMessages(Contact contact) {
		return messages.get(contact);
	}
}
