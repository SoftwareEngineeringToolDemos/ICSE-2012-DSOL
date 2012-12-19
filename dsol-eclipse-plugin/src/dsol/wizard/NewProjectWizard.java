package dsol.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.ui.internal.UpdateConfigurationJob;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;


public class NewProjectWizard extends Wizard implements INewWizard {

	public NewProjectWizard() {
		setWindowTitle("DSOL Project Wizard");
	}

	NewProjectWizardPage1 properties;
	WizardNewProjectCreationPage wizardPage;

	public void addPages() {
		wizardPage = new WizardNewProjectCreationPage("DSOL Project");
		wizardPage.setTitle("Create a DSOL Project");
		wizardPage.setDescription("Enter a project name");

		properties = new NewProjectWizardPage1("Properties Page");
		addPage(wizardPage);
		addPage(properties);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page.equals(wizardPage)) {
			String projectName = wizardPage.getProjectName();
			properties.setProjectName(projectName);
		}
		return super.getNextPage(page);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {

		final IProject projectHandle = wizardPage.getProjectHandle();

		URI projectURI = (!wizardPage.useDefaults()) ? wizardPage.getLocationURI() : null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProjectDescription desc = workspace
				.newProjectDescription(projectHandle.getName());

		desc.setNatureIds(new String[] { IMavenConstants.NATURE_ID, JavaCore.NATURE_ID,"org.dsol.dsolNature" });

		ICommand mavenBuilder = desc.newCommand();
		mavenBuilder.setBuilderName(IMavenConstants.BUILDER_ID);
		ICommand javaBuilder = desc.newCommand();
		javaBuilder.setBuilderName(JavaCore.BUILDER_ID);

		desc.setBuildSpec(new ICommand[] { mavenBuilder, javaBuilder });
		desc.setLocationURI(projectURI);

		Map properties = new HashMap<String, String>();
		properties.put("Orchestration_Interface", this.properties.orchestrationInterface.getText());
		properties.put("Service_Context", this.properties.contextText.getText());
		properties.put("Service_Port", this.properties.portText.getText());
		properties.put("stand-alone", String.valueOf(this.properties.standaloneApp.getSelection()));
		
		WorkspaceModifyOperation operation = new WorkspaceModifyDelegatingOperation(new SaveProject(projectHandle, desc, properties));

		
		try {
			getContainer().run(true, false, operation);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}
		return true;
	}

	class SaveProject implements IRunnableWithProgress {

		private String SRC = "src";
		private String MAIN = SRC + File.separator + "main";
		private String RESOURCES = MAIN + File.separator + "resources";
		private String JAVA = MAIN + File.separator + "java";
		private String WEB = MAIN + File.separator + "webapp";
		private String WEB_INF = WEB + File.separator + "WEB-INF";
		
		IProjectDescription projectDescription;
		IProject project = null;
		Map<String,String> properties = null;

		public SaveProject(	IProject project,
							IProjectDescription projectDescription,
							Map<String,String> properties) {
			this.project = project;
			this.projectDescription = projectDescription;
			this.properties = properties;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				project.create(projectDescription, monitor);
				project.open(monitor);

				addProjectContent(monitor);

				UpdateConfigurationJob updateProjConfigurationJob = new UpdateConfigurationJob(new IProject[] { project }, true, true);
				
				updateProjConfigurationJob.setPriority(Job.BUILD);
				updateProjConfigurationJob.schedule();
				project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
			catch (IOException e) {
				throw new InvocationTargetException(e);
			}
		}

		private void addProjectContent(IProgressMonitor monitor)
				throws CoreException, IOException {
			IContainer container = (IContainer) project;

			addFolderToProject(container, new Path(SRC), monitor);
			addFolderToProject(container, new Path(MAIN), monitor);
			addFolderToProject(container, new Path(JAVA), monitor);
			addFolderToProject(container, new Path(JAVA + File.separatorChar + "actions"), monitor);
			
			addFolderToProject(container, new Path(RESOURCES), monitor);

			addFileToProject(container, getResourcePath("abstract_actions.dsol"), "abstract_actions.dsol", monitor);
			addFileToProject(container, getResourcePath("echo.goal.dsol"), "echo.goal.dsol", monitor);
			addFileToProject(container, getResourcePath("dsol-composed-services.xml"), "dsol-composed-services.xml", monitor);
			
			addDsolProperties(container, monitor);
			addOrchestrationInterface(container, monitor);
			addConcreteActions(container, monitor);
			
			boolean standAloneApp = new Boolean(properties.get("stand-alone"));
			addPom(container, monitor, standAloneApp);
			if(!standAloneApp){
				addWebDescriptor(container, monitor);
			}
		}

		private Path getResourcePath(String fileName){
			return new Path(RESOURCES+File.separator+fileName);
		}
		
		private void addFolderToProject(IContainer container, Path path,
				IProgressMonitor monitor) throws CoreException {
			final IFolder folder = container.getFolder(path);
			if (!folder.exists()) {
				folder.create(true, true, monitor);
			}
		}

		private void addFileToProject(IContainer container, Path path,
				String fileName, IProgressMonitor monitor) throws CoreException {
			InputStream contentStream = getResource(fileName);
			addFileToProject(container, path, contentStream, monitor);
		}

		private void addFileToProject(IContainer container, Path path,
				InputStream contentStream, IProgressMonitor monitor)
				throws CoreException {
			final IFile file = container.getFile(path);

			if (file.exists()) {
				file.setContents(contentStream, true, true, monitor);
			} else {
				file.create(contentStream, true, monitor);
			}
		}

		protected InputStream getResource(String name) {
			if (!name.startsWith("/")) {
				name = "/" + name;
			}
			return getClass().getResourceAsStream(name);
		}

		protected String convertStreamToString(InputStream is)
				throws IOException {

			byte[] bytes = new byte[1024];
			int bytesRead = 0;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			while ((bytesRead = is.read(bytes)) > 0) {
				bos.write(bytes, 0, bytesRead);
			}
			return new String(bos.toByteArray());
		}

		protected void addDsolProperties(IContainer container, IProgressMonitor monitor) throws CoreException, IOException{
			
			String dsolProperties = convertStreamToString(getResource("dsol.properties"));
			
			dsolProperties = dsolProperties.replace("#{Orchestration_Interface}", properties.get("Orchestration_Interface"));
			dsolProperties = dsolProperties.replace("#{ConcreteActions}", "actions."+getConcreteActionsClassName());
			dsolProperties = dsolProperties.replace("#{Service_Context}", properties.get("Service_Context"));
			dsolProperties = dsolProperties.replace("#{Service_Port}", properties.get("Service_Port"));
			
	        addFileToProject(container, getResourcePath("dsol.properties"), new ByteArrayInputStream(dsolProperties.getBytes()), monitor);
		}
		
		protected void addConcreteActions(IContainer container, IProgressMonitor monitor) throws CoreException, IOException{
			
			String concreteActions = convertStreamToString(getResource("ConcreteActions.template"));
			
			String concreteActionsClassName = getConcreteActionsClassName();
			concreteActions = concreteActions.replace("#{ClassName} ", concreteActionsClassName);
			
	        addFileToProject(container, new Path(JAVA + File.separatorChar + "actions"+ File.separatorChar +concreteActionsClassName+".java"), new ByteArrayInputStream(concreteActions.getBytes()), monitor);
		}
		
		protected void addOrchestrationInterface(IContainer container, IProgressMonitor monitor) throws CoreException, IOException{
			
			String orchestrationInterfaceTemplate = convertStreamToString(getResource("OrchestrationInterface.template"));
			String orchestrationInterface = properties.get("Orchestration_Interface");
			String[] parts = orchestrationInterface.split("[\\.]");
			
			String packageName = "";
			String currentFolder = JAVA;
			if (parts.length > 1) {
				packageName = "package " + parts[0];
				currentFolder = currentFolder + File.separator + parts[0];
				addFolderToProject(container, new Path(currentFolder), monitor);
				for (int i = 1; i < parts.length - 1; i++) {
					addFolderToProject(container, new Path(currentFolder
							+ File.separator + parts[i]), monitor);
					currentFolder = currentFolder + File.separator + parts[i];
					packageName += "." + parts[i];
				}
				packageName += ";";
			}
			String orchestrationInterfaceFodler = currentFolder;
			
			orchestrationInterfaceTemplate = orchestrationInterfaceTemplate.replace("#{packageName}", packageName);
			String orchestrationInterfaceClassName = parts[parts.length - 1];
			orchestrationInterfaceTemplate = orchestrationInterfaceTemplate.replace("#{OrchestrationInterfaceName}", orchestrationInterfaceClassName);
			
	        addFileToProject(container, new Path(orchestrationInterfaceFodler+File.separator+orchestrationInterfaceClassName+".java"), new ByteArrayInputStream(orchestrationInterfaceTemplate.getBytes()), monitor);
		}	
		
		protected void addPom(IContainer container, IProgressMonitor monitor, boolean standAloneApp) throws CoreException, IOException{
			String pomFile = standAloneApp?"pom.xml":"pomWeb.xml";
			String pom = convertStreamToString(getResource(pomFile));
			pom = pom.replace("#{project-name}", projectDescription.getName());
			pom = pom.replace("#{artifactId}", projectDescription.getName());
			pom = pom.replace("#{context}", properties.get("Service_Context"));
			pom = pom.replace("#{port}", properties.get("Service_Port"));//web			
	        addFileToProject(container, new Path("pom.xml"), new ByteArrayInputStream(pom.getBytes()), monitor);
		}
		
		protected void addWebDescriptor(IContainer container, IProgressMonitor monitor) throws CoreException, IOException{
			String webDescriptor = convertStreamToString(getResource("web.xml"));
			webDescriptor = webDescriptor.replace("#{display-name}", projectDescription.getName());
			addFolderToProject(container, new Path(WEB), monitor);
			addFolderToProject(container, new Path(WEB_INF), monitor);
	        addFileToProject(container, new Path(WEB_INF + File.separator+"web.xml"), new ByteArrayInputStream(webDescriptor.getBytes()), monitor);
		}

		
		private String getConcreteActionsClassName(){
			return getOrchestrationInterfaceClassName()+"ConcreteActions";
		}
		
		private String getOrchestrationInterfaceClassName(){
			String orchestrationInterface = properties.get("Orchestration_Interface");
			String[] parts = orchestrationInterface.split("[\\.]");
			return parts[parts.length - 1];
		}
	}
}
