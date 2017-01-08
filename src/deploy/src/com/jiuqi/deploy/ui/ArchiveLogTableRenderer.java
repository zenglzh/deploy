package com.jiuqi.deploy.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.jiuqi.deploy.server.ArchiveLogEntry;

public class ArchiveLogTableRenderer implements TableCellRenderer {

	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

	final static private double LAVEL1 = 50;
	final static private double LAVEL2 = 80;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
		Color foreground = Color.BLACK, background = Color.WHITE;
		Object valueAt3col = table.getValueAt(row, 4);
		if("Î´Á¬½Ó".equals(valueAt3col)){
			background = Color.LIGHT_GRAY;
		}else if (column == 7 || column == 10) {
			if (null!=value && !value.toString().contains("-")) {
				Number number = null;
				try {
					number = ArchiveLogEntry.df.parse(String.valueOf(value));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (number != null) {
					double doubleValue = number.doubleValue();
					if (doubleValue < LAVEL1) {
						foreground = Color.DARK_GRAY;
						background = Color.GREEN;
					} else if (LAVEL1 <= doubleValue && doubleValue < LAVEL2) {
						foreground = Color.DARK_GRAY;
						background = Color.YELLOW;
					} else if (LAVEL2 <= doubleValue) {
						foreground = Color.WHITE;
						background = Color.RED;
					}
				}
			}
		}
		renderer.setForeground(foreground);
		renderer.setBackground(background);
		return renderer;
	}
}
