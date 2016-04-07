package com.jiuqi.deplay.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class AddRemoveCells implements ActionListener {
	JTable table = null;
	DefaultTableModel defaultModel = null;

	public AddRemoveCells() {
		JFrame f = new JFrame();
		String[] name = { "�ֶ� 1", "�ֶ� 2", "�ֶ� 3", "�ֶ� 4", "�ֶ� 5" };
		String[][] data = new String[5][5];
		int value = 1;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length; j++)
				data[i][j] = String.valueOf(value++);
		}

		defaultModel = new DefaultTableModel(data, name);
		table = new JTable(defaultModel);
		table.setPreferredScrollableViewportSize(new Dimension(400, 80));
		JScrollPane s = new JScrollPane(table);

		JPanel panel = new JPanel();
		JButton b = new JButton("������");
		panel.add(b);
		b.addActionListener(this);
		b = new JButton("������");
		panel.add(b);
		b.addActionListener(this);
		b = new JButton("ɾ����");
		panel.add(b);
		b.addActionListener(this);
		b = new JButton("ɾ����");
		panel.add(b);
		b.addActionListener(this);

		Container contentPane = f.getContentPane();
		contentPane.add(panel, BorderLayout.NORTH);
		contentPane.add(s, BorderLayout.CENTER);

		f.setTitle("AddRemoveCells");
		f.pack();
		f.setVisible(true);

		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
}

	/*
	 * Ҫɾ���б���ʹ��TableColumnModel���涨���removeColumn()�����������������JTable���
	 * getColumnModel()����ȡ��TableColumnModel��������TableColumnModel��getColumn()����ȡ
	 * ��Ҫɾ���е�TableColumn.��TableColumn��������removeColumn()�Ĳ�����ɾ��������Ϻ������������������Ҳ����
	 * ʹ��DefaultTableModel��setColumnCount()���������á�
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("������"))
			defaultModel.addColumn("������");
		if (e.getActionCommand().equals("������")) {
			Vector<String> rowData = new Vector<String>();
			rowData.add("abc");
			rowData.add("ddd");
			rowData.add("dddd");
			defaultModel.addRow(rowData);
		}
		if (e.getActionCommand().equals("ɾ����")) {
			int columncount = defaultModel.getColumnCount() - 1;
			if (columncount >= 0) { // ��columncount<0�����Ѿ�û���κ����ˡ�
				TableColumnModel columnModel = table.getColumnModel();
				TableColumn tableColumn = columnModel.getColumn(columncount);
				columnModel.removeColumn(tableColumn);
				defaultModel.setColumnCount(columncount);
			}
		}
		if (e.getActionCommand().equals("ɾ����")) {
			int rowcount = defaultModel.getRowCount() - 1;// getRowCount����������rowcount<0�����Ѿ�û���κ����ˡ�
			if (rowcount >= 0) {
				defaultModel.removeRow(rowcount);
				defaultModel.setRowCount(rowcount);
				/*
				 * ɾ���бȽϼ򵥣�ֻҪ��DefaultTableModel��removeRow()�������ɡ�//ɾ������Ϻ����������������
				 * ��Ҳ����ʹ��DefaultTableModel��setRowCount()���������á�
				 */
			}
		}
		table.revalidate();
	}

	public static void main(String args[]) {
		new AddRemoveCells();
	}
}