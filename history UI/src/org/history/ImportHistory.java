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

public class ImportHistory {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static byte[] concat(byte[] A, byte[] B) {
		byte[] C= new byte[A.length+B.length];
		   System.arraycopy(A, 0, C, 0, A.length);
		   System.arraycopy(B, 0, C, A.length, B.length);

		   return C;
		}
	
	static void makeCacheFile(DataOutputStream localDataOutputStream, String myuin) {
	    try {
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
		// TODO Auto-generated method stub
		//String myuin;
		String contactuin = "";
		
		File importDir = new File(dir);
		
	    File outputFile = new File(importDir + "\\" + myuin + ".jpha");
	    DataOutputStream localDataOutputStream = new DataOutputStream(new FileOutputStream(outputFile));
	    
	    
		localDataOutputStream.writeUTF("JPHA");
		localDataOutputStream.writeUTF(myuin);
		

		String[] children = importDir.list();
		
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i];
		        
		        if (filename.equals(myuin + ".jpha"))
		        {
		        	continue;
		        }
		        
		        contactuin = filename.substring(0, filename.length() - 4);
		        
		        InputStream localFile = new FileInputStream(importDir + "\\" +  filename);
		        
				localDataOutputStream.writeUTF(contactuin);		
				
				makeCacheFile(localDataOutputStream, myuin);
				
			    File tempfile = File.createTempFile(contactuin, ".hst");
			    
			    tempfile.deleteOnExit();
			    
			    DataOutputStream tempOutStream = new DataOutputStream(new FileOutputStream(tempfile));
			    
			    try {
					makeHstFile(myuin, contactuin, localFile, tempOutStream);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			    			    
			    readTempFile(tempfile, localDataOutputStream);
		    }
		}

		return true;
	}

	public static void readTempFile(File tempfile,
			DataOutputStream localDataOutputStream) throws IOException,
			FileNotFoundException {
		byte[] buf = new byte[1024];
	    
	    localDataOutputStream.writeInt((int) (tempfile.length()));
	    
	    InputStream tempread = new FileInputStream(tempfile);

	    int len;
	    while ((len = tempread.read(buf)) > 0){
	    	localDataOutputStream.write(buf, 0, len);
	    }
	}


	public static void makeHstFile(String myuin, String contactuin,
			InputStream localFile, DataOutputStream localDataOutputStream) throws ParseException {
		BufferedReader localDataInputStream;
		localDataInputStream = new BufferedReader(new InputStreamReader(localFile));
		  try {
			byte[] strbytes;
			String str = "";
			String str2 = "";
			int strlen = 0;
			Date timestamp = null;
			byte direction = 0;
			boolean firstline = true;
			DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			byte[] newlinebytes = new byte[] { 0x0D, 0x0A, 0x0D, 0x0A };
			while (localDataInputStream.ready())
			  {
				if (str == "")
				{
				str = localDataInputStream.readLine();
				}
				else 
				{
					str = str2;
				}
				if ( str2.startsWith(myuin))
				{
					localDataOutputStream.writeByte(direction);
					localDataOutputStream.writeBoolean(false);
					if (timestamp == null)
					{
					timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
					}
					localDataOutputStream.writeLong(timestamp.getTime());
					
				}
				else if (str2.startsWith(contactuin))
				{
					localDataOutputStream.writeByte(direction);
					localDataOutputStream.writeBoolean(false);
					if (timestamp == null)
					{
					timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
					}
					localDataOutputStream.writeLong(timestamp.getTime());
				}

				if ( str.startsWith(myuin))
				{
					localDataOutputStream.writeByte(0);
					localDataOutputStream.writeBoolean(false);
					
					try {
						if (timestamp == null)
						{
						timestamp = df.parse(str.substring(myuin.length()+2, str.length() -1));
						}
						localDataOutputStream.writeLong(timestamp.getTime());
						firstline = false;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else if (str.startsWith(contactuin))
				{
					localDataOutputStream.writeByte(1);
					localDataOutputStream.writeBoolean(false);
					try {
						if (timestamp == null)
						{
						timestamp = df.parse(str.substring(contactuin.length()+2, str.length() -1));
						}
						localDataOutputStream.writeLong(timestamp.getTime());
						firstline = false;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				else
				{
					strlen = str.length();
					if (str != "")
					{

					str2 = localDataInputStream.readLine();
					
					System.out.println(str);
					System.out.println(str2);
					if (str2 == null)
					{
						strbytes = str.getBytes();
						localDataOutputStream.writeInt(strlen);
						localDataOutputStream.write(strbytes);
						continue;
					}
					
					if ( str2.startsWith(myuin))
					{
						try {
							direction = 0;
							timestamp = df.parse(str2.substring(myuin.length()+2, str2.length() -1));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					else if (str2.startsWith(contactuin))
					{
						try {
							direction = 1;
							timestamp = df.parse(str2.substring(contactuin.length()+2, str2.length() -1));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					while (true)
					{	
						if (str2 == null)
						{
							break;
						}
						if (str2.startsWith(myuin) || str2.startsWith(contactuin))
						{
							break;
						}
						if (str2.length() == 0)
						{
							str = new String(concat(str.getBytes(),newlinebytes));
							strlen += 4;
						}
						else
						{
						str += str2;
						strlen += str2.length();
						}
						str2 = localDataInputStream.readLine();
					}
					}
					
					strbytes = str.getBytes();
					localDataOutputStream.writeInt(strlen);
					localDataOutputStream.write(strbytes);
					

				}
				
				str = "";

			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
