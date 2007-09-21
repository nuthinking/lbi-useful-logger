package com.lbi.logger.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import com.lbi.logger.helpers.ColorsHelper;
import com.lbi.logger.helpers.StringHelper;
import com.lbi.logger.listeners.ITextContentListener;

public class TextContent
{
	private Color red,orange,green,blue,black,white;
	
	private String content;
	private StringBuffer formatted_buffer;
	
	HashMap<String, TextStyle> defaults_map,groups_map,elements_map;

	private String added_group;
	public String getAddedGroup () { return added_group; }
	private String added_element;
	public String getAddedElement () { return added_element; }
	
	private int buffer_pos;
	
	String separator;
	private int starting_buffer_pos;
	private TextStyle default_style;
	private TextStyle current_style;
	private TextStyle setup_style;

	public TextContent()
	{
		initDefaults();
		
		groups_map = new HashMap<String, TextStyle>();
		elements_map = new HashMap<String, TextStyle>();
        
        separator = System.getProperty("line.separator");
        
        listeners = new Vector<ITextContentListener>();
	}
	
	private Vector<ITextContentListener> listeners;
	public void addListener(ITextContentListener listener)
	{
		listeners.add(listener);
	}
	public void removeListener(ITextContentListener listener)
	{
		listeners.remove(listener);
	}
	
	private void dispatchGroupAdded()
	{
		for (Iterator<ITextContentListener> iter = listeners.iterator(); iter.hasNext();) {
			ITextContentListener listener = iter.next();
			listener.onGroupAdded();
		}
	}
	
	private void dispatchElementAdded()
	{
		for (Iterator<ITextContentListener> iter = listeners.iterator(); iter.hasNext();) {
			ITextContentListener listener = iter.next();
			listener.onElementAdded();
		}
	}
	
	private void dispatchClear()
	{
		
	}

	
	private void initDefaults()
	{		
		initColors();
		
		default_style = new TextStyle();
		setup_style = new TextStyle(white, black);
		
		defaults_map = new HashMap<String, TextStyle>();
		defaults_map.put("fatal", new TextStyle(red, null, true));
		defaults_map.put("error", new TextStyle(red));
		defaults_map.put("warning", new TextStyle(orange));
		defaults_map.put("info", new TextStyle(blue));
		defaults_map.put("debug", new TextStyle(green));
	}
	
	private String getDefaultStart ( String key )
	{
		return "[" + key.toLowerCase() + "]";
	}
	
	private String getCustomStart ( String key )
	{
		return "[" + key.toLowerCase();
	}
	
	private String getCommandString(String new_line)
	{
		return new_line.substring(new_line.indexOf("[")+1, new_line.indexOf("]")).trim();
	}

	private void initColors()
	{
		red = ColorsHelper.getColour(SWT.COLOR_RED);
		orange = ColorsHelper.newColor(255, 153, 0);
        green = ColorsHelper.newColor(0, 153, 0);
        blue = ColorsHelper.getColour(SWT.COLOR_BLUE);
        black = ColorsHelper.getColour(SWT.COLOR_BLACK);
        white = ColorsHelper.getColour(SWT.COLOR_WHITE);
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
		
		startRange();
		
		findStyle(new_line);

		buffer_pos += new_line.length();
		endRange();
		formatted_buffer.append(line);
		formatted_buffer.append(separator);
		buffer_pos+=separator.length();
	}
	
	private void findStyle(String new_line)
	{
		if(isCustom(new_line)) return;
		if(isDefault(new_line)) return;
		if(isSetup(new_line)) return;
		if(isGroup(new_line)) return;
		if(isElement(new_line)) return;
		current_style = default_style;
	}

	private boolean isElement(String new_line)
	{
		if(new_line.startsWith(getCustomStart("element"))){
			String command = getCommandString(new_line);
//			String name = StringHelper.getAttributeFromCommand(command, "name");
			String name = getItemName(command);
			TextStyle style = elements_map.get(name);
			if(style != null){
				current_style = style;
				return true;
			}
		}
		return false;
	}

	private boolean isGroup(String new_line)
	{
		if(new_line.startsWith(getCustomStart("group"))){
			String command = getCommandString(new_line);
//			String name = StringHelper.getAttributeFromCommand(command, "name");
			System.out.println("command:" + command);
			String name = getItemName(command);
			System.out.println("name:" + name);
			TextStyle style = groups_map.get(name);
			if(style != null){
				current_style = style;
				return true;
			}
		}
		return false;
	}

	private String getItemName(String command)
	{
		int start_pos = command.lastIndexOf(" ");
		if(start_pos == -1) return null;
		return command.substring(start_pos+1);
	}
	private boolean isCustom(String new_line)
	{
		if(new_line.startsWith(getCustomStart("custom"))){
			String command = getCommandString(new_line);
			System.out.println("CUSTOM:\"" + command + "\"");
			current_style = TextStyle.getFromCommand(command);
			return true;
		}
		return false;
	}

	private boolean isSetup(String new_line)
	{
		if(new_line.startsWith(getCustomStart("setup"))){
			String command = getCommandString(new_line);
			if(command.startsWith("setup-group")){
				addGroup(command);
			}else if(command.startsWith("setup-element")){
				addElement(command);
			}else if(command.startsWith("setup_clean")){
				dispatchClear();
			}
			current_style = setup_style;
			return true;
		}
		return false;
	}

	private void addGroup(String command)
	{
		added_group = command.split(" ")[1];
		System.out.println("addGroup: name:" + added_group);
		groups_map.put(added_group, TextStyle.getFromCommand(command));
		dispatchGroupAdded();
	}
	
	private void addElement(String command)
	{
		added_element = command.split(" ")[1];
		System.out.println("addElement: name:" + added_element);
		elements_map.put(added_element, TextStyle.getFromCommand(command));
		dispatchElementAdded();
	}

	private boolean isDefault(String new_line)
	{
		Iterator<String> it = defaults_map.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(new_line.startsWith(getDefaultStart(key))){
				current_style = defaults_map.get(key);
				return true;
			}
		}
		return false;
	}

	protected ArrayList<StyleRange> styleRanges;
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
           
            if(current_style.getBold()){
            	rangeStyle |= SWT.BOLD;            	
            }else{
            	rangeStyle &= ~SWT.BOLD;            	
            }
            curRange.fontStyle = rangeStyle;
            curRange.foreground = current_style.getForeground();
            curRange.background = current_style.getBackground();

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

	public String parseContent(String content, int starting_buffer_pos)
	{
		this.content = content;
	    this.starting_buffer_pos = starting_buffer_pos;
	    styleRanges = new ArrayList<StyleRange>();
	    return toPlainText();
	}
	public String[] getGroups()
	{
		System.out.println("TextContent.getGroups:" + groups_map.size());
		String[] groups = new String[groups_map.size()];
		Iterator<String> it = groups_map.keySet().iterator();
		int i=0;
		while(it.hasNext()) {
			String key = it.next().toString();
			System.out.println("--- iterate " + i + " --- " + key);
			groups[i++] = key;
			System.out.println("+ added " + groups[(i-1)]);
		}
//		System.out.println(groups);
		return groups;
	}

}
