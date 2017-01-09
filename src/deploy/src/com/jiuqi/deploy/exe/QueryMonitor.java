package com.jiuqi.deploy.exe;

import java.awt.Color;

import com.jiuqi.deploy.util.IMonitor;
import com.jiuqi.deploy.util.ShowMessage;

public class QueryMonitor implements IMonitor {
	private ShowMessage showMessage;

	public QueryMonitor(ShowMessage showMessage) {
		this.showMessage = showMessage;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void process(int process) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propt(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propt(int process, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(String error) {
		showErrorStatus(error);
	}

	@Override
	public void finish() {

	}

	private void showErrorStatus(String text) {
		showMessage.wshow(text, Color.RED);
	}

	private void showOkStatus(String text) {
		showMessage.wshow(text, Color.DARK_GRAY);
	}

}
