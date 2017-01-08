/*
 * @(#)IMonitor.java  2012-7-24	
 *
 * Copyright  2010 Join-Cheer Corporation Copyright (c) All rights reserved.
 * BEIJING JOIN-CHEER SOFTWARE CO.,LTD
 */

package com.jiuqi.deploy.util;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * @author:  zenglizhi
 * @time:    2012-7-24
 * @version:  v1.0
 */
public interface IMonitor {

	public void start();

	public void process(int process);
	
	public void propt(String msg);

	public void propt(int process, String msg);

	public void error(String error);

	public void finish();
}
