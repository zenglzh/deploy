/**
 * 
 */
package com.jiuqi.deploy.util;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Unicode格式文件的头部可能会存在BOM(Byte Order Mark)，部分XML解析器不能处理含有BOM的XML。
 * 可以使用<code>UTFInputStream</code>来自动处理BOM
 * @author huangkaibin
 *
 */
public class UTFInputStream extends BufferedInputStream {

	public static final byte[] BOM_UTF8 = new byte[] {-17, -69, -65};
	public static final byte[] BOM_UTF16_BE = new byte[] {-2, -1};
	public static final byte[] BOM_UTF16_LE = new byte[] {-1, -2};
	public static final byte[] BOM_UTF32_BE = new byte[] {0, 0, -2, -1};
	public static final byte[] BOM_UTF32_LE = new byte[] {-1, -2, 0, 0};

	public static final byte[][] BOM = new byte[][] { BOM_UTF8, BOM_UTF16_BE,
			BOM_UTF16_LE, BOM_UTF32_BE, BOM_UTF32_LE };

	private boolean skipBom = true;

	private volatile boolean first = true;

	public UTFInputStream(InputStream in) {
		super(in);
	}

	public UTFInputStream(InputStream in, int size) {
		super(in, size);
	}

	@Override
	public synchronized int read() throws IOException {
		skipBOM();
		return super.read();
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		skipBOM();
		return super.read(b, off, len);
	}

	private synchronized void skipBOM() throws IOException {
		if (!skipBom || !first) {
			return;
		}
		super.mark(8192);
		for (int i = 0; i < BOM.length; ++i) {
			byte[] b = new byte[BOM[i].length];
			super.read(b, 0, b.length);
			if (Arrays.equals(b, BOM[i])) {
				return;
			}
			super.reset();
		}
		first = false;
	}

	public boolean isSkipBom() {
		return skipBom;
	}

	public void setSkipBom(boolean skipBom) {
		this.skipBom = skipBom;
	}

}
