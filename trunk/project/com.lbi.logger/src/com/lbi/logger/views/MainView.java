package com.lbi.logger.views;


import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.lbi.logger.actions.ShowAboutAction;

public class MainView extends ViewPart {
	protected TableViewer viewer;
	private Action about_plugin_action;
	private Action add_log_action;
	private Action doubleClickAction;
	
	protected Composite parent;

	/**
	 * The constructor.
	 */
	public MainView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	CTabFolder tab_folder;
	private IMemento memento;
	protected LTabItem selected_tab;
	
	public void createPartControl(Composite $parent)
	{
		parent = $parent;
		
		//parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//parent.setLayout(new RowLayout());
		
		createTabFolder();
		
		makeActions();
		//hookContextMenu();
		//hookDoubleClickAction();
		contributeToActionBars();
		restoreState();
	}

	protected void createTabFolder() {
		// TODO Auto-generated method stub
		int modes = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CLOSE | SWT.TOP;
		tab_folder = new CTabFolder(parent, modes);
		tab_folder.addSelectionListener(new SelectionListener (){
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(selected_tab != null) selected_tab.stopLogging();
				LTabItem tab = (LTabItem) e.item;
				System.out.println("widgetSelected item:" + tab.getText());
				tab.startLogging();
				selected_tab = tab;
			}
		});
		//tab_folder.setSelectionForeground(new Color(parent.getDisplay(), 255, 255, 255));
		//tab_folder.addCTabFolder2Listener(new CTabFolder2Listener(null));
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MainView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tab_folder);
		tab_folder.setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		//fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(add_log_action);
		manager.add(new Separator());
		manager.add(about_plugin_action);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(about_plugin_action);
		manager.add(add_log_action);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(about_plugin_action);
		manager.add(add_log_action);
	}

	private void makeActions() {
		about_plugin_action = new ShowAboutAction(parent);
		
		
		add_log_action = new Action() {
			public void run() {
				promptNewTab();
			}
		};
		add_log_action.setText("Add new log");
		add_log_action.setToolTipText("Add new log viewer as new tab");
		add_log_action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		/*doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};*/
	}
	
	protected void promptNewTab ()
	{
		Shell shell = parent.getDisplay().getShells()[0];
		FileDialog fileDialog = new FileDialog(shell, SWT.NONE);

        //fileDialog.setFilterPath(fileFilterPath);
        
        fileDialog.setFilterExtensions(new String[]{"*.*", "*.log", "*.txt", });
        fileDialog.setFilterNames(new String[]{"Any", "Log Files", "Normal Text", });
        
        String firstFile = fileDialog.open();

        if(firstFile != null) {
        	String file_name = firstFile.replace("\\", "/");
        	System.out.println("selected file:" + file_name);
        	addNewLog(file_name);
        }
	}
	
	protected LTabItem addLog ( String path )
	{
		return new LTabItem(tab_folder, path);
	}
	
	protected void addNewLog (String path)
	{
		LTabItem item = addLog(path);
		tab_folder.setSelection(item);
		item.startLogging();
	}

	/*private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}*/
	private void showMessage(String message) {
		MessageDialog.openInformation(
			parent.getShell(),
			"Main View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		System.out.println("MainView::setFocus");
		//viewer.getControl().setFocus();
	}
	
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		init(site);
		this.memento = memento;
	}
	
	public void saveState(IMemento memento){
		int tabs_number = tab_folder.getItemCount();
		if(tabs_number == 0) return;
		// save files
		IMemento files_memento = memento.createChild("files");
		for (int i = 0; i < tabs_number; i++) {
			LTabItem item = (LTabItem) tab_folder.getItem(i);
			files_memento.createChild("file",item.getLogPath());
		}
		// save selected index
		memento.createChild("selection", String.valueOf(tab_folder.getSelectionIndex()));
	}
	
	private void restoreState(){
		// restore files
		if(memento == null){
			//System.err.println("memento is null!");
			return;
		}
		
		IMemento files_memento = memento.getChild("files");
		if(files_memento != null){
			IMemento descriptors [] = files_memento.getChildren("file");
			if(descriptors != null && descriptors.length > 0){
				for (int i = 0; i < descriptors.length; i++) {
					String path = descriptors[i].getID();
					addLog(path);
				}
				// check selection
				IMemento selection_memento = memento.getChild("selection");
				int index = Integer.parseInt(selection_memento.getID());
				LTabItem initial_item = (LTabItem) tab_folder.getItem(index);
				tab_folder.setSelection(initial_item);
				initial_item.startLogging();
				selected_tab = initial_item;
			}
		}
	}
}