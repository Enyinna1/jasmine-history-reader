package org.history;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.wb.swt.SWTResourceManager;

public class DateDialog  {
	Display display;
	Shell shlSearch;
	Text searchText;
	Label searchAreaLabel;
	Button findButton;
	FindListener findHandler;
	private GridLayout gl_shlSearch;
	private Label lblNewLabel;
	private Label lblNewTimestamp;
	private Text text;
	private Label lblWrongData;
	private Label lblCorrectData;
	private Text text_1;
	private Text text_2;
	public boolean Done = false;

/**
 * Class constructor that sets the parent shell and the styledText widget that
 * the dialog will search.
 *
 * @param parent	Shell 
 *			The shell that is the parent of the dialog.
 */
public DateDialog(Shell parent) {
	display = Display.getDefault();
	shlSearch = new Shell(parent, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.BORDER | SWT.TITLE);
	GridLayout layout;
	gl_shlSearch = new GridLayout();
	gl_shlSearch.numColumns = 2;
	shlSearch.setLayout(gl_shlSearch);
	shlSearch.setText("Date Error");
	shlSearch.addShellListener(new ShellAdapter(){
		public void shellClosed(ShellEvent e) {
			// don't dispose of the shell, just hide it for later use
			e.doit = false;
			shlSearch.setVisible(false);
		}
	});
	
	lblNewLabel = new Label(shlSearch, SWT.NONE);
	lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
	lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
	lblNewLabel.setText("Error found in timestamp, date is wrong. Correct it and press Done.");
		
	Label lblSearchWhat = new Label(shlSearch, SWT.LEFT);
	lblSearchWhat.setText("Wrong timestamp:");	
	searchText = new Text(shlSearch, SWT.BORDER);
	GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	gridData.widthHint = 200;
	searchText.setLayoutData(gridData);
	searchText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			boolean enableFind = (searchText.getCharCount() != 0);
			findButton.setEnabled(enableFind);
		}
	});
	
	lblWrongData = new Label(shlSearch, SWT.NONE);
	lblWrongData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblWrongData.setText("Wrong Data:");
	
	text_1 = new Text(shlSearch, SWT.BORDER);
	text_1.setEnabled(false);
	text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	searchAreaLabel = new Label(shlSearch, SWT.LEFT);
	new Label(shlSearch, SWT.NONE);
	
	lblNewTimestamp = new Label(shlSearch, SWT.NONE);
	lblNewTimestamp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblNewTimestamp.setText("New timestamp:");
	
	text = new Text(shlSearch, SWT.BORDER);
	text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	
	lblCorrectData = new Label(shlSearch, SWT.NONE);
	lblCorrectData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblCorrectData.setText("Correct Data:");
	
	text_2 = new Text(shlSearch, SWT.BORDER);
	text_2.setEnabled(false);
	text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Composite composite = new Composite(shlSearch, SWT.NONE);
	gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gridData.horizontalSpan = 2;
	composite.setLayoutData(gridData);
	layout = new GridLayout();
	layout.numColumns = 2;
	layout.makeColumnsEqualWidth = true;
	composite.setLayout(layout);
	
	findButton = new Button(composite, SWT.PUSH);
	findButton.setText("Done");
	findButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	findButton.setEnabled(false);
	findButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
				Done = true;		
		}
	});
			
	Button cancelButton = new Button(composite, SWT.PUSH);
	cancelButton.setText("Cancel");
	cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
	cancelButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			shlSearch.setVisible(false);
		}
	});
	
	shlSearch.pack();
}

public void open() {
	if (shlSearch.isVisible()) {
		shlSearch.setFocus();
	} else {
		shlSearch.open();
	}
	searchText.setFocus();
	
	while (!shlSearch.isDisposed()) {
	    if (!display.readAndDispatch()) {
	        display.sleep();
	    }
	}
}

public void setWrongTimestamp(String text) {
	searchText.setText(text);
}

public void setWrongDate(String text,String myuin) {
	DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	Date timestamp = null;
	try {
		timestamp = df.parse(text.substring(myuin.length()+2, text.length() -1));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	text_1.setText(df.format(timestamp));
}


}
