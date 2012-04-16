package org.history;


import java.io.IOException;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class ImportHistoryUI {

	Shell shlImport;
	Text importText;
	Label dirAreaLabel;
	Button importButton;
	ImportHistory ImportHistory;
	ImportListener importHandler;
	private GridLayout gl_shlImport;
	private Button btnNewButton;
	private Label lblNewLabel;
	private Text text;

/**
 * 
 *  @param parent	Shell 
 *			The shell that is the parent of the dialog.
 */
	
public ImportHistoryUI(Shell parent) {
	shlImport = new Shell(parent, SWT.DIALOG_TRIM);
	shlImport.setSize(345, 260);
	GridLayout layout;
	gl_shlImport = new GridLayout();
	gl_shlImport.numColumns = 3;
	shlImport.setLayout(gl_shlImport);
	shlImport.setText("Search");
	shlImport.addShellListener(new ShellAdapter(){
		public void shellClosed(ShellEvent e) {
			// don't dispose of the shell, just hide it for later use
			e.doit = false;
			shlImport.setVisible(false);
		}
	});
		
	Label lblImportDir = new Label(shlImport, SWT.LEFT);
	lblImportDir.setText("History Dir:");	
	importText = new Text(shlImport, SWT.BORDER);
	GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	gridData.widthHint = 200;
	importText.setLayoutData(gridData);
	importText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			boolean enableFind = (importText.getCharCount() != 0);
			importButton.setEnabled(enableFind);
		}
	});
	
	btnNewButton = new Button(shlImport, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			DirectoryDialog dlg = new DirectoryDialog(shlImport);
			dlg.setText("Import History");
			dlg.setFilterPath("C:/");
			String selected = dlg.open();
			if (selected == null) {
				return;
			}
			
			importText.setText(selected);
			
		}
	});
	btnNewButton.setText("Open");

	dirAreaLabel = new Label(shlImport, SWT.LEFT);
	new Label(shlImport, SWT.NONE);
	new Label(shlImport, SWT.NONE);
	
	lblNewLabel = new Label(shlImport, SWT.NONE);
	lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblNewLabel.setText("Your UIN:");
	
	text = new Text(shlImport, SWT.BORDER);
	GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
	gd_text.widthHint = 123;
	text.setLayoutData(gd_text);
	new Label(shlImport, SWT.NONE);

	Composite composite = new Composite(shlImport, SWT.NONE);
	gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gridData.horizontalSpan = 2;
	composite.setLayoutData(gridData);
	layout = new GridLayout();
	layout.numColumns = 2;
	layout.makeColumnsEqualWidth = true;
	composite.setLayout(layout);
	
	importButton = new Button(composite, SWT.PUSH);
	importButton.setText("Import");
	importButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	importButton.setEnabled(false);
	importButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			String dir = getDirString();
			String uin = getUInString();
			
			ImportHistory hist = new ImportHistory();
			
			try {
				hist.importHistory(uin, dir);
				
				History.showMessage("Done.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				History.showMessage("Import Failed");
				e1.printStackTrace();
			}
		}
	});
			
	Button cancelButton = new Button(composite, SWT.PUSH);
	cancelButton.setText("Cancel");
	cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
	cancelButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			shlImport.setVisible(false);
		}
	});
	
	shlImport.pack();
	new Label(shlImport, SWT.NONE);
}


public String getDirString() {
	return importText.getText();
}

public String getUInString() {
	return text.getText();
}

public void open() {
	if (shlImport.isVisible()) {
		shlImport.setFocus();
	} else {
		shlImport.open();
	}
	importText.setFocus();
}

public void addImportListener(ImportListener listener) {
	this.importHandler = listener;	
}

}

