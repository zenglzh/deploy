/*
 * @(#)DecomDaThread.java  2012-7-24	
 *
 * Copyright  2010 Join-Cheer Corporation Copyright (c) All rights reserved.
 * BEIJING JOIN-CHEER SOFTWARE CO.,LTD
 */

package com.jiuqi.deploy.exe;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.IMonitor;
import com.jiuqi.deploy.util.StringHelper;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author: zenglizhi
 * @time: 2012-7-24
 * @version: v1.0
 * @see:
 * @since: SR5.0.1
 */
public class ImpdmpThread implements Runnable {

	final private IMonitor monitor;
	final private String filePath;
	private DatabaseConnectionInfo info;
	private String fromuser;
	private String touser;

	public ImpdmpThread(IMonitor monitor, DatabaseConnectionInfo info, String filePath, String fromuser,
			String touser) {
		this.monitor = monitor;
		this.info = info;
		this.filePath = filePath;
		this.fromuser = fromuser;
		this.touser = touser;
	}

	public void run() {
		monitor.start();
		if (null != info) {
			try {
				importDump();
				monitor.finish();
			} catch (Exception e) {
				System.err.println("错误:" + e);
				monitor.error("导入失败，原因：" + e.getMessage());
			}
		} else {
			monitor.error("数据库未连接。");
		}
	}

	public void importDump() throws Exception {
		String cmdStr = "imp " + info.getUsername() + "/" + info.getPassword() + "@//" + info.getHost() + ":"
				+ info.getPort() + "/" + info.getSid() + " file=\"" + filePath + "\" " + " fromuser=" + fromuser
				+ " touser=" + touser;

		if (!StringHelper.isEmpty(fromuser)) {
			cmdStr += " fromuser=" + fromuser;
		}
		if (!StringHelper.isEmpty(touser)) {
			cmdStr += " touser=" + touser;
		}

		System.out.println(cmdStr);
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmdStr);
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
		// 读取ErrorStream很关键，这个解决了挂起的问题。
		while ((line = br.readLine()) != null) {
			System.err.println(line);
			monitor.propt(line);
		}
		br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			monitor.propt(line);
		}
		process.waitFor();
	}

}
