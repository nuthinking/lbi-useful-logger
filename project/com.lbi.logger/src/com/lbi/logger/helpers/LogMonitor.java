package com.lbi.logger.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.lbi.logger.listeners.ILogListener;

public class LogMonitor
{
	private String log_path;

	private Vector<String> lines;
	private Vector<ILogListener> listeners;

	private Display display;

	private int interval;

	private Timer timer;

	public int new_content_starting_line = 0;

	private StringBuffer new_content;

	private void dispatchUpdated()
	{
		for (Iterator<ILogListener> iter = listeners.iterator(); iter.hasNext();) {
			ILogListener listener = iter.next();
			listener.onLogUpdate();
		}
	}

	public LogMonitor(String log_path, Display threaded_display, int interval)
	{
		this.log_path = log_path;
		this.display = threaded_display;
		this.interval = interval;

		listeners = new Vector<ILogListener>();
		lines = new Vector<String>();
	}

	public void addListener(ILogListener listener)
	{
		listeners.add(listener);
	}

	private void checkBuffer()
	{
		//System.out.println("### check buffer");

		new_content = new StringBuffer();
		boolean has_changed = false;

		// declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			input = new BufferedReader(new FileReader(log_path));
			String line = null; // not declared within while loop
			/*
			 * readLine is a bit quirky : it returns the content of a line MINUS
			 * the newline. it returns null only for the END of the stream. it
			 * returns an empty String if two newlines appear in a row.
			 */
			int line_index = 0;
			new_content_starting_line = 0;
			while ((line = input.readLine()) != null) {
				//System.out.println("::process line:" + line_index);
				if (!has_changed){
					//if(lines.size() <= line_index) System.out.println("- it will be a new line for sure, index:"+line_index+" len:" + lines.size());
					//if (lines.size() > line_index) System.out.println("comparing:\"" + line + "\" with \"" + lines.get(line_index) + "\"");
					if (lines.size() > line_index && line.equals(lines.get(line_index))) {
						//System.out.println("found same line:" + new_content_starting_line);
						new_content_starting_line = line_index+1;
					} else {
						//System.out.println("found new line:" + line);
						has_changed = true;
					}
				}
				if (has_changed) {
					lines.add(line_index, line);
					//System.out.println("added line, new size:" + lines.size());
					if(lines.size()>line_index+1){
						System.out.println("*** the log file has been probably cleaned ***");
						lines.setSize(line_index+1);
					}
					//System.out.println("append:\"" + line + "\"");
					new_content.append(line);
					new_content.append(System.getProperty("line.separator"));
				}
				line_index++;
			}
			if(line_index==0){
				// the stream was null
				if(lines.size()>0){
					System.out.println("*** the log file has been probably cleaned ***");
					new_content_starting_line = 0;
					lines.clear();
					has_changed = true;
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					// flush and close both "input" and its underlying
					// FileReader
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		//System.out.println("checkbuffer::new_content:\"" + new_content.toString() + "\"");
		if(has_changed) dispatchUpdated();
	}

	public void start()
	{
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run()
			{
				display.syncExec(new Runnable() {
					public void run()
					{
						checkBuffer(); // update view
					}
				});
			}

		}, 0, interval);
	}

	public void stop()
	{
		timer.cancel();
		timer.purge();
	}

	public String getNewContent()
	{
		// TODO Auto-generated method stub
		return new_content.toString();
	}

	public void clean()
	{
		// TODO Auto-generated method stub
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter(log_path));
	        out.write("");
	        out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public void reload()
	{
		lines.clear();
		checkBuffer();
	}
}
