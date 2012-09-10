package org.history;



import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class Search {

	Shell shlSearch;
	Text searchText;
	Label searchAreaLabel;
	Button matchCase;
	Button findButton;
	FindListener findHandler;
	private GridLayout gl_shlSearch;

/**
 * Class constructor that sets the parent shell and the styledText widget that
 * the dialog will search.
 *
 * @param parent	Shell 
 *			The shell that is the parent of the dialog.
 */
public Search(Shell parent) {
	shlSearch = new Shell(parent, SWT.CLOSE | SWT.BORDER | SWT.TITLE);
	GridLayout layout;
	gl_shlSearch = new GridLayout();
	gl_shlSearch.numColumns = 2;
	shlSearch.setLayout(gl_shlSearch);
	shlSearch.setText("Search");
	shlSearch.addShellListener(new ShellAdapter(){
		public void shellClosed(ShellEvent e) {
			// don't dispose of the shell, just hide it for later use
			e.doit = false;
			shlSearch.setVisible(false);
		}
	});
		
	Label lblSearchWhat = new Label(shlSearch, SWT.LEFT);
	lblSearchWhat.setText("Search What:");	
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

	searchAreaLabel = new Label(shlSearch, SWT.LEFT);
	new Label(shlSearch, SWT.NONE);
	
	matchCase = new Button(shlSearch, SWT.CHECK);
	matchCase.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
		}
	});
	matchCase.setText("Match Case");
	gridData = new GridData();
	gridData.horizontalSpan = 2;
	matchCase.setLayoutData(gridData);

	Composite composite = new Composite(shlSearch, SWT.NONE);
	gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gridData.horizontalSpan = 2;
	composite.setLayoutData(gridData);
	layout = new GridLayout();
	layout.numColumns = 2;
	layout.makeColumnsEqualWidth = true;
	composite.setLayout(layout);
	
	findButton = new Button(composite, SWT.PUSH);
	findButton.setText("Search");
	findButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	findButton.setEnabled(false);
	findButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			if (!findHandler.find()){
				MessageBox box = new MessageBox(shlSearch, SWT.ICON_INFORMATION | SWT.OK | SWT.PRIMARY_MODAL);
				box.setText(shlSearch.getText());
				box.setMessage("Cannot find " +"\"" + searchText.getText() + "\"");
				box.open();	
			}		
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

public boolean getMatchCase() {
	return matchCase.getSelection();
}
public String getSearchString() {
	return searchText.getText();
}

public void open() {
	if (shlSearch.isVisible()) {
		shlSearch.setFocus();
	} else {
		shlSearch.open();
	}
	searchText.setFocus();
}

public void setSearchAreaLabel(String label) {
	searchAreaLabel.setText(label);
}
public void setMatchCase(boolean match) {
	matchCase.setSelection(match);
}
public void setSearchString(String searchString) {
	searchText.setText(searchString);
}
public void addFindListener(FindListener listener) {
	this.findHandler = listener;	
}
public void removeFindListener(FindListener listener) {
	this.findHandler = null;
}
}
