package com.jiuqi.deplay.util;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

public class ShowMessage {
	private JLabel l_message;

	public ShowMessage(JLabel l_message) {
		this.l_message = l_message;
	}

	private void showAwhile(final String msg, int delay) {
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				l_message.setText(msg);
			}
		}, delay);
	}

	public void wshow(String msg) {
		wshow(msg, Color.DARK_GRAY);
	}
	public void wshow(String msg, Color color) {
		show(msg, color);
		int delay = 3000;
		showAwhile("", delay);
		showAwhile(msg, delay + 100);
		showAwhile("", delay + 200);
	}

	public void show(String msg, Color color) {
		l_message.setText(msg);
		l_message.setForeground(color);
	}



	private void showErrorStatus(String text) {
		l_message.setText(text);
		l_message.setToolTipText(text);
		l_message.setForeground(Color.RED);
	}

	private void showOkStatus(String text) {
		l_message.setText(text);
		l_message.setToolTipText(text);
		l_message.setForeground(Color.DARK_GRAY);
	}

}
