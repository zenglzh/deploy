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
public class ExpdmpThread implements Runnable {

	final private IMonitor monitor;
	final private String filePath;
	private DatabaseConnectionInfo info;

	public ExpdmpThread(IMonitor monitor, DatabaseConnectionInfo info, String filePath) {
		this.monitor = monitor;
		this.info = info;
		this.filePath = filePath;
	}

	public void run() {
		monitor.start();
		if (null != info) {
			try {
				exportDump();
				monitor.finish();
			} catch (Exception e) {
				System.err.println("错误:" + e);
				monitor.error("导出失败，原因：" + e.getMessage());
			}
		} else {
			monitor.error("数据库未连接。");
		}
	}

	public void exportDump() throws Exception {
			String cmdStr = "exp " + info.getUsername() + "/" + info.getPassword() + "@//" + info.getHost() + ":"
					+ info.getPort() + "/" + info.getSid() + " file=\"" + filePath + "\" ";
			System.out.println(cmdStr);
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmdStr);
			String line = null;
			// StringBuffer buffer = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
			// 读取ErrorStream很关键，这个解决了挂起的问题。
			while ((line = br.readLine()) != null) {
				System.err.println(line);
				monitor.error(line);
				// buffer.append(line).append("<br />");
			}
			br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				monitor.propt(line);
				// buffer.append(line).append("<br />");
			}
			process.waitFor();
			// if (process.waitFor() != 0) {
			// throw new Exception("导出失败");
			// }
			// return buffer.toString();
	}

}
