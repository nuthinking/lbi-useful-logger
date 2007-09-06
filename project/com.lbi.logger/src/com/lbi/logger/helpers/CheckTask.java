package com.lbi.logger.helpers;

import java.util.TimerTask;

import com.lbi.logger.views.LTabItem;

public class CheckTask extends TimerTask
{
	private LTabItem view;
	public CheckTask ( LTabItem view )
	{
		this.view = view;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//view.checkBuffer();
	}

}
