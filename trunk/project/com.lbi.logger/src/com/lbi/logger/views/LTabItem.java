package com.lbi.logger.views;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.lbi.logger.Activator;
import com.lbi.logger.helpers.LogMonitor;
import com.lbi.logger.listeners.ILogListener;
import com.lbi.logger.models.TextContent;
import com.lbi.logger.preferences.PreferenceConstants;

public class LTabItem extends CTabItem
{
	private int CHECK_DELAY = 500;
	
	private String __text;
	private String log_path;
	
	private StyledText styledText;
	private Composite body;
	private LogMonitor log_monitor;
	private TextContent text_content;
//	private GroupButtonsView group_buttons_view;

	private boolean hide_markups;
	
	public LTabItem(CTabFolder parent, String log_path)
	{
		super(parent, SWT.NONE, parent.getItemCount());
		
		this.log_path = log_path;

		updateLabel();
		body = new Composite(parent, SWT.NONE);
		setLayoutContainer();
		//checkBuffer();
		/*addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				stopLogging();
			}});*/
		log_monitor = new LogMonitor(log_path, styledText.getDisplay(), CHECK_DELAY);
		log_monitor.addListener(new ILogListener(){

			public void onLogUpdate()
			{
				addContent(log_monitor.getNewContent());
				/*System.out.println("--------------------");
				System.out.println("Log Monitor updated!");
				System.out.println("new content line index: " + log_monitor.new_content_starting_line);
				System.out.println("new content: \"" + log_monitor.getNewContent());
				System.out.println("--------------------");*/
				// styledText.append
			}
			
		});
		text_content = new TextContent();
		
		hide_markups = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.HIDE_MARKUPS);
		
		Activator.getDefault().getPluginPreferences().addPropertyChangeListener(new IPropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent event)
			{
				onPreferencesChanged();
			}});
		
	}
	
	protected void onPreferencesChanged()
	{
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		// TODO Auto-generated method stub
//		System.out.println("Preferences changed!" + Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.HIDE_MARKUPS));
		if(prefs.getBoolean(PreferenceConstants.HIDE_MARKUPS)
				!= hide_markups)
		{
			reload();
			hide_markups = prefs.getBoolean(PreferenceConstants.HIDE_MARKUPS);
		}
	}
	
	private void reload ()
	{
		styledText.setText("");
		styledText.setStyleRanges(new StyleRange[0]);
		log_monitor.reload();
	}

	protected void addContent(String newContent)
	{
		styledText.append(text_content.parseContent(newContent, styledText.getCharCount()));
//		styledText.setStyleRanges(text_content.getStyleRanges());
		styledText.setStyleRanges(concatStyles(styledText.getStyleRanges(), text_content.getStyleRanges()));
		
		styledText.setTopIndex(styledText.getLineCount());
	}
	
	private StyleRange[] concatStyles(StyleRange[] A, StyleRange[] B) {
		StyleRange[] C= new StyleRange[A.length+B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);
	 
	   return C;
	} 

	public String getLogPath () {
		return log_path;
	}
	
	private void updateLabel() {
		String name = (log_path.lastIndexOf("/")>-1 ? log_path.substring(log_path.lastIndexOf("/")+1) : log_path);
		setText(name);
	}

	public void setText ( String s )
	{
		super.setText(s);
		__text = s;
	}
	
	public void setLayoutContainer ()
	{
		
		body.setLayout(new FillLayout());
//		body.setLayout(new FormLayout());
		
		
		styledText = new StyledText(body, SWT.MULTI | SWT.WRAP | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
		styledText.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				stopLogging();
			}});
		/*styledText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){
				System.out.println("FocusGained:" + e.toString());
			}
		
			public void focusLost(FocusEvent e) {
				System.out.println("FocusLost:" + e.toString());
			}});*/
		/*styledText.addMouseMoveListener(new MouseMoveListener(){

			public void mouseMove(MouseEvent e)
			{
				
			}
			
		});*/
		styledText.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e)
			{
				Preferences prefs = Activator.getDefault().getPluginPreferences();
				boolean use_double_click = prefs.getBoolean(PreferenceConstants.USE_DOUBLE_CLICK_TO_CLEAR);
//				System.out.println("use double-click: " + use_double_click);
				if(use_double_click) cleanBuffer();
			}

			public void mouseDown(MouseEvent e)
			{
				
			}

			public void mouseUp(MouseEvent e)
			{
				
			}
		
		});
		
		/*group_buttons_view = new GroupButtonsView(body, 0);
		
		Point size = group_buttons_view.computeSize (SWT.DEFAULT, SWT.DEFAULT);
		final FormData buttonsData = new FormData (size.x, 25);
		buttonsData.left = new FormAttachment (0, 0);
		buttonsData.right = new FormAttachment (100, 0);
		buttonsData.bottom = new FormAttachment(100,0);
		group_buttons_view.setLayoutData (buttonsData);
		
		FormData listData = new FormData ();
		listData.left = new FormAttachment (0, 0);
		listData.right = new FormAttachment (100, 0);
		listData.top = new FormAttachment (0, 0);
		listData.bottom = new FormAttachment (group_buttons_view, 0);
		styledText.setLayoutData (listData);*/
		
		
        setControl(body);
	}
	
	/*public void checkBuffer ()
	{
		System.out.println("LTabItem::checkBuffer: " + __text);
		String new_buffer = getBuffer();
		//System.out.println("buffer:" + new_buffer);
		if(new_buffer.equals(buffer)) return;
		buffer = new_buffer;
		draw();
	}
	private String getBuffer() {
	    return BufferUtils.getBufferContent(log_path);
	}
	 */

	protected void cleanBuffer()
	{
		//System.out.println("### cleanBuffer!");
		log_monitor.clean();
		styledText.setText("");
		styledText.setStyleRanges(new StyleRange[0]);
	}

	public void startLogging ()
	{
		System.out.println("LTabItem::startLogging: " + __text);
		log_monitor.start();
	}
	
	public void stopLogging ()
	{
		System.out.println("### stopLogging: " + __text);
		log_monitor.stop();
	}

	public TextContent getTextContent()
	{
		return text_content;
	}
	
	/*private void draw ()
	{
		System.out.println("draw: " + __text);
		SimpleLbiTextContent tc = new SimpleLbiTextContent(body.getDisplay());
		tc.setContent(buffer);
		styledText.setText(tc.toPlainText());
		styledText.setStyleRanges(tc.getStyleRanges());
		styledText.setTopIndex(styledText.getLineCount());
	}*/
}
