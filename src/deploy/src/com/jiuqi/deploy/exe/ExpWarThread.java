/*
 * @(#)DecomDaThread.java  2012-7-24	
 *
 * Copyright  2010 Join-Cheer Corporation Copyright (c) All rights reserved.
 * BEIJING JOIN-CHEER SOFTWARE CO.,LTD
 */

package com.jiuqi.deploy.exe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jiuqi.deploy.exe.DecompresDA.DefMonitor;
import com.jiuqi.deploy.util.IMonitor;
import com.jiuqi.deploy.util.IOUtils;

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
public class ExpWarThread implements Runnable {
	private static final String[] REMOVEJARS = { "com.jiuqi.dna.core.jetty", "javax.servlet", "org.eclipse.jetty" };

	private static final String[] WARRESOURCEFILE = { 	"resource/war/META-INF/MANIFEST.MF",
													 	"resource/war/WEB-INF/faces-config.xml", 
													 	"resource/war/WEB-INF/web.xml", 
													 	"resource/war/WEB-INF/lib/com.jiuqi.dna.core.proxy.jar", 
													 	"resource/war/WEB-INF/lib/cxf.was.adapter.jar", 
													 	"resource/war/WEB-INF/eclipse/.eclipseproduct", 
													 	"resource/war/WEB-INF/eclipse/launch.ini", 
													 	"resource/war/WEB-INF/eclipse/configuration/config.ini", 
													 	"resource/war/WEB-INF/eclipse/plugins/com.jiuqi.dna.core.bridge_1.0.0.jar", 
													 	
	};

	final private IMonitor monitor;
	final private File dafile;
	private String projectname;

	public ExpWarThread(IMonitor monitor, String projectname, File dafile) {
		this.monitor = monitor;
		this.projectname = projectname;
		this.dafile = dafile;
	}

	public void run() {
		monitor.start();
		try {
			export();
		} catch (Exception e) {
			monitor.error(e.getMessage());
		}
		monitor.finish();
	}

	public static void main(String[] args) throws Exception {
		String da = "F:\\sr_da\\tt\\JQR_MinHangTJ_PUB.da";
		File dafile = new File(da);
		ExpWarThread t = new ExpWarThread(new DefMonitor(), "CAAC", dafile);
		t.export();
	}



	private static void buildwartemplate(String root) throws IOException {
		File rfile = new File(root);
		if (!rfile.exists()) {
			rfile.mkdirs();
		}
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		for (String relFilename : WARRESOURCEFILE) {
			String[] names = relFilename.split("/");
			String curname = root;
			int i = 2;
			for (; i < names.length - 1; i++) {
				String warpath = curname + File.separator + names[i];
				File tp = new File(warpath);
				if (tp.isFile()) {
					IOUtils.delete(warpath);
				}
				if (!tp.exists()) {
					tp.mkdirs();
				}
				curname = warpath;
			}
			String fullfilename = curname + File.separator + names[i];
			InputStream is = contextClassLoader.getResourceAsStream(relFilename);
			File file = new File(fullfilename);
			OutputStream ou = new FileOutputStream(file);
			IOUtils.copyStream(is, ou);
		}

	}

	/**
	 * 1、解压da ，2、去除多余包，3, 制作war包环境2、添加第三方包 3、压缩为war包
	 */
	public void export() throws Exception {
		monitor.propt(1, "解压 DA 包...");
		String daPath = dafile.getAbsolutePath();
		String outdatemp = dafile.getParentFile().getAbsolutePath() + File.separator + System.currentTimeMillis();
		DecompresDA deda = new DecompresDA(monitor, daPath, outdatemp);
		deda.run();
		/// delete jqr
		monitor.propt(55, "删除多余jar包...");
		File tempFile = new File(outdatemp);
		for (File file : tempFile.listFiles()) {
			for (String string : REMOVEJARS) {
				if (file.getName().contains(string)) {
					file.delete();
				}
			}
		}
		monitor.propt(65, "生成 war 包模板文件 ...");
		String root = dafile.getParentFile().getAbsolutePath();
		String warpath = root + File.separator + projectname;

		IOUtils.delete(warpath);
		File project = new File(warpath);
		if (!project.exists()) {
			project.mkdirs();
		}
		buildwartemplate(warpath);
		monitor.propt(70, "正在构建 war 包目录 ...");

		for (File file : tempFile.listFiles()) {
			if (file.getName().endsWith(".jar")) {
				File target = new File(warpath + "/WEB-INF/eclipse/plugins/" + file.getName());
				IOUtils.copyFile(file, target);
				monitor.propt("  拷贝文件..." + file.getName());
			}
		}
		monitor.propt(82, "正在压缩  war 包 ...");
		File[] listFiles = project.listFiles();
		File warFile = new File(warpath + ".war");
		IOUtils.zipFiles(warFile, "", listFiles);
		monitor.propt(99, "删除临时文件 ...");
		IOUtils.deleteDir(warpath);
		IOUtils.delete(outdatemp);
		monitor.propt(100, "WAR 包目录：" + warFile.getAbsolutePath());
	}

}
