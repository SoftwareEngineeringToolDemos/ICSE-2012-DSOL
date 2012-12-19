package dsolcoloring.editors;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.editors.text.TextEditor;

public class DSOLEditor extends TextEditor {

	private ColorManager colorManager;
	public static Color DEFAULT;
	public static Color KEYWORD;
	public static Color PARAM;
	public static Color COMMENT;

	public DSOLEditor() {
		super();
		colorManager = new ColorManager();
		KEYWORD = colorManager.getColor(ColorConstants.KEYWORD);
		PARAM = colorManager.getColor(ColorConstants.STRING);
		COMMENT = colorManager.getColor(ColorConstants.COMMENT);
		DEFAULT = colorManager.getColor(ColorConstants.DEFAULT);
		setSourceViewerConfiguration(new Configuration());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
