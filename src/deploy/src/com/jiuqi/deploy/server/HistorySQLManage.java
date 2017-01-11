package com.jiuqi.deploy.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Vector;

public class HistorySQLManage {

	private List<String> sqls;

	private final static String FILE_NAME = "jsqlhistory.txt";

	public HistorySQLManage() {
		sqls = new Vector<String>();
	}

	public synchronized void load(String homePath) {
		File file = new File(homePath, FILE_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = br.readLine()) != null) {
				sqls.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void store(String homePath) {
		File file = new File(homePath, FILE_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			for (int i = 0; i < sqls.size() && i < 15; i++) {
				bw.write(sqls.get(i));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isEmpty() {
		return sqls.isEmpty();
	}

	public List<String> allSQLs() {
		return sqls;
	}

	public String last() {
		if (sqls.isEmpty()) {
			return "";
		}
		return sqls.get(sqls.size() - 1);
	}

	public void push(String sql) {
		if (!sqls.contains(sql)) {
			sqls.add(sql);
		}
	}

	public void removeAll() {
		sqls.clear();
	}

	public void remove(int index) {
		if (index >= 0 && index < sqls.size()) {
			sqls.remove(index);
		}
	}
}
