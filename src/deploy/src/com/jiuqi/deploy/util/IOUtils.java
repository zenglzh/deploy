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
 * IO工具类
 * @author huangkaibin
 *
 */
public class IOUtils {

	private static final int BUFFER_SIZE = 1 << 15;

	/**
	 * 复制
	 * @param source 源路径
	 * @param target 目标路径
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
	 */
	public static void copy(String source, String target)
			throws IOException {
		copy(new File(source), new File(target));
	}

	/**
	 * 复制
	 * @param source 源
	 * @param target 目标
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
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
	 * 复制整个目录
	 * @param source 源目录
	 * @param target 目标目录路径
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
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
	 * 复制整个目录
	 * @param source 源目录
	 * @param target 目标文件
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
	 */
	public static void copyDirectory(File source, File target) throws IOException {
		copyDirectory(source, target.getAbsolutePath());
	}

	/**
	 * 复制单个文件
	 * @param source 源文件路径
	 * @param target 目标文件路径
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
	 */
	public static void copyFile(String source, String target) throws IOException {
		copyFile(new File(source), new File(target));
	}

	/**
	 * 复制单个文件
	 * @param source 源文件
	 * @param target 目标文件
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
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
	 * 复制流，把内容从输入流复制到输出流。
	 * @param in 源文件输入流
	 * @param out 目标文件输出流
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[BUFFER_SIZE];
		int count = 0;
		while ((count = in.read(buf)) > 0) {
			out.write(buf, 0, count);
		}
	}

	/**
	 * 复制流，把内容从输入流复制到输出流。
	 * @param in 源文件输入流
	 * @param out 目标文件输出流
	 * @throws IOException 如果复制过程中出现错误，则抛出这个异常
	 */
	public static void copyStream(Reader in, Writer out) throws IOException {
		char[] buf = new char[BUFFER_SIZE];
		int count = 0;
		while ((count = in.read(buf)) > 0) {
			out.write(buf, 0, count);
		}
	}

	/**
	 * 删除指定的文件或目录
	 * @param path 文件或目录的路径
	 * @return 如果删除成功，就返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	public static boolean delete(String path) {
		return deleteFile(new File(path));
	}

	/**
	 * 删除指定的文件或目录
	 * @param file 文件或目录
	 * @return 如果删除成功，就返回<tt>true</tt>，否则返回<tt>false</tt>
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
	 * 判断指定的文件是否存在
	 * @param path 文件路径
	 * @return 如果文件存在，就返回<tt>true</tt>；如果文件不存在，或者路径指定的不是文件，就返回<tt>false</tt>。
	 */
	public static boolean existsFile(String path) {
		return existsFile(new File(path));
	}

	/**
	 * 判断指定的文件是否存在
	 * @param directory 文件所在目录
	 * @param fileName 文件名
	 * @return 如果文件存在，就返回<tt>true</tt>；如果文件不存在，或者路径指定的不是文件，就返回<tt>false</tt>。
	 */
	public static boolean existsFile(String directory, String fileName) {
		return existsFile(new File(directory, fileName));
	}

	/**
	 * 判断指定的目录是否存在
	 * @param path 目录路径
	 * @return 如果目录存在，就返回<tt>true</tt>；如果目录不存在，或者路径指定的不是目录，就返回<tt>false</tt>。
	 */
	public static boolean existsDirectory(String path) {
		return existsDirectory(new File(path));
	}

	/**
	 * 判断指定的文件是否存在
	 * @param file 文件
	 * @return 如果文件存在，就返回<tt>true</tt>；如果文件不存在，或者路径指定的不是文件，就返回<tt>false</tt>。
	 */
	public static boolean existsFile(File file) {
		if (file == null) {
			throw new NullPointerException();
		}
		return file.exists() && file.isFile();
	}

	/**
	 * 判断指定的目录是否存在
	 * @param directory 目录
	 * @return 如果目录存在，就返回<tt>true</tt>；如果目录不存在，或者路径指定的不是目录，就返回<tt>false</tt>。
	 */
	public static boolean existsDirectory(File directory) {
		if (directory == null) {
			throw new NullPointerException();
		}
		return directory.exists() && directory.isDirectory();
	}

	/**
	 * 创建指定的目录
	 * @param path 目录路径
	 * @return 如果创建成功或者目录原来就存在，就返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	public static boolean makeDirectory(String path) {
		return makeDirectory(new File(path));
	}

	/**
	 * 创建指定的目录
	 * @param directory 目录
	 * @return 如果创建成功或者目录原来就存在，就返回<tt>true</tt>，否则返回<tt>false</tt>
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
	 * 读取文件的内容
	 * @param filePath 文件路径
	 * @return 以字符串的形式返回文件内容
	 * @throws IOException 如果读取文件失败，就抛出该异常
	 */
	public static String getFileContentsString(String filePath) throws IOException {
		return getFileContentsString(new File(filePath));
	}

	/**
	 * 读取文件的内容
	 * @param file 文件
	 * @return 以字符串的形式返回文件内容
	 * @throws IOException 如果读取文件失败，就抛出该异常
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
	 * 读取文件的内容
	 * @param in 文件输入流
	 * @return 以字符串的形式返回文件内容
	 * @throws IOException 如果读取文件失败，就抛出该异常
	 */
	public static String getContentsString(InputStream in) throws IOException {
		return getContentsString(new InputStreamReader(in));
	}

	/**
	 * 读取文件的内容
	 * @param in 文件输入流
	 * @return 以字符串的形式返回文件内容
	 * @throws IOException 如果读取文件失败，就抛出该异常
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
	 * 读取文件的内容
	 * @param filePath 文件路径
	 * @return 以字节数组的形式返回文件内容
	 * @throws IOException 如果读取文件失败，就抛出该异常
	 */
	public static byte[] getFileContentsData(String filePath) throws IOException {
		return getFileContentsData(new File(filePath));
	}

	/**
	 * 读取文件的内容
	 * @param file 文件
	 * @return 以字节数组的形式返回文件内容
	 * @throws IOException 如果读取文件失败，就抛出该异常
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
	 * 读取输入流的内容
	 * @param in 输入流
	 * @return 以字节数组的形式返回输入流内容
	 * @throws IOException 如果读取输入流失败，就抛出该异常
	 */
	public static byte[] getContentsData(InputStream in) throws IOException {
		return getContentsData(in, -1);
	}

	/**
	 * 从输入流中读取不超过maxSize大小的内容
	 * @param in 输入流
	 * @param maxSize 读取数据的大小限制，如果传-1表示不限制。
	 * @return 以字节数组的形式返回输入流内容
	 * @throws IOException 如果读取输入流失败，就抛出该异常
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
	 * 把指定的内容写到文件中
	 * @param filePath 文件路径
	 * @param content 文件的内容字符串
	 * @throws IOException 如果写文件失败，就抛出该异常
	 */
	public static void writeFile(String filePath, String content) throws IOException {
		writeFile(new File(filePath), content);
	}

	/**
	 * 把指定的内容写到文件中
	 * @param filePath 文件路径
	 * @param in 输入流
	 * @throws IOException 如果写文件失败，就抛出该异常
	 */
	public static void writeFile(String filePath, InputStream in) throws IOException {
		writeFile(new File(filePath), in);
	}

	/**
	 * 把指定的内容写到文件中
	 * @param filePath 文件路径
	 * @param in 输入流
	 * @throws IOException 如果写文件失败，就抛出该异常
	 */
	public static void writeFile(String filePath, Reader in) throws IOException {
		writeFile(new File(filePath), in);
	}

	/**
	 * 把指定的内容写到文件中
	 * @param file 文件
	 * @param content 文件的内容字符串
	 * @throws IOException 如果写文件失败，就抛出该异常
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
	 * 把指定的内容写到文件中
	 * @param file 文件
	 * @param in 输入流
	 * @throws IOException 如果写文件失败，就抛出该异常
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
	 * 把指定的内容写到文件中
	 * @param file 文件
	 * @param in 输入流
	 * @throws IOException 如果写文件失败，就抛出该异常
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
	 * 关闭流
	 * @param stream 流
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
	 * 获取Java临时目录，这个目录一般也是操作系统配置的临时目录
	 * @return Java临时目录
	 */
	public static String getTempDir() {
		return System.getenv("java.io.tmpdir");
	}

	/**
	 * 创建并返回临时目录，这个临时目录是在Java临时目录下的一个空的子目录
	 * @return 临时目录
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
