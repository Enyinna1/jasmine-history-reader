package org.history;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class ImportHistory {
	
	static DateDialog DateDialog;

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	// concat two byte arrays
	static byte[] concat(byte[] A, byte[] B) {
		byte[] C= new byte[A.length+B.length];
		   System.arraycopy(A, 0, C, 0, A.length);
		   System.arraycopy(B, 0, C, A.length, B.length);

		   return C;
		}
	
	static void makeCacheFile(DataOutputStream localDataOutputStream, String myuin) {
	    try {
	    	// write useless history item to .cache, it shows in main chat window
	    	// TODO rework this to get proper message from contact ( prefer last one )
			localDataOutputStream.writeInt(26);
			localDataOutputStream.writeByte(0);
			localDataOutputStream.writeBoolean(false);
			localDataOutputStream.writeLong(1332959825497L);
			String cachestring = "Open history";
			localDataOutputStream.writeInt(cachestring.length());
			localDataOutputStream.write(cachestring.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean importHistory(String myuin, String dir) throws IOException {

		String contactuin = "";
		
		File importDir = new File(dir);
		
		// make output file in dir with history files
	    File outputFile = new File(importDir + "\\" + myuin + ".jpha");
	    
	    DataOutputStream localDataOutputStream = new DataOutputStream(new FileOutputStream(outputFile));
	    
	    // write header and users uin
		localDataOutputStream.writeUTF("JPHA");
		localDataOutputStream.writeUTF(myuin);
		
		// get files in directory
		String[] children = importDir.list();
		
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i];
		        
		        // if there already output file - skip it
		        if (filename.equals(myuin + ".jpha"))
		        {
		        	continue;
		        }
		        
		        // get contact uin - strip extension of filename
		        contactuin = filename.substring(0, filename.length() - 4);
		        
		        InputStream localFile = new FileInputStream(importDir + "\\" +  filename);
		        
		        
				localDataOutputStream.writeUTF(contactuin);		
				
				// write cache to jpha file
				makeCacheFile(localDataOutputStream, myuin);
				
				// make a temp file for history file ( to get proper history length )
			    File tempfile = File.createTempFile(contactuin, ".hst");
			    
			    tempfile.deleteOnExit();
			    
			    DataOutputStream tempOutStream = new DataOutputStream(new FileOutputStream(tempfile));
			    
			    try {
			    	// write proper history to temp file
					makeHstFile(myuin, contactuin, localFile, tempOutStream);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			    
			    // write temp file to jpha file
			    readTempFile(tempfile, localDataOutputStream);
		    }
		}

		return true;
	}

	public static void readTempFile(File tempfile,
			DataOutputStream localDataOutputStream) throws IOException,
			FileNotFoundException {
		
		byte[] buf = new byte[1024];
		
		// write filesize to jpha file
	    localDataOutputStream.writeInt((int) (tempfile.length()));
	    
	    InputStream tempread = new FileInputStream(tempfile);
	    
	    // copy contents
	    int len;
	    while ((len = tempread.read(buf)) > 0){
	    	localDataOutputStream.write(buf, 0, len);
	    }
	}


	public static void makeHstFile(String myuin, String contactuin,
			InputStream localFile, DataOutputStream localDataOutputStream) throws ParseException {
		BufferedReader localDataInputStream;
		
		// read local .txt history file
		localDataInputStream = new BufferedReader(new InputStreamReader(localFile));
		  try {
			byte[] strbytes;
			String str = "";
			String str2 = "";
			int strlen = 0;
			Date timestamp = null;
			byte direction = 0;
			DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			
			// 0d0a0d0a byte string - DOS new string ( needed to correctly parse multi-lined messages )
			byte[] newlinebytes = new byte[] { 0x0D, 0x0A, 0x0D, 0x0A };
			
			while (localDataInputStream.ready())
			  {
				// this cycle is quite weird to understand O_o
				// TODO refactor and optimize this shit
				
				//read new line
				str = localDataInputStream.readLine();				
			
				// str2 contains next line - needed to correctly read multilined messages		
			
				// if str2 starts with uins - properly add direction and timestamp info
				if (str2.startsWith(myuin + " ("))
				{
					localDataOutputStream.writeByte(direction);
					localDataOutputStream.writeBoolean(false);
					if (timestamp == null)
					{
					timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
					}
					
					if (timestamp.getTime() > 1577826000000L || timestamp.getTime() < 946674000000L )
					{
						// TODO complete this
						
						DateDialog = new DateDialog(History.shell);
															
						DateDialog.setWrongTimestamp(str2);
						DateDialog.setWrongDate(str2);
						DateDialog.setCorrectTimestamp(str2);
						str2 = DateDialog.open();
						
						timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
					}
					
					localDataOutputStream.writeLong(timestamp.getTime());
					
				}
				// if str2 starts with uins - properly add direction and timestamp info
				else if (str2.startsWith(contactuin + " ("))
				{
					localDataOutputStream.writeByte(direction);
					localDataOutputStream.writeBoolean(false);
					if (timestamp == null)
						
					{
					timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
					}
					
					if (timestamp.getTime() > 1577826000000L || timestamp.getTime() < 946674000000L )
					{
						// TODO complete this

						DateDialog = new DateDialog(History.shell);
						
						DateDialog.setWrongTimestamp(str2);
						DateDialog.setWrongDate(str2);
						DateDialog.setCorrectTimestamp(str2);
						str2 = DateDialog.open();
					
						
						
						timestamp = df.parse(str2.substring(contactuin.length()+2, str2.length() -1));
					}
					
					localDataOutputStream.writeLong(timestamp.getTime());
				}

				// if str starts with uins - parse data and time info and write it to output stream, along with direction info
				if (str.startsWith(myuin + " ("))
				{
					localDataOutputStream.writeByte(0);
					localDataOutputStream.writeBoolean(false);
					
					try {
						if (timestamp == null)
						{
						timestamp = df.parse(str.substring(myuin.length()+2, str.length() -1));
						}
						
						if (timestamp.getTime() > 1577826000000L || timestamp.getTime() < 946674000000L )
						{
							// TODO complete this
							
							DateDialog = new DateDialog(History.shell);
																
							DateDialog.setWrongTimestamp(str2);
							DateDialog.setWrongDate(str2);
							DateDialog.setCorrectTimestamp(str2);
							str2 = DateDialog.open();
							
							timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
						}
						
						localDataOutputStream.writeLong(timestamp.getTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				// if str starts with uins - parse data and time info and write it to output stream, along with direction info
				else if (str.startsWith(contactuin + " ("))
				{
					localDataOutputStream.writeByte(1);
					localDataOutputStream.writeBoolean(false);
					try {
						if (timestamp == null)
						{
						timestamp = df.parse(str.substring(contactuin.length()+2, str.length() -1));
						}
						
						if (timestamp.getTime() > 1577826000000L || timestamp.getTime() < 946674000000L )
						{
							// TODO complete this

							DateDialog = new DateDialog(History.shell);
							
							DateDialog.setWrongTimestamp(str2);
							DateDialog.setWrongDate(str2);
							DateDialog.setCorrectTimestamp(str2);
							str2 = DateDialog.open();
						
							
							
							timestamp = df.parse(str2.substring(contactuin.length()+2, str2.length() -1));
						}
						
						localDataOutputStream.writeLong(timestamp.getTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// if string doesnt start with uin - it's usually a direct message
				else
				{

					strlen = str.length();
					
					if (str != "")
					{
						// read next line - to know whether this is a multiline message or just single line )
						str2 = localDataInputStream.readLine();

						// debug
						System.out.println(str);
						System.out.println(str2);
						
						// if str2 = null -> EOF - write all info to output stream
						if (str2 == null)
						{
							strbytes = str.getBytes();
							localDataOutputStream.writeInt(strlen);
							localDataOutputStream.write(strbytes);
							continue;
						}

						// if str2 starts with uin - store direction and timestamp in global variables because its a new message and we need to process it later )
						if ( str2.startsWith(myuin + " ("))
						{
							try {
								direction = 0;
								timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
								
								if (timestamp.getTime() > 1577826000000L || timestamp.getTime() < 946674000000L )
								{
									// TODO complete this
									
									DateDialog = new DateDialog(History.shell);
																		
									DateDialog.setWrongTimestamp(str2);
									DateDialog.setWrongDate(str2);
									DateDialog.setCorrectTimestamp(str2);
									str2 = DateDialog.open();
									
									timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						
						// if str2 starts with uin - store direction and timestamp in global variables because its a new message and we need to process it later )
						else if (str2.startsWith(contactuin + " ("))
						{
							try {
								direction = 1;
								timestamp = df.parse(str2.substring(contactuin.length()+2, str2.length() -1));
								
								if (timestamp.getTime() > 1577826000000L || timestamp.getTime() < 946674000000L )
								{
									// TODO complete this

									DateDialog = new DateDialog(History.shell);
									
									DateDialog.setWrongTimestamp(str2);
									DateDialog.setWrongDate(str2);
									DateDialog.setCorrectTimestamp(str2);
									str2 = DateDialog.open();
								
									
									
									timestamp = df.parse(str2.substring(contactuin.length()+2, str2.length() -1));
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						// cycle to read multiline messages
						while (true)
						{	
							// EOF - stop
							if (str2 == null)
							{
								break;
							}
							
							// if str2 start with uin - stop
							if (str2.startsWith(myuin + " (") || str2.startsWith(contactuin + " ("))
							{
								break;
							}
							
							// if length = 0 - DOS new line ( 0d0a0d0a ). Concat str byte array and 0d0a0d0a byte[], increment string length
							if (str2.length() == 0)
							{
								str = new String(concat(str.getBytes(),newlinebytes));
								strlen += 4;
							}
							
							// usual case - append second line to first and increment length of string
							else
							{
								str += str2;
								strlen += str2.length();
							}
							
							// read line for next iteration
							str2 = localDataInputStream.readLine();
						}
					}
					
					// write str length and byte array to output stream
					strbytes = str.getBytes();
					localDataOutputStream.writeInt(strlen);
					localDataOutputStream.write(strbytes);
					

				}
				

			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			History.showMessage("Something went wrong, import failed");
			e.printStackTrace();
		}
	}

}
