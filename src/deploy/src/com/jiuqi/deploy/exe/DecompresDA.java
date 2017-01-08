/*
 * @(#)Uncomp.java  2012-7-19	
 *
 * Copyright  2010 Join-Cheer Corporation Copyright (c) All rights reserved.
 * BEIJING JOIN-CHEER SOFTWARE CO.,LTD
 */

package com.jiuqi.deploy.exe;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jiuqi.deploy.util.IMonitor;

/**
 * <p>Title:</p>





 * @author zenglizhi
 * @version $Revision: 1.0 $
 */
public class DecompresDA {

	private IMonitor monitor;
	private String filePath;
	private String outPath;
	
	/**
	 * @param monitor
	 *            �����
	 * @param daPath
	 *            da ��
	 * @param outPath
	 *            ���Ŀ¼
	 */
	public DecompresDA(IMonitor monitor, String daPath, String outPath) {
		this.monitor = monitor;
		this.filePath = daPath;
		this.outPath = outPath;
	}
	
	public void run(){
		if(null == filePath)
			return;
		// monitor.start();
		try {
			unzipda(filePath);
		} catch (IOException e) {
			monitor.error("��ѹ�쳣" + e.getMessage());
		}
		// monitor.finish();
	}
	/**
	 * Method zip.
	 * @param zipFilePath String
	 * @param targetDirectoryPath String
	 * @throws IOException
	 */
	static public void zip(String zipFilePath,String targetDirectoryPath) throws IOException{
		File zipFile = new File(zipFilePath);
		// ��Ŀ��Ŀ¼Ϊ�յ�ʱ���ļ���ѹ���ļ�����Ŀ¼��
		String targetDirectory = targetDirectoryPath;
		if (targetDirectoryPath == null) {
			targetDirectory = zipFile.getParent()+File.separator;
		}
		
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry;
		// ������ѹ����ļ��С�
		while ((entry = zis.getNextEntry()) != null) {
			if (!entry.isDirectory()) {
				continue;
			}
			File directory = new File(targetDirectory, entry.getName());
			if (!directory.exists()) {
				if (!directory.mkdirs()) {
					System.exit(0);
				}
				zis.closeEntry();
			}

		}
		zis.close();
	}
	/**
	 * Method unzipda. ��ѹda�������µ�app Ŀ¼
	 * @param daPath String
	 * @throws IOException
	 */
	public void unzipda(String daPath) throws IOException{
		String daName = getTargetDirPath();
		monitor.propt("  ��ѹDA��...");
		unzip(daPath,daName);
		// String listPath = daName+File.separator+"app";
		File appPath = new File(daName);
		File[] listFiles = appPath.listFiles();				
		if(null ==listFiles)
			return;
		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			if (file.isFile() && file.getName().endsWith(".app")) {
				String targetDirPath = getTargetDirPath();
				unzip(file.getPath(), targetDirPath);
			}
		}
	}
	/**
	 * Method getTargetDirPath.
	 * @param zipFile File
	 * @param prix String
	 * @return String
	 */
	public String getTargetDirPath() {
		File file = new File(outPath);
		if(!file.exists())
			file.mkdir();
		return file.getPath();
	}
	
	/**
	 * Method unzip.
	 * @param zipFilePath String
	 * @param targetDirectory String
	 * @throws IOException
	 */
	public void unzip(String zipFilePath,String targetDirectory) throws IOException{
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry;
		while (((entry = zis.getNextEntry()) != null)) {
			if (entry.isDirectory()) {
				continue;
			}
			 String name = entry.getName();
			String[] split = name.split("/");
			// String lastPath = targetDirectory;
			// for(int i=0;i<split.length-1;i++){
			// String newPath =lastPath+File.separator+split[i];
			// File dir = new File(newPath);
			// if (!dir.exists()) {
			// dir.mkdir();
			// }
			// lastPath = dir.getPath();
			// }
			File unzippedFile = new File(targetDirectory, split[split.length - 1]);
			String filePath = unzippedFile.getPath();
			if (filePath.endsWith(".")) {
				String newPath = filePath.substring(0,filePath.length() - 1);
				File dir = new File(newPath);
				if (!dir.exists()) {
					dir.mkdir();
				}
				continue;
			}

			if (!unzippedFile.exists()) {
				unzippedFile.createNewFile();
				monitor.propt("  ��ѹ�ļ�..." + unzippedFile.getName());
				
			}
			FileOutputStream fout = new FileOutputStream(unzippedFile);
			DataOutputStream dout = new DataOutputStream(fout);

			byte[] b = new byte[1024];
			int len = 0;
			while ((len = zis.read(b)) != -1) {
				dout.write(b, 0, len);
			}
			dout.close();
			fout.close();
			zis.closeEntry();
			// monitor.process(index/6);
			// ++index;
		}
		zis.close();
	}

	private static int index = 1;

	/**
	 * Method main.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {

		File packFile = null;
		do {
			Scanner sc = new Scanner(System.in);
			System.out.println("����DA���ľ���·��(���롰 q ���˳�):>>");
			String nextLine = sc.nextLine();
			if ("q".equalsIgnoreCase(nextLine))
				System.exit(0);
			packFile = new File(nextLine);
		} while ((!packFile.isFile()) || (!packFile.getName().endsWith(".da")));
		DecompresDA da = new DecompresDA(new DefMonitor(), packFile.getAbsolutePath(),
				packFile.getParentFile().getAbsolutePath());
		da.run();
	}

static class DefMonitor implements IMonitor {
	public long t_start;
	public long t_end;
	public void process(int process) {
		System.out.print("("+process/100+"%)");
	}

	public void propt(String msg) {
		System.out.println(msg);
	}

	public void finish() {
		t_end = System.currentTimeMillis();
		process(100);
		propt("��ѹ���......\n\r��ʱ��"+ (t_end-t_start)/1000+"�롣");
	}

	public void start() {
		t_start = System.currentTimeMillis();
		propt("��ѹ��ʼִ��......");
		process(0);
	}

	@Override
	public void error(String error) {

	}

		@Override
		public void propt(int process, String msg) {
			// TODO Auto-generated method stub

		}
	
}

}
