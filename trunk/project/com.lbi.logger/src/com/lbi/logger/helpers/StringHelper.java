package com.lbi.logger.helpers;

public class StringHelper
{
	public static String getAttributeFromCommand(String command, String attribute_name)
	{
		// TODO Auto-generated method stub
		int start_pos = command.indexOf(attribute_name);
		if(start_pos == -1)
			return null;
		start_pos = command.indexOf("=",start_pos);
		if(start_pos == -1)
			return null;
		start_pos++;
		int end_pos = command.indexOf(" ", start_pos);
		if(end_pos == -1)
			end_pos = command.length();
		return command.substring(start_pos, end_pos);
	}
}
