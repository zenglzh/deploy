/*
 * @(#)Monitor.java  2012-7-24	
 *
 * Copyright  2010 Join-Cheer Corporation Copyright (c) All rights reserved.
 * BEIJING JOIN-CHEER SOFTWARE CO.,LTD
 */

package com.jiuqi.deploy.exe;

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import com.jiuqi.deploy.util.IMonitor;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * @author:  zenglizhi
 * @time:    2012-7-24
 * @version:  v1.0
 * @since:    SR5.0.1
 */
public class Monitor implements IMonitor {
	public long t_start;
	public long t_end;
	// private JProgressBar progressBar;
	private Container panel;
	private JTextArea textArea;
	private int process;
	public Monitor(JComponent panel) {
		this.panel = panel;
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setColumns(10);
		panel.add(textArea);
		// progressBar = new JProgressBar();
		// progressBar.setIndeterminate(true);
		// progressBar.setMaximum(100);
		// progressBar.setIndeterminate(false);
		// progressBar.setVisible(false);
		// panel.add(progressBar, "4, 12");

	}

	private void appendMessage(String msg) {
		textArea.setText(textArea.getText() + "\n" + msg);
		textArea.selectAll();
		textArea.setCaretPosition(textArea.getSelectedText().length());
		textArea.requestFocus();
	}

	public void propt(String msg) {
		// progressBar.setString(msg);
		appendMessage(msg);
	}

	public void process(int process) {
		if(process<1)
			process = 0;
		else if(process>100)
			process = 100;
		// progressBar.setValue(process);
	}

	@Override
	public void propt(int process, String msg) {
		process(process);
		propt("(" + process + "%) " + msg);
	}

	public void finish() {
		t_end = System.currentTimeMillis();
		propt("完成......\n\r耗时：" + (t_end - t_start) / 1000 + "秒。");
		process(100);
		panel.doLayout();
	}

	public void start() {
		t_start = System.currentTimeMillis();
		textArea.setText("");
		propt("开始......");
		process(0);
		// progressBar.setVisible(true);
		panel.doLayout();
	}

	@Override
	public void error(String error) {
		appendMessage(error);
	}



}
