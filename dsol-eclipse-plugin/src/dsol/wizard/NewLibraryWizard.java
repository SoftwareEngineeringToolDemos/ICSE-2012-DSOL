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


public class NewLibraryWizard extends Wizard implements INewWizard {

	public NewLibraryWizard() {
		setWindowTitle("DSOL Concrete Action Library Wizard");
	}

	WizardNewProjectCreationPage wizardPage;

	public void addPages() {
		wizardPage = new WizardNewProjectCreationPage("DSOL Project");
		wizardPage.setTitle("Create a DSOL Project");
		wizardPage.setDescription("Enter a project name");

		addPage(wizardPage);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {}

	@Override
	public boolean performFinish() {

		final IProject projectHandle = wizardPage.getProjectHandle();

		URI projectURI = (!wizardPage.useDefaults()) ? wizardPage
				.getLocationURI() : null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProjectDescription desc = workspace.newProjectDescription(projectHandle.getName());

		desc.setNatureIds(new String[] { IMavenConstants.NATURE_ID, JavaCore.NATURE_ID,"org.dsol.dsolNature" });

		ICommand mavenBuilder = desc.newCommand();
		mavenBuilder.setBuilderName(IMavenConstants.BUILDER_ID);
		ICommand javaBuilder = desc.newCommand();
		javaBuilder.setBuilderName(JavaCore.BUILDER_ID);

		desc.setBuildSpec(new ICommand[] { mavenBuilder, javaBuilder });
		desc.setLocationURI(projectURI);
		
		WorkspaceModifyOperation operation = new WorkspaceModifyDelegatingOperation(new SaveProject(projectHandle, desc));
		
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
		
		IProjectDescription projectDescription;
		IProject project = null;

		public SaveProject(	IProject project,
							IProjectDescription projectDescription) {
			this.project = project;
			this.projectDescription = projectDescription;
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
			
			
			addConcreteActions(container, monitor);
			addPom(container, monitor);
		}
		
		private void addFolderToProject(IContainer container, Path path,
				IProgressMonitor monitor) throws CoreException {
			final IFolder folder = container.getFolder(path);
			if (!folder.exists()) {
				folder.create(true, true, monitor);
			}
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
		
		protected void addConcreteActions(IContainer container, IProgressMonitor monitor) throws CoreException, IOException{
			
			String concreteActions = convertStreamToString(getResource("ConcreteActionsLibrary.template"));
			
			String concreteActionsClassName = getConcreteActionsClassName();
			concreteActions = concreteActions.replace("#{ClassName} ", concreteActionsClassName);
			
	        addFileToProject(container, new Path(JAVA + File.separatorChar + "actions"+ File.separatorChar +concreteActionsClassName+".java"), new ByteArrayInputStream(concreteActions.getBytes()), monitor);
		}
		
		protected void addPom(IContainer container, IProgressMonitor monitor) throws CoreException, IOException{
			String pom = convertStreamToString(getResource("pomLibrary.xml"));
			pom = pom.replace("#{artifactId}", projectDescription.getName());
	        addFileToProject(container, new Path("pom.xml"), new ByteArrayInputStream(pom.getBytes()), monitor);
		}
				
		private String getConcreteActionsClassName(){
			String projectName = projectDescription.getName();
			projectName = projectName.substring(0,1).toUpperCase()+projectName.substring(1);
			return projectName+"ConcreteActions";
		}
	}
}
