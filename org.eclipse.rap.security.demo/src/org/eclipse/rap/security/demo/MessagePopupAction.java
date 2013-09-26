package org.eclipse.rap.security.demo;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * When run, this action will show a message dialog.
 */
public class MessagePopupAction extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2801619252363512709L;
	private final IWorkbenchWindow window;

	MessagePopupAction(String text, IWorkbenchWindow window) {
		super(text);
		this.window = window;
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_MESSAGE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_MESSAGE);
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.rap.security.demo", "/icons/sample3.gif"));
	}

	public void run() {
		MessageDialog.openInformation(window.getShell(), "Open", "Open Message Dialog!");
	}
}
