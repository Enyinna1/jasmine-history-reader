

package org.history;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import org.history.getHistory;

public class History {

	protected static Shell shell;
	Display display;
	Search Search;
	MenuItem mntmSearch_1;
	MenuItem mntmFindNext;
	List uinList;
	StyledText styledText;
	String myuin = "";
	String[] uinListArray;
	String searchKeyword = "";
	Vector<Integer> keywordOffsetArray = new Vector<Integer>();
	Vector<HistoryItem> historyVector;
	HashSet<String> uinListHashArray;
	public String[] items;
	public Iterator<HistoryItem> itr;
	int offsetIndex = -1;
	Thread t;



	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args)  {
		try {
			History window = new History();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(679, 786);
		shell.setText("Jasmine History Reader");
		shell.setLayout(new GridLayout(2, false));
		
		Search = new Search(shell);
		Search.addFindListener(new FindListener () {
			public boolean find() {
				return findEntry();
			}
		});
			
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("File");
		
		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		
		MenuItem mntmOpen = new MenuItem(menu_1, SWT.NONE);
		mntmOpen.setText("&Open\tCtrl+O");
		mntmOpen.setAccelerator(SWT.CTRL + 'O');
		
		mntmOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)  {
		        FileDialog fd = new FileDialog(shell, SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.jpha","*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		          

		        if (selected == null)
		        {
		        	return;
		        }
		          
		        // clear list with uins, to prevent uins in list of different history files
		        uinList.removeAll();
		          
		        Vector[] result = null;
				try {
					result = getHistory.processHistory(selected);
				} catch (IOException e1) {
					showMessage("Wrong file type.");
					e1.printStackTrace();
					return;
				}
				// result[0] contains history from cache file.
				historyVector = result[1];		
				
				Iterator<HistoryItem> itr = historyVector.iterator();
				uinListHashArray = new HashSet<String>();
				
				// process history items, get profile uin, contact uins
				while (itr.hasNext())
				{
					HistoryItem item = (HistoryItem)itr.next();
					myuin = item.myuin.toString();
					uinListHashArray.add(item.contactuin);			
				}				
					
				uinListArray = uinListHashArray.toArray(new String[0]);
				
				//add items to List UI
				for (int i = 0; i < uinListArray.length;i++)
				{
					uinList.add(uinListArray[i].toString());
				}
				
				mntmSearch_1.setEnabled(true);
		        }
			
			});
		

		MenuItem mntmImportHistory = new MenuItem(menu_1, SWT.NONE);
		mntmImportHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		mntmImportHistory.setText("Import History");
		
		MenuItem mntmExportHistory = new MenuItem(menu_1, SWT.NONE);
		mntmExportHistory.setText("Export History");
		
		mntmExportHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)  {
		          if (historyVector == null)
		          {
		        	  showMessage("Open file with history first.");
		        	  return;
		          }
		          DirectoryDialog dlg = new DirectoryDialog(shell);
		          dlg.setText("Export history");
		          dlg.setFilterPath("C:/");
		          String selected = dlg.open();
		          System.out.println(selected);
		          if (selected == null)
		          {
		        	  return;
		          }
		          for (int i=0; i<uinListArray.length;i++)
		          {
			          Iterator<HistoryItem> itr = historyVector.iterator();
		        	  String uin = uinListArray[i].toString();
						FileWriter out2 = null;
						try {
							out2 = new FileWriter(selected + "\\" + uin +".txt",false);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						BufferedWriter out = new BufferedWriter(out2);
		        	  while(itr.hasNext())
		        	  {
		        		  HistoryItem item = (HistoryItem)itr.next();
		        		  if (item.contactuin.equals(uin))
		        		  {

								if (item.direction == 1)
								 {
									try {
										out.write(item.contactuin + " " + "(" + getHistory.formatDate(item.date) +")");
										out.newLine();
										out.write(item.message);
										out.newLine();

									} catch (IOException e1) {
										e1.printStackTrace();
									}
								 }
								 else
								 {
									 try {
										out.write(item.myuin + " " + "(" + getHistory.formatDate(item.date) +")");
										out.newLine();
										out.write(item.message);
										out.newLine();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								 }
		        		  }
		        	  }
		        	  try {
						out.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		          }
		          showMessage("Done.");
			} 
			});
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.setText("Exit");
		
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.getDisplay().dispose();
				System.exit(0);
			}
		});

		
		MenuItem mntmSearch = new MenuItem(menu, SWT.CASCADE);
		mntmSearch.setText("Search");
		
		Menu menu_2 = new Menu(mntmSearch);
		mntmSearch.setMenu(menu_2);
		
		mntmSearch_1 = new MenuItem(menu_2, SWT.NONE);

		mntmSearch_1.setText("Search...\tCtrl+F");
		mntmSearch_1.setAccelerator(SWT.MOD1 + 'F');
		mntmSearch_1.setEnabled(false);
		
		mntmSearch_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Open search Dialog
				Search.setMatchCase(false);
				Search.setSearchString("");
				Search.open();
			}
		});
		
		mntmFindNext = new MenuItem(menu_2, SWT.NONE);
		mntmFindNext.setText("Find next\tF3");
		mntmFindNext.setAccelerator(SWT.F3);
		mntmFindNext.setEnabled(false);
		
		mntmFindNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
				// Quite weird Find-next implementation. offsetIndex - global variable that points to current element in offset array. Method sets current line to line with the keyword
				styledText.setTopIndex(styledText.getLineAtOffset((Integer) keywordOffsetArray.get(offsetIndex +1) - searchKeyword.length()));
				offsetIndex++;
				} catch (ArrayIndexOutOfBoundsException e1)
				{
					showMessage("Reached end. Starting from the beginning");
					offsetIndex = -1;
				}
			}
		});

	
		GridData gd_list = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_list.widthHint = 87;
		gd_list.heightHint = 717;
		
		uinList = new List(shell, SWT.BORDER);
		uinList.setLayoutData(gd_list);
		uinList.addListener(SWT.Selection, new Listener () {
			
			 @Override
			 public void handleEvent (Event e) {
				if(t!=null)
				{
				t.interrupt();
				}
				// clear found keyword offsets ( to prevent search results from previous opened contact )
		    	keywordOffsetArray.clear();
		    	
		    	mntmFindNext.setEnabled(false);
		    	
		    	// Clear text field
		    	styledText.setText("");
		    	
		    	// get selected uin
		    	items = uinList.getSelection();
		    	
		    	itr = historyVector.iterator();

		    	// start new thread which prints history messages
				t = new Thread(new textThread());
				t.start();
		     }
		 });
		
		styledText = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setWordWrap(!styledText.getWordWrap());
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		styledText.addLineStyleListener(new LineStyleListener() {
		        public void lineGetStyle(LineStyleEvent event) {
		          // print sent messages in blue
		          if(event.lineText.startsWith(myuin)) {
		            event.styles = new StyleRange[1];
			          String line = event.lineText;
			          
			          int length = line.length();
			          
			          Color blue = display.getSystemColor(SWT.COLOR_BLUE);
	          
			          event.styles[0] = new StyleRange(event.lineOffset, length, blue, null);
			          event.styles[0].fontStyle=SWT.BOLD;

		            return;
		          }
		          // color incoming messages in red
		          else
		          {
		        	  for ( int i=0; i<uinListArray.length;i++)
		        	  {
			          if(event.lineText.startsWith(uinListArray[i].toString())) {
				            event.styles = new StyleRange[1];
					          String line = event.lineText;
					          int length = line.length();
					          
					          Color red = display.getSystemColor(SWT.COLOR_RED);
					          
					          event.styles[0] = new StyleRange(event.lineOffset, length, red, null);
					          event.styles[0].fontStyle=SWT.BOLD;
				            return;
				          }
		        	  }
		          }
		          
		          // search highlighting		          
		          if(searchKeyword == null || searchKeyword.length() == 0) {
			            event.styles = new StyleRange[0];
			            return;
			          }
		          	// if Match Case set to false - convert every line to lower case
			          if (!Search.getMatchCase())
			          {
			        	  String line = event.lineText.toLowerCase();
			        	  int cursor = -1;
			        	  searchKeyword = searchKeyword.toLowerCase();		          
			        	  LinkedList<StyleRange> list = new LinkedList<StyleRange>();
			        	  if ( (cursor = line.indexOf(searchKeyword, cursor+1)) >= 0) {
			        		  list.add(getHighlightStyle(event.lineOffset+cursor, searchKeyword.length()));
			        	  }			          		          
			        	  event.styles = (StyleRange[]) list.toArray(new StyleRange[list.size()]);
			          }
			          // when Match Case enabled
			          else
			          {
			        	  String line = event.lineText;
			        	  int cursor = -1;		          
			        	  LinkedList<StyleRange> list = new LinkedList<StyleRange>();
			        	  if ( (cursor = line.indexOf(searchKeyword, cursor+1)) >= 0) {
			        		  list.add(getHighlightStyle(event.lineOffset+cursor, searchKeyword.length()));
			        	  }			          		          
			        	  event.styles = (StyleRange[]) list.toArray(new StyleRange[list.size()]);
			          }
		        }
		      });
		    
	}
	// method called every search
	protected boolean findEntry() {
			keywordOffsetArray = new Vector<Integer>();
			boolean matchCase = Search.getMatchCase();
			searchKeyword = Search.getSearchString();
			
			styledText.redraw();
			
			String str;
			Pattern p;
			
			if (!matchCase)
			{
				str = styledText.getText().toLowerCase();
				p = Pattern.compile(searchKeyword.toLowerCase());
			}
			else
			{
				str = styledText.getText();
				p = Pattern.compile(searchKeyword);
			}
			
			Matcher m = p.matcher(str);

			while (m.find()) { // find next match
			    int match = m.start();
			    if (!keywordOffsetArray.contains(m.start()))
	            {
			    	// add offset of match to array
	            	keywordOffsetArray.add(match);
	            }
			}
				// jump to first match
		        if (!keywordOffsetArray.isEmpty())
		        {
					if (!searchKeyword.isEmpty())
					{
						mntmFindNext.setEnabled(true);
					}
					
					styledText.setTopIndex(styledText.getLineAtOffset(keywordOffsetArray.get(0) - searchKeyword.length()));
					offsetIndex++;
					Search.shlSearch.setVisible(false);
		        	return true;

		        }
		        else
		        {
		        	return false;
		        }
	}

	public static void showMessage(String message) {
		MessageBox dialog = new MessageBox(shell);
		dialog.setMessage(message);
		dialog.open();
	}
	
	private StyleRange getHighlightStyle(int startOffset, int length) {
		    StyleRange styleRange = new StyleRange();
		    styleRange.start = startOffset;
		    styleRange.length = length;
		    styleRange.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
		    return styleRange;
		  }

	public class textThread extends Thread {
		public void run() {
    		while (itr.hasNext())
    		{
    			final HistoryItem item = (HistoryItem)itr.next();
            try {
              Thread.sleep((long) 0.1);
            } catch (Throwable th) {
            }
            if (display.isDisposed())
              break;
            display.syncExec (new Runnable () {
                public void run () {	
                	try
                	{
	    			if (item.contactuin.equals(items[0]))
	    			{
	    				 if (item.direction == 1)
	    				 {
	    					 styledText.append(item.contactuin + " " + "(" + getHistory.formatDate(item.date) +")" +"\n");
	    					 styledText.append(item.message+"\n");
	    				 }
	    				 else
	    				 {
	    					 styledText.append(item.myuin + " " + "(" + getHistory.formatDate(item.date) +")"+"\n");
	    					 styledText.append(item.message+"\n");
	    				 }
	    			}
                	} 
                	catch (ArrayIndexOutOfBoundsException e1)
                	{
                		e1.printStackTrace();
                	}
                }
             });
          }
		}
	}
}

