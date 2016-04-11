/**
 * 
 */
package com.jiuqi.deploy.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * IO������
 * @author huangkaibin
 *
 */
public class IOUtils {

	private static final int BUFFER_SIZE = 1 << 15;

	/**
	 * ����
	 * @param source Դ·��
	 * @param target Ŀ��·��
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copy(String source, String target)
			throws IOException {
		copy(new File(source), new File(target));
	}

	/**
	 * ����
	 * @param source Դ
	 * @param target Ŀ��
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copy(File source, File target)
			throws IOException {
		if (existsFile(source)) {
			copyFile(source, target);
		} else if (existsDirectory(source)) {
			copyDirectory(source, target);
		}
	}

	/**
	 * ��������Ŀ¼
	 * @param source ԴĿ¼
	 * @param target Ŀ��Ŀ¼·��
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copyDirectory(File source, String target)
			throws IOException {
		String sourcePath = source.getAbsolutePath();
		List<File> list = new ArrayList<File>(200);
		list.add(source);
		for (int i = 0; i < list.size(); ++i) {
			File file = list.get(i);
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				Collections.addAll(list, children);
			}
		}

		for (int i = 0; i < list.size(); ++i) {
			File file = list.get(i);
			String path = file.getAbsolutePath();
			String relativePath = path.substring(sourcePath.length());
			File f = new File(target + "/" + relativePath);
			if (file.isDirectory()) {
				f.mkdirs();
			} else {
				copyFile(file, f);
			}
		}
	}
	
	/**
	 * ��������Ŀ¼
	 * @param source ԴĿ¼
	 * @param target Ŀ���ļ�
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copyDirectory(File source, File target) throws IOException {
		copyDirectory(source, target.getAbsolutePath());
	}

	/**
	 * ���Ƶ����ļ�
	 * @param source Դ�ļ�·��
	 * @param target Ŀ���ļ�·��
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copyFile(String source, String target) throws IOException {
		copyFile(new File(source), new File(target));
	}

	/**
	 * ���Ƶ����ļ�
	 * @param source Դ�ļ�
	 * @param target Ŀ���ļ�
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copyFile(File source, File target) throws IOException {
		File parent = target.getParentFile();
		if (!parent.exists()) {
			if (!parent.mkdirs()) {
				System.err.println("Cannot create directory: "
						+ parent.getAbsolutePath());
				return;
			}
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(target);
			copyStream(in, out);
		} finally {
			closeStream(in);
			closeStream(out);
		}
	}

	/**
	 * �������������ݴ����������Ƶ��������
	 * @param in Դ�ļ�������
	 * @param out Ŀ���ļ������
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[BUFFER_SIZE];
		int count = 0;
		while ((count = in.read(buf)) > 0) {
			out.write(buf, 0, count);
		}
	}

	/**
	 * �������������ݴ����������Ƶ��������
	 * @param in Դ�ļ�������
	 * @param out Ŀ���ļ������
	 * @throws IOException ������ƹ����г��ִ������׳�����쳣
	 */
	public static void copyStream(Reader in, Writer out) throws IOException {
		char[] buf = new char[BUFFER_SIZE];
		int count = 0;
		while ((count = in.read(buf)) > 0) {
			out.write(buf, 0, count);
		}
	}

	/**
	 * ɾ��ָ�����ļ���Ŀ¼
	 * @param path �ļ���Ŀ¼��·��
	 * @return ���ɾ���ɹ����ͷ���<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean delete(String path) {
		return deleteFile(new File(path));
	}

	/**
	 * ɾ��ָ�����ļ���Ŀ¼
	 * @param file �ļ���Ŀ¼
	 * @return ���ɾ���ɹ����ͷ���<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean deleteFile(File file) {
		if (!file.exists()) {
			return true;
		}
		List<File> list = new ArrayList<File>(100);
		list.add(file);
		while(!list.isEmpty()) {
			int idx = list.size() - 1;
			File f = list.get(idx);
			boolean remove = true;
			if (f.isDirectory()) {
				File[] children = f.listFiles();
				if (children != null && children.length != 0) {
					remove = false;
					Collections.addAll(list, children);
				}
			}
			if (remove) {
				if (f.delete()) {
					list.remove(idx);
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * �ж�ָ�����ļ��Ƿ����
	 * @param path �ļ�·��
	 * @return ����ļ����ڣ��ͷ���<tt>true</tt>������ļ������ڣ�����·��ָ���Ĳ����ļ����ͷ���<tt>false</tt>��
	 */
	public static boolean existsFile(String path) {
		return existsFile(new File(path));
	}

	/**
	 * �ж�ָ�����ļ��Ƿ����
	 * @param directory �ļ�����Ŀ¼
	 * @param fileName �ļ���
	 * @return ����ļ����ڣ��ͷ���<tt>true</tt>������ļ������ڣ�����·��ָ���Ĳ����ļ����ͷ���<tt>false</tt>��
	 */
	public static boolean existsFile(String directory, String fileName) {
		return existsFile(new File(directory, fileName));
	}

	/**
	 * �ж�ָ����Ŀ¼�Ƿ����
	 * @param path Ŀ¼·��
	 * @return ���Ŀ¼���ڣ��ͷ���<tt>true</tt>�����Ŀ¼�����ڣ�����·��ָ���Ĳ���Ŀ¼���ͷ���<tt>false</tt>��
	 */
	public static boolean existsDirectory(String path) {
		return existsDirectory(new File(path));
	}

	/**
	 * �ж�ָ�����ļ��Ƿ����
	 * @param file �ļ�
	 * @return ����ļ����ڣ��ͷ���<tt>true</tt>������ļ������ڣ�����·��ָ���Ĳ����ļ����ͷ���<tt>false</tt>��
	 */
	public static boolean existsFile(File file) {
		if (file == null) {
			throw new NullPointerException();
		}
		return file.exists() && file.isFile();
	}

	/**
	 * �ж�ָ����Ŀ¼�Ƿ����
	 * @param directory Ŀ¼
	 * @return ���Ŀ¼���ڣ��ͷ���<tt>true</tt>�����Ŀ¼�����ڣ�����·��ָ���Ĳ���Ŀ¼���ͷ���<tt>false</tt>��
	 */
	public static boolean existsDirectory(File directory) {
		if (directory == null) {
			throw new NullPointerException();
		}
		return directory.exists() && directory.isDirectory();
	}

	/**
	 * ����ָ����Ŀ¼
	 * @param path Ŀ¼·��
	 * @return ��������ɹ�����Ŀ¼ԭ���ʹ��ڣ��ͷ���<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean makeDirectory(String path) {
		return makeDirectory(new File(path));
	}

	/**
	 * ����ָ����Ŀ¼
	 * @param directory Ŀ¼
	 * @return ��������ɹ�����Ŀ¼ԭ���ʹ��ڣ��ͷ���<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean makeDirectory(File directory) {
		if (directory == null) {
			throw new NullPointerException();
		}
		if (existsDirectory(directory)) {
			return true;
		}
		return directory.mkdirs();
	}

	/**
	 * ��ȡ�ļ�������
	 * @param filePath �ļ�·��
	 * @return ���ַ�������ʽ�����ļ�����
	 * @throws IOException �����ȡ�ļ�ʧ�ܣ����׳����쳣
	 */
	public static String getFileContentsString(String filePath) throws IOException {
		return getFileContentsString(new File(filePath));
	}

	/**
	 * ��ȡ�ļ�������
	 * @param file �ļ�
	 * @return ���ַ�������ʽ�����ļ�����
	 * @throws IOException �����ȡ�ļ�ʧ�ܣ����׳����쳣
	 */
	public static String getFileContentsString(File file) throws IOException {
		Reader in = null;
		try {
			in = new FileReader(file);
			return getContentsString(in);
		} finally {
			closeStream(in);
		}
	}


	/**
	 * ��ȡ�ļ�������
	 * @param in �ļ�������
	 * @return ���ַ�������ʽ�����ļ�����
	 * @throws IOException �����ȡ�ļ�ʧ�ܣ����׳����쳣
	 */
	public static String getContentsString(InputStream in) throws IOException {
		return getContentsString(new InputStreamReader(in));
	}

	/**
	 * ��ȡ�ļ�������
	 * @param in �ļ�������
	 * @return ���ַ�������ʽ�����ļ�����
	 * @throws IOException �����ȡ�ļ�ʧ�ܣ����׳����쳣
	 */
	public static String getContentsString(Reader in) throws IOException {
		StringBuffer buffer = new StringBuffer();
		char[] b = new char[BUFFER_SIZE];
		int count;
		while ((count = in.read(b)) > 0) {
			buffer.append(b, 0, count);
		}
		return buffer.toString();
	}

	/**
	 * ��ȡ�ļ�������
	 * @param filePath �ļ�·��
	 * @return ���ֽ��������ʽ�����ļ�����
	 * @throws IOException �����ȡ�ļ�ʧ�ܣ����׳����쳣
	 */
	public static byte[] getFileContentsData(String filePath) throws IOException {
		return getFileContentsData(new File(filePath));
	}

	/**
	 * ��ȡ�ļ�������
	 * @param file �ļ�
	 * @return ���ֽ��������ʽ�����ļ�����
	 * @throws IOException �����ȡ�ļ�ʧ�ܣ����׳����쳣
	 */
	public static byte[] getFileContentsData(File file) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return getContentsData(in);
		} finally {
			closeStream(in);
		}
	}

	/**
	 * ��ȡ������������
	 * @param in ������
	 * @return ���ֽ��������ʽ��������������
	 * @throws IOException �����ȡ������ʧ�ܣ����׳����쳣
	 */
	public static byte[] getContentsData(InputStream in) throws IOException {
		return getContentsData(in, -1);
	}

	/**
	 * ���������ж�ȡ������maxSize��С������
	 * @param in ������
	 * @param maxSize ��ȡ���ݵĴ�С���ƣ������-1��ʾ�����ơ�
	 * @return ���ֽ��������ʽ��������������
	 * @throws IOException �����ȡ������ʧ�ܣ����׳����쳣
	 */
	public static byte[] getContentsData(InputStream in, long maxSize)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[getBufferSize(maxSize)];
		int count;
		long size = 0;
		while (((count = in.read(b, 0, getReadLength(b.length, size, maxSize))) > 0)
				&& (maxSize == -1 || size < maxSize)) {
			size += count;
			out.write(b, 0, count);
		}
		return out.toByteArray();
	}

	private static int getBufferSize(long maxSize) {
		return maxSize != -1 ? (int) Math.min(BUFFER_SIZE, maxSize) : BUFFER_SIZE;
	}

	private static int getReadLength(int length, long size, long maxSize) {
		return maxSize != -1 ? (int) Math.min(length, maxSize - size) : length;
	}

	/**
	 * ��ָ��������д���ļ���
	 * @param filePath �ļ�·��
	 * @param content �ļ��������ַ���
	 * @throws IOException ���д�ļ�ʧ�ܣ����׳����쳣
	 */
	public static void writeFile(String filePath, String content) throws IOException {
		writeFile(new File(filePath), content);
	}

	/**
	 * ��ָ��������д���ļ���
	 * @param filePath �ļ�·��
	 * @param in ������
	 * @throws IOException ���д�ļ�ʧ�ܣ����׳����쳣
	 */
	public static void writeFile(String filePath, InputStream in) throws IOException {
		writeFile(new File(filePath), in);
	}

	/**
	 * ��ָ��������д���ļ���
	 * @param filePath �ļ�·��
	 * @param in ������
	 * @throws IOException ���д�ļ�ʧ�ܣ����׳����쳣
	 */
	public static void writeFile(String filePath, Reader in) throws IOException {
		writeFile(new File(filePath), in);
	}

	/**
	 * ��ָ��������д���ļ���
	 * @param file �ļ�
	 * @param content �ļ��������ַ���
	 * @throws IOException ���д�ļ�ʧ�ܣ����׳����쳣
	 */
	public static void writeFile(File file, String content) throws IOException {
		Reader in = null;
		try {
			in = new StringReader(content);
			writeFile(file, in);
		} finally {
			closeStream(in);
		}
	}

	/**
	 * ��ָ��������д���ļ���
	 * @param file �ļ�
	 * @param in ������
	 * @throws IOException ���д�ļ�ʧ�ܣ����׳����쳣
	 */
	public static void writeFile(File file, InputStream in) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			copyStream(in, out);
		} finally {
			closeStream(out);
		}
	}

	/**
	 * ��ָ��������д���ļ���
	 * @param file �ļ�
	 * @param in ������
	 * @throws IOException ���д�ļ�ʧ�ܣ����׳����쳣
	 */
	public static void writeFile(File file, Reader in) throws IOException {
		Writer out = null;
		try {
			out = new FileWriter(file);
			copyStream(in, out);
		} finally {
			closeStream(out);
		}
	}

	/**
	 * �ر���
	 * @param stream ��
	 */
	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ȡJava��ʱĿ¼�����Ŀ¼һ��Ҳ�ǲ���ϵͳ���õ���ʱĿ¼
	 * @return Java��ʱĿ¼
	 */
	public static String getTempDir() {
		return System.getenv("java.io.tmpdir");
	}

	/**
	 * ������������ʱĿ¼�������ʱĿ¼����Java��ʱĿ¼�µ�һ���յ���Ŀ¼
	 * @return ��ʱĿ¼
	 */
	public static String generateTempDir() {
		String temp = getTempDir();
		long l = System.currentTimeMillis();
		File f = new File(temp, String.valueOf(l));
		Random r = new Random();
		while (existsDirectory(f)) {
			l = r.nextLong();
			f = new File(temp, String.valueOf(l));
		}
		makeDirectory(f);
		return f.getAbsolutePath();
	}

	public static boolean workDirIsOk(String parameter) {
		boolean returnValue = false;
		try {
			File file = new File(parameter);
			if (file.exists()) {
				File tempFile = new File(parameter + File.separator + "a.txt");
				tempFile.createNewFile();
				tempFile.delete();
				returnValue = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

}
