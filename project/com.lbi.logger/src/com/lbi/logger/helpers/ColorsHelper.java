package com.lbi.logger.helpers;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorsHelper
{
	private static Display display;
	
	public static void setDisplay ( Display new_display )
	{
		display = new_display;
	}
	
	public static Color getColour( int id )
	{
		return display.getSystemColor(id);
	}
	
	public static Color newColor(int r, int g, int b){
		return new Color(display, r, g, b);
	}

	public static Color getColor(String color)
	{
		if(color.startsWith("#")) color = color.substring(color.lastIndexOf("#")+1);
		
		int r = Integer.parseInt(color.substring(0,2),16);
		int g = Integer.parseInt(color.substring(2,4),16);
		int b = Integer.parseInt(color.substring(4,6),16);
		
		return newColor(r,g,b);
	}
}
