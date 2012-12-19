package dsol.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AddConcreteAction extends Wizard {
	
	private InnerWizard wizard;
	
	private String abstractAction;
	private String service;
	private String returnValue;
	private String returnType;
	private String methodName;
	private String parameters;
	
	public AddConcreteAction() {
		setWindowTitle("Concrete Action");
	}
	
	@Override
	public void addPages() {
		wizard = new InnerWizard("Concrete Action");
		addPage(wizard);
	}
	
	public String getAbstractActionName(){
		return abstractAction;
	}
	
	public String getService() {
		return service;
	}
	
	public String getReturnValue() {
		return returnValue;
	}
	
	public String getReturnType(){
		return returnType;
	}
	
	public String getMethodName(){
		return methodName;
	}
	
	public String getParameters() {
		return parameters;
	}
	
	@Override
	public boolean performFinish() {
		abstractAction = wizard.abstractAction.getText().trim();
		service = wizard.service.getText().trim();
		returnValue = wizard.returnValue.getText().trim();
		
		String returnType = wizard.returnType.getText().trim();
		if(returnType.isEmpty()){
			returnType = "void";
		}
		this.returnType = returnType;
		
		String methodName = wizard.methodName.getText().trim();
		if(methodName.isEmpty()){
			methodName = getAbstractActionName();
		}
		
		this.methodName = methodName;
		this.parameters = wizard.parameters.getText().trim();
		return true;
	}
	
	class InnerWizard extends WizardPage{
		
		Composite composite;
		KeyListener keyListener;
		protected Text abstractAction;
		protected Text service;
		protected Text methodName;
		protected Text parameters;
		protected Text returnType;
		protected Text returnValue;
	    
		public InnerWizard(String pageName) {
	    	super(pageName);
	    	setPageComplete(false);
		    setTitle("Add a new Concrete Action");
		    setDescription("Please, enter the information for the new concrete action");
		    
			keyListener = new KeyListener() {
				@Override
				public void keyReleased(KeyEvent e) {
					validate();
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					validate();
				}
			};
	    }

		private void validate(){
			setPageComplete(false);
			setErrorMessage(null);
			setMessage(null);
			if(isEmpty(abstractAction)){
				setErrorMessage("Please, inform the abstract action name to be implemented.");
				return;
			}
			
			if(isEmpty(returnType) && !isEmpty(returnValue)){
				setErrorMessage("Please, inform the return type.");
				return;
			}
			if(!isEmpty(returnType) && isEmpty(returnValue)){
				setErrorMessage("Please, inform the return object mapping.");
			}
			setPageComplete(true);
		}
		
		private boolean isEmpty(Text text){
			return text.getText().trim().isEmpty();
		}
		
		@Override
		public void createControl(Composite parent) {
			composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			composite.setLayout(layout);
			setControl(composite);
			
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			
			addLabel("Abstract Action: ");
			abstractAction = getText();
			abstractAction.setLayoutData(gridData);
			
			addLabel("Service: ");
			service = getText();
			service.setLayoutData(gridData);
			
			addLabel("Method Name: ");
			methodName = getText();
			methodName.setLayoutData(gridData);
			
			addLabel("Parameters: ");
			parameters = getText();
			parameters.setLayoutData(gridData);
			
			addLabel("Return Type: ");
			returnType = getText();
			returnType.setLayoutData(gridData);

			addLabel("Return Object Mapping: ");
			returnValue = getText();
			returnValue.setLayoutData(gridData);
			
			
		}
		
		private Text getText(){
			Text text =  new Text(composite, SWT.BORDER);
			text.addKeyListener(keyListener);
			return text;
		}
		
		private void addLabel(String text){
			new Label(composite, SWT.NONE).setText(text);
		}

	}
}
