package com.lbi.logger.models;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class SimpleLbiTextContent {

	private Color red;
	private Color orange;
	private Color green;
	private Color blue;
	private Color black;
	private Color white;
	
	private String content;
	private StringBuffer formatted_buffer;
	
	boolean inFatal = false, inError = false, inWarning = false, inInfo = false, inDebug = false;
	private int buffer_pos;
	
	String separator;
	private int starting_buffer_pos;

	public SimpleLbiTextContent(Display display) {
		red = display.getSystemColor(SWT.COLOR_RED);
		orange = new Color(display, 255, 153, 0);
        green = new Color(display, 0, 153, 0);
        blue = display.getSystemColor(SWT.COLOR_BLUE);
        black = display.getSystemColor(SWT.COLOR_BLACK);
        white = display.getSystemColor(SWT.COLOR_WHITE);
        
        separator = System.getProperty("line.separator");
	}
	
	public void setContent(String content, int starting_buffer_pos) {
//		System.out.println("SimpleLbiTextContent.setContent(): " + content);
	    this.content = content;
	    this.starting_buffer_pos = starting_buffer_pos;
	}
	
	public String toPlainText() {
		String[] lines = content.split(separator);
		formatted_buffer = new StringBuffer();
		buffer_pos = starting_buffer_pos;
		//System.out.println("### len:" + lines.length);
		for(int i=0; i<lines.length; i++){
//			System.out.println("format line:" + i);
			formatLine(lines[i]);
		}
		// TODO Auto-generated method stub
		return formatted_buffer.toString();
	}
	
	private void formatLine(String line)
	{
		if(line.length() == 0){
			formatted_buffer.append(separator);
			buffer_pos+=separator.length();
			return;
		}
		int pos=0;
		while(" ".equals(line.charAt(pos))){
			pos++;
		}
		buffer_pos +=pos;
		
		String new_line = (pos > 0 ? line.substring(pos) : line).toLowerCase();
		
		if(new_line.startsWith("fatal:")){
			startRange();
			inFatal = true;
		}else if(new_line.startsWith("error:")){
			startRange();
			inError = true;
		}else if(new_line.startsWith("warning:")){
			startRange();
			inWarning = true;
		}else if(new_line.startsWith("info:")){
			startRange();
			inInfo = true;
		}else if(new_line.startsWith("debug:")){
			startRange();
			inDebug = true;
		}else{
			startRange();
		}
		buffer_pos += new_line.length();
		endRange();
		inFatal = inError = inWarning = inInfo = inDebug = false;
		formatted_buffer.append(line);
		formatted_buffer.append(separator);
		buffer_pos+=separator.length();
	}
	
	protected ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
	protected StyleRange curRange;
    protected int rangeStyle = SWT.NORMAL;
    protected int nesting = 0;
	
	protected void startRange() {
        if (curRange != null) {
            endRange();
        }
        curRange = new StyleRange();
        curRange.start = buffer_pos;
    }
    protected void endRange() {
        if (curRange != null) {
            curRange.length = buffer_pos - curRange.start;
            //System.out.println("state: " +  curRange.length + ": I=" + inItalics + ", B=" + inBold + ", U=" + inUnderline + ", S=" + inStrikeout + ":" + inRed + "x" + inGreen + "x" + inBlue);
            if (inFatal) {
                rangeStyle |= SWT.BOLD;
            }
            else {
                rangeStyle &= ~SWT.BOLD;
            }
            curRange.fontStyle = rangeStyle;

            if(inFatal || inError) {
                curRange.foreground = red;
            }
            else if (inWarning) {
                curRange.foreground = orange;
            }
            else if (inInfo) {
                curRange.foreground = green;
            }
            else if(inDebug){
            	curRange.foreground = blue;
            }
            else {
                curRange.foreground = black;
            }
            if (curRange.background == null) {
                curRange.background = white;
            }
            if (curRange.length > 0) {
                styleRanges.add(curRange);
                //System.out.println("added: " + Integer.toBinaryString(curRange.fontStyle) + "-" + curRange);
            }
        }
        curRange = null;
        if (nesting > 0) {
			startRange();
		}
    }
    
    public StyleRange[] getStyleRanges() {
        return (StyleRange[])styleRanges.toArray(new StyleRange[styleRanges.size()]);    
    }

}
