package org.history;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

public class getHistory {
	
	public static Vector[] processHistory(String selected) throws IOException {
	    Vector<HistoryItem> localVector = new Vector<HistoryItem>();
	    Vector<HistoryItem> localVector2 = new Vector<HistoryItem>();
		File localFile = new File(selected);
	    DataInputStream localDataInputStream;
	    if (localFile.length() > 0L)
	      {
	        localDataInputStream = new DataInputStream(new FileInputStream(localFile));
	        // read JPHA header

	        localDataInputStream.readUTF();
	        // read UIN of profile
	        String uin = localDataInputStream.readUTF();
	          while (localDataInputStream.available() > 0)
	          {
	        	  // read UIN of contact
	        	  String contactuin = localDataInputStream.readUTF();
	        	  if (contactuin.equals(""))
	        	  	{
	        		  // readUTF() reads two bytes ( they point to length of string ). These two for those cases when between uin and length pointer are more than 4 bytes.
	        		  contactuin = localDataInputStream.readUTF();
	        		  contactuin = localDataInputStream.readUTF();
	        	  	}
	        	  // read length of hst/cache file. 4 bytes.
		          int length = localDataInputStream.readInt();	                    
		          if (length == 0)
		          {
		        	  continue;
		          }
		          else
		          {
		        	  // we are going to process cache file.
		        	  int u = 0;
		        	  // u will store number of bytes read
		        	  while(u < length)
		        	  {
		        		// recieved/sent message flag
		        	    int i = localDataInputStream.readByte();
		        	    //  xtraz flag
			            boolean bool = localDataInputStream.readBoolean();
			            // data and time in unixtime format
			            long l = localDataInputStream.readLong();
			            // length of string
			            int j = localDataInputStream.readInt();
			            // byte array with string
			            byte[] arrayOfByte = new byte[j];
			            localDataInputStream.read(arrayOfByte, 0, j);
			            String str2 = new String(arrayOfByte, "windows-1251");
			            HistoryItem messageContainer = new HistoryItem();
			            messageContainer.myuin = uin;
			            messageContainer.contactuin = contactuin.toString();
			            messageContainer.direction = i;
			            messageContainer.confirmed = true;
			            messageContainer.message = str2;
			            messageContainer.date = l;
			            messageContainer.isXtrazMessage = bool;
			            localVector.add(messageContainer);
			    		// 14 = int+bool+long+int(1+1+8+4). j - number of bytes in string array
			    		u = u + 14 + j;
			    		
		        	  }
		        	  // proccessing hst file. Just like previous one.
		        	  int lengthhst = localDataInputStream.readInt();
		        	  int uhst = 0;
		        	  while(uhst < lengthhst)
		        	  {
		        	    int i = localDataInputStream.readByte();
			            boolean bool = localDataInputStream.readBoolean();
			            long l = localDataInputStream.readLong();
			            int j = localDataInputStream.readInt();
			            byte[] arrayOfByte = new byte[j];
			            localDataInputStream.read(arrayOfByte, 0, j);
			            String str2 = new String(arrayOfByte, "windows-1251");
			            

			            HistoryItem messageContainer = new HistoryItem();
			            messageContainer.contactuin = contactuin.toString();
			            messageContainer.myuin = uin;
			            messageContainer.direction = i;
			            messageContainer.confirmed = true;
			            messageContainer.message = str2;
			            messageContainer.date = l;
			            messageContainer.isXtrazMessage = bool;
			            
			            // debug
//						if (messageContainer.direction == 1)
//						 {
//							System.out.println(messageContainer.contactuin + " " + "(" + getHistory.formatDate(messageContainer.date) +")");
//							System.out.println(messageContainer.message);
//						 }
//						 else
//						 {
//							 System.out.println(messageContainer.myuin + " " + "(" + getHistory.formatDate(messageContainer.date) +")");
//							 System.out.println(messageContainer.message);
//						 }
						
			            localVector2.add(messageContainer);
			    		uhst = uhst + 14 + j;
			    		
		        	  }
		          }
          
	          }
	      }

	return new Vector[] {localVector,localVector2};
}
	
	public static String formatDate(long l) {
		Date d = new Date(l);
		DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		
		return df.format(d);
	}

}
