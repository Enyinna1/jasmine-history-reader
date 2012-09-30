package org.history;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

public class getHistory {
	
	public static Vector[] processHistoryJPHA(String selected) throws IOException {
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
	
	public static Vector[] processHistoryJHA2(String selected) throws Exception {
	    Vector<HistoryItem> localVector = new Vector<HistoryItem>();
	    Vector<HistoryItem> localVector2 = new Vector<HistoryItem>();
		File localFile = new File(selected);
	    DataInputStream localDataInputStream;
	    if (localFile.length() > 0L)
	      {
	        localDataInputStream = new DataInputStream(new FileInputStream(localFile));
	        
	        // read JHA2 header
	        localDataInputStream.readInt();
	        
	        // read UIN of profile
	        String uin = readUIN(localDataInputStream);
	        
	        //TODO read unused 2 bytes ( WTF? Contact type? )
	        localDataInputStream.readShort();

	          while (localDataInputStream.available() > 0)
	          {	        	 
	        	  // read UIN of contact
	        	  String contactuin = readUIN(localDataInputStream);

	        	  // read length of hst/cache file. 4 bytes.
		          int length = localDataInputStream.readInt();	                    
		          if (length != 0)
		          {
		        	  // we are going to process cache file.
		        	  // u will store number of bytes read
		        	  int u = 0;

		        	  boolean JHA = false;
		        	  while(u < length)
		        	  {
			        		// recieved/sent message flag if normal or it will be 55 ( ASCII "U" ) if cache starts with UNI
			        	    int i = localDataInputStream.readByte();
			        	    			        	    			        	    
			        	    if ( i == 0x55 || JHA == true)
			        	    {
			        	    	if ( JHA == false )
			        	    	{
			        	    		// this will run only once in the beginning if cache starts with UNI
			        	    		
			        	    		// if cache starts with U - skip next 2 bytes ( NI )
			        	    		localDataInputStream.skip(2L);
			        	    		// read recieved/sent message flag
				        	    	i = localDataInputStream.readByte();	
				        	    	// set number of readen bytes to 3 ( because of UNI header )
			        	    		u = 3;
			        	    		//  set flag to true
			        	    		JHA = true;
			        	    	}
		        	    				        	    	
			        	    	//  xtraz flag
						        boolean bool = localDataInputStream.readBoolean();
						        // unused bytes
						        localDataInputStream.readInt();
						        // data and time in unixtime format
						        long l = localDataInputStream.readLong();
						        // length of string
						        int j = localDataInputStream.readInt();
						        // if cache starts with UNI - all strings are unicode, so we use different method
						        String str2 = readString(localDataInputStream, j);
						        
					            HistoryItem messageContainer = new HistoryItem();
					            messageContainer.myuin = uin;
					            messageContainer.contactuin = contactuin.toString();
					            messageContainer.direction = i;
					            messageContainer.confirmed = true;
					            messageContainer.message = str2;
					            System.out.println(str2);
					            messageContainer.date = l;
					            messageContainer.isXtrazMessage = bool;
					            localVector.add(messageContainer);
					    		// 18 = int+bool+long+int+int(1+1+8+4+4). j - number of bytes in string array
					    		u = u + 18 + j;
			        	    }
			        	    else
			        	    {
			        	    	// is cache is normal and doesnt start with UNI
			        	    	
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
				            	System.out.println(str2);
				            	messageContainer.date = l;
				            	messageContainer.isXtrazMessage = bool;
				          	  	localVector.add(messageContainer);
				          	  	// 14 = int+bool+long+int(1+1+8+4). j - number of bytes in string array
				          	  	u = u + 14 + j;
			        	    }
		        	  }
		        	  
		        	  // proccessing hst file. Just like previous one.
		        	  int lengthhst = localDataInputStream.readInt();
		        	  
		        	  // there are many hst files with only UNI string in it - so we skip it
		        	  if (lengthhst == 0x03)
		        	  {		     		  
		        		  continue;
		        	  }
		        	  
		        	  // u stores the number of bytes read.
		        	  int uhst = 0;
		        	  
		        	  // recieved/sent message flag if normal or it will be 55 ( ASCII "U" ) if hst starts with UNI
		        	  JHA = false;
		        	  while(uhst < lengthhst)
		        	  {
		        		// recieved/sent message flag
			        	int i = localDataInputStream.readByte();
			        	
		        	    if ( i == 0x55 || JHA == true)
		        	    {
		        	    	if ( JHA == false )
		        	    	{
		        	    		// this will run only once in the beginning if cache starts with UNI
		        	    		
		        	    		// if cache starts with U - skip next 2 bytes ( NI )
		        	    		localDataInputStream.skip(2L);
		        	    		// read recieved/sent message flag
			        	    	i = localDataInputStream.readByte();	
			        	    	// set number of readen bytes to 3 ( because of UNI header )
		        	    		u = 3;
		        	    		//  set flag to true
		        	    		JHA = true;
		        	    	}
		        	    }
			        	
			        	//  xtraz flag
				        boolean bool = localDataInputStream.readBoolean();
				        localDataInputStream.readInt();
				        // data and time in unixtime format
				        long l = localDataInputStream.readLong();
				        // length of string
				        int j = localDataInputStream.readInt();
				        // if cache starts with UNI - all strings are unicode, so we use different method
				        String str2 = readString(localDataInputStream, j);
			            
			            HistoryItem messageContainer = new HistoryItem();
			            messageContainer.contactuin = contactuin.toString();
			            System.out.println(str2);
			            messageContainer.myuin = uin;
			            messageContainer.direction = i;
			            messageContainer.confirmed = true;
			            messageContainer.message = str2;
			            messageContainer.date = l;
			            messageContainer.isXtrazMessage = bool;
			            localVector2.add(messageContainer);
			    		uhst = uhst + 18 + j;			    		
		        	  }
		          }
		          else
		          {
		        	  // this will run if cache file doesn't exist
		        	  
		        	  // proccessing hst file. Just like previous one.
		        	  int lengthhst = localDataInputStream.readInt();
		        	  
		        	  localDataInputStream.skip(3L);
		        	  
		        	  if (lengthhst == 0x03)
		        	  {		        		  
		        		  continue;
		        	  }
		        	  
		        	  int uhst = 0;
		        	  while(uhst < lengthhst)
		        	  {
		        		// recieved/sent message flag
			        	int i = localDataInputStream.readByte();
			        	//  xtraz flag
				        boolean bool = localDataInputStream.readBoolean();
				        localDataInputStream.readInt();
				        // data and time in unixtime format
				        long l = localDataInputStream.readLong();
				        // length of string
				        int j = localDataInputStream.readInt();
				        String str2 = readString(localDataInputStream, j);
			            
			            HistoryItem messageContainer = new HistoryItem();
			            messageContainer.contactuin = contactuin.toString();
			            messageContainer.myuin = uin;
			            messageContainer.direction = i;
			            messageContainer.confirmed = true;
			            messageContainer.message = str2;
			            messageContainer.date = l;
			            messageContainer.isXtrazMessage = bool;
			            localVector2.add(messageContainer);
			    		uhst = uhst + 14 + j;
			    		
		        	  }
		          }
	          }
	      }
          

	return new Vector[] {localVector,localVector2};
}
	
	public static final String readString(DataInputStream InputStream, int length) 
			throws Exception
			{
				StringBuilder localStringBuilder = new StringBuilder();
				for (int i = 0; ; i += 2)
			    {
			      if (i >= length)
			        return localStringBuilder.toString();
			      int j = InputStream.readByte();
			      localStringBuilder.append((char)(InputStream.readByte() | j << 8));
			    }
			}
		
	public static final String readUIN(DataInputStream InputStream)
				    throws Exception
				  {
				    StringBuilder localStringBuilder = new StringBuilder();
				    int i = InputStream.readInt();
				    
				    for (int j = 0; ; j += 2)
				    {
				      if (j >= i)
				        return localStringBuilder.toString();
				      int k = InputStream.readByte();
				      localStringBuilder.append((char)(InputStream.readByte() | k << 8));
				    }
				  }
	
	public static String formatDate(long l) {
		Date d = new Date(l);
		DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		
		return df.format(d);
	}

}
