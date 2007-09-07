package com.lbi.logger.models;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.lbi.logger.helpers.ColorsHelper;
import com.lbi.logger.helpers.StringHelper;


public class TextStyle
{	
	private Color foreground;
	public void setForeground(Color foreground)
	{
		this.foreground = foreground;
	}
	public Color getForeground () { return foreground; }
	
	private Color background;
	public void setBackground(Color background)
	{
		this.background = background;
	}
	public Color getBackground () { return background; }
	
	private boolean is_bold = false;
	public void setBold(boolean is_bold)
	{
		this.is_bold = is_bold;
	}
	public boolean getBold() { return is_bold; }

	
	public TextStyle ()
	{
		this(ColorsHelper.getColour(SWT.COLOR_BLACK));
	}
	
	public TextStyle ( Color foreground ){
		this(foreground, ColorsHelper.getColour(SWT.COLOR_WHITE));
	}
	
	public TextStyle ( Color foreground, Color background)
	{
		this(foreground, background, false);
	}
	
	public TextStyle ( Color foreground, Color background, boolean is_bold)
	{
		if(foreground == null) foreground = ColorsHelper.getColour(SWT.COLOR_BLACK);
		if(background == null) foreground = ColorsHelper.getColour(SWT.COLOR_WHITE);
		
		this.foreground = foreground;
		this.background = background;
		this.is_bold = is_bold;
	}
	
	public static TextStyle getFromCommand ( String command )
	{
		Color foreground = ColorsHelper.getColor(StringHelper.getAttributeFromCommand(command, "foreground"));
		Color background = ColorsHelper.getColor(StringHelper.getAttributeFromCommand(command, "background"));
		boolean is_bold = (StringHelper.getAttributeFromCommand(command, "bold") != null
				? Boolean.valueOf(StringHelper.getAttributeFromCommand(command, "bold"))
				: false);
		return new TextStyle(foreground, background, is_bold);
	}
}
