package com.lbi.logger.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ShowAboutAction extends Action
{
	private Composite view;
	
	public ShowAboutAction(Composite $view)
	{
		view = $view;
		setText("About LBi Logger");
		setToolTipText("Find out who did this plug-in");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	public void run() {
		String message = "This plugin has been developed by LBi Uk";
		MessageDialog.openInformation(
				view.getShell(),
				"About LBi Logger",
				message);
	}
}
