package com.lbi.logger.helpers;

import java.util.Hashtable;

public class PluginImages
{
	public static final String PLUGIN = "com.lbi.logger.plugin";
	
	private static Hashtable<String, String> map;
	
	public static String getImageNameById ( String id )
	{
		if(map == null) initMap();
		
		String path = map.get(id);
		if(path == null){
			System.err.println("Image with id " + id + " can't be found!");
		}
		
		return path;
	}

	private static void initMap() {
		// TODO Auto-generated method stub
		map = new Hashtable<String, String>();
		map.put(PLUGIN, "icons/view.gif");
	}
}
