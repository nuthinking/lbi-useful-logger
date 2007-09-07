package com.lbi.logger.views;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.lbi.logger.helpers.BufferUtils;
import com.lbi.logger.helpers.LogMonitor;
import com.lbi.logger.listeners.ILogListener;
import com.lbi.logger.models.SimpleLbiTextContent;

public class LTabItem extends CTabItem
{
	private int CHECK_DELAY = 1000;
	private Timer timer;
	
	private String __text;
	private String log_path;
	
	private String buffer;
	private StyledText styledText;
	private Composite body;
	private LogMonitor log_monitor;
	private SimpleLbiTextContent text_content;
	
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
				// TODO Auto-generated method stub
				stopLogging();
			}});*/
		log_monitor = new LogMonitor(log_path, styledText.getDisplay(), CHECK_DELAY);
		log_monitor.addListener(new ILogListener(){

			@Override
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
		text_content = new SimpleLbiTextContent();
	}
	
	protected void addContent(String newContent)
	{
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		styledText = new StyledText(body, SWT.MULTI | SWT.WRAP | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
		styledText.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				// TODO Auto-generated method stub
				stopLogging();
			}});
		/*styledText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){
				System.out.println("FocusGained:" + e.toString());
			}
		
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				System.out.println("FocusLost:" + e.toString());
			}});*/
		/*styledText.addMouseMoveListener(new MouseMoveListener(){

			public void mouseMove(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
		});*/
		styledText.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e)
			{
				// TODO Auto-generated method stub
				cleanBuffer();
			}

			public void mouseDown(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

			public void mouseUp(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		
		});
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
		// TODO Auto-generated method stub
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
