package dsol;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.operation.IRunnableContext;

public class DSOLLaunchShortcut extends JavaApplicationLaunchShortcut {


	@Override
	protected IType chooseType(IType[] types, String title) {
		
		for(IType type:types){
			if("org.dsol.engine.DSOLServer".equals(type.getFullyQualifiedName())){
				return type;
			}
		}
		return super.chooseType(types, title);
	}
	
		
	@Override
	protected IType[] findTypes(Object[] elements, IRunnableContext context)
			throws InterruptedException, CoreException {

		try {
			for(Object element:elements){
				if(element instanceof JavaProject){
					JavaProject project = (JavaProject)element;
					IType dsolServer = project.getJavaProject().findType("org.dsol.engine.DSOLServer");
					if(dsolServer != null){
						return new IType[]{dsolServer};	
					}					
				}				
			}
		} catch (Exception e) {}
		
		
		return super.findTypes(elements, context);
	}
	

}
