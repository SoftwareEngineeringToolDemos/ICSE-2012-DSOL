package dsol.wizard;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewProjectWizardPage1 extends WizardPage {

	protected Text orchestrationInterface;
	protected Text abstractActionFileText;
	protected Text servicesFileText;
	protected Text contextText;
	protected Text portText;
	protected Button standaloneApp;
	protected Label portLabel;

	protected NewProjectWizardPage1(String pageName) {
		super(pageName);
		setTitle("Service Configuration");
		setDescription("Please enter your service configuration");
	}

	public void setProjectName(String projectName) {
		String firstLetter = projectName.substring(0, 1);
		this.orchestrationInterface.setText("service." +firstLetter.toUpperCase() + projectName.substring(1));
		this.contextText.setText(firstLetter.toLowerCase() + projectName.substring(1));
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		setControl(composite);

		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				validate();
			}
		};

		GridData buttonData = new GridData();
        buttonData.horizontalSpan=2;
		
		standaloneApp = new Button(composite, SWT.CHECK);
		standaloneApp.setLayoutData(buttonData);
		standaloneApp.setText("Stand-alone application");
		
//		standaloneApp.addMouseListener(new MouseListener() {
//
//			public void mouseDown(MouseEvent e) {}		
//			public void mouseDoubleClick(MouseEvent e) {}
//
//			@Override
//			public void mouseUp(MouseEvent e) {
//				if(e.getSource() == standaloneApp){
//					portLabel.setVisible(standaloneApp.getSelection());
//					portText.setVisible(standaloneApp.getSelection());
//				}
//				validate();
//			}
//		});
		
		new Label(composite, SWT.NONE).setText("Orchestration Interface: ");
		orchestrationInterface = new Text(composite, SWT.BORDER);
		orchestrationInterface.addKeyListener(keyListener);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		orchestrationInterface.setLayoutData(gridData);

		new Label(composite, SWT.NONE).setText("Abstract Actions: ");
		abstractActionFileText = new Text(composite, SWT.BORDER);
		abstractActionFileText.addKeyListener(keyListener);
		
		abstractActionFileText.setLayoutData(gridData);
		abstractActionFileText.setText("classpath:abstract_actions.dsol");
		
		new Label(composite, SWT.NONE).setText("Composed Services: ");
		servicesFileText = new Text(composite, SWT.BORDER);
		servicesFileText.addKeyListener(keyListener);
		
		servicesFileText.setLayoutData(gridData);
		servicesFileText.setText("classpath:dsol-composed-services.xml");
		
		new Label(composite, SWT.NONE).setText("Context: ");
		contextText = new Text(composite, SWT.BORDER);
		contextText.addKeyListener(keyListener);
		
		contextText.setLayoutData(gridData);

		portLabel = new Label(composite, SWT.NONE);
		portLabel.setText("Port: ");
		//portLabel.setVisible(false);
		
		portText = new Text(composite, SWT.BORDER);
		portText.addKeyListener(keyListener);
		//portText.setVisible(false);
		portText.setText("8088");
	}

	private void validate(){
		setPageComplete(false);
		String orchestrationInterfaceClassName = orchestrationInterface.getText();
		setErrorMessage(null);
		if(orchestrationInterfaceClassName.trim().isEmpty()){
			setErrorMessage("Please, inform the Orchestration Interface class name.");
			return;
		}
		else if(!isFullyQualifiedClassname(orchestrationInterfaceClassName)){
			setErrorMessage("Please, inform a valid class name for the Orchestration Interface.");
			return;
		}
		
		if(abstractActionFileText.getText().trim().isEmpty()){
			setErrorMessage("Please, inform the abstract actions cource code file.");
			return;
		}
		
		if(servicesFileText.getText().trim().isEmpty()){
			setErrorMessage("Please, inform the file in which the composed services are specified.");
			return;
		}
		
		if(contextText.getText().trim().isEmpty()){
			setErrorMessage("Please, inform the context in which the service must me deployed.");
			return;
		}
		
		//the port is only madnatory if the project is a stand-alone application
		if(portText.getText().trim().isEmpty()){
			setErrorMessage("Please, inform the port in which the service must me deployed.");
			return;
		}
		
		setPageComplete(isPageComplete());
	}
	
	
	@Override
	public boolean isPageComplete() {
		String orchestrationInterfaceClassName = orchestrationInterface.getText();
		return isFullyQualifiedClassname(orchestrationInterfaceClassName)
				&& !abstractActionFileText.getText().trim().isEmpty()
				&& !contextText.getText().trim().isEmpty()
				&& !portText.getText().trim().isEmpty();
	}
	
	public boolean isFullyQualifiedClassname( String classname ) {
	      if (classname == null || classname.trim().isEmpty() || classname.endsWith(".")) return false;
	      
	      String[] parts = classname.split("[\\.]");
	      if (parts.length == 0) return false;
	      for (String part : parts) {
	    	  if(part.trim().isEmpty()) return false;
	          CharacterIterator iter = new StringCharacterIterator(part);
	          // Check first character (there should at least be one character for each part) ...
	          char c = iter.first();
	          if (c == CharacterIterator.DONE) return false;
	          if (!Character.isJavaIdentifierStart(c) && !Character.isIdentifierIgnorable(c)) return false;
	          c = iter.next();
	          // Check the remaining characters, if there are any ...
	          while (c != CharacterIterator.DONE) {
	              if (!Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c)) return false;
	              c = iter.next();
	          }
	      }
	      return true;
	  }

}
