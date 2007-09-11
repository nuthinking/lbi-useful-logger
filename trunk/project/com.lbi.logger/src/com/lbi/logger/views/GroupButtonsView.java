package com.lbi.logger.views;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.lbi.logger.listeners.ITextContentListener;
import com.lbi.logger.models.TextContent;

public class GroupButtonsView extends Composite
{

	private ITextContentListener text_listener;
	private TextContent text_content;
	private LTabItem current_tab;
	
	private HashMap<String, Button> buttons_map = new HashMap<String, Button>();

	public GroupButtonsView(Composite parent, int style)
	{
		super(parent, style);
		// TODO Auto-generated constructor stub

		setLayout (new FillLayout());
		
		pack();
		/*Listener listener = new Listener () {
			public void handleEvent (Event e) {
				Control [] children = shell.getChildren ();
				for (int i=0; i<children.length; i++) {
					Control child = children [i];
					if (e.widget != child && child instanceof Button && (child.getStyle () & SWT.TOGGLE) != 0) {
						((Button) child).setSelection (false);
					}
				}
				((Button) e.widget).setSelection (true);
			}
		};
		for (int i=0; i<5; i++) {
			Button button = new Button (this, SWT.TOGGLE);
			button.setText ("B" + i);
//			button.addListener (SWT.Selection, listener);
			if (i == 0) button.setSelection (true);
		}
		addGroup("initial");*/
	}
	
	public void setCurrentTab(LTabItem tab)
	{
		System.out.println("setCurrentTab " + tab);
		if(tab == current_tab) return;
		current_tab = tab;
		setTextContent(current_tab.getTextContent());
	}
	
	public void setTextContent (TextContent text_content)
	{
		System.out.println("setTextContent: " + text_content + " is the same? " + (text_content == this.text_content));
		if(text_content == this.text_content) return;
		if(text_listener != null) this.text_content.removeListener(text_listener);
		clearGroups();
		
		this.text_content = text_content;
		
		text_listener = new ITextContentListener(){

			@Override
			public void onClear()
			{
			}

			@Override
			public void onElementAdded()
			{
			}

			@Override
			public void onGroupAdded()
			{
				onTextGroupAdded();
			}};
		System.out.println("try to addListener");
		this.text_content.addListener(text_listener);
		System.out.println("try to getgroups");
		String[] groups = this.text_content.getGroups();
		if(groups == null) System.out.println("GROUPS is NULL!");
		System.out.println("setTextContent::groups len: " + groups.length);
		for (int i = 0; i < groups.length; i++) {
			addGroup(groups[i]);
		}
		
	}

	private void clearGroups()
	{
		System.out.println("clearGroups");
		Iterator<String> it = buttons_map.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			buttons_map.get(key).dispose();
		}
		buttons_map.clear();
	}

	private void addGroup(String name)
	{
		System.out.println("addGroup:" + name);
		Button button = new Button (this, SWT.TOGGLE);
		button.setText (name);
		buttons_map.put(name, button);
		pack();
	}


	protected void onTextGroupAdded()
	{
		addGroup(text_content.getAddedGroup());
	}

}
