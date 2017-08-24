/*
 * @(#)ArchiveLogTableWrapper.java  
 */
package com.jiuqi.deploy.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jiuqi.deploy.server.ArchiveLogEntry;

/**
 * @author: zenglizhi
 * @time: 2016年12月27日
 */
public class ArchiveLogTableWrapper {

	private JTable table;
	private ArchiveLogTableModel tablemode;

	public ArchiveLogTableWrapper(JTable table) {
		this.table = table;
	}

	public void initUI(ArchiveLogTableModel tablemode) {
		this.tablemode = tablemode;
		table.setModel(tablemode);
		table.getTableHeader().setReorderingAllowed(true);
		table.getTableHeader().setResizingAllowed(true);
		table.setDefaultRenderer(Object.class, new ArchiveLogTableRenderer());
		table.setDefaultEditor(Object.class , new LogTableEditor());
		for (int i = 0; i < ArchiveLogTableModel.SIZE.length; i++) {
			table.getColumnModel().getColumn(i).setMaxWidth(ArchiveLogTableModel.SIZE[i] * 6);
			table.getColumnModel().getColumn(i).setPreferredWidth(ArchiveLogTableModel.SIZE[i] * 3);
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (row > -1 && col > -1) {
					Object value = table.getValueAt(row, col);
					if (null != value && !"".equals(value))
						table.setToolTipText(value.toString());// 悬浮显示单元格内容
					else
						table.setToolTipText(null);// 关闭提示
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
			}
		});
	}

	public JTable getTable() {
		return table;
	}

	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	public ArchiveLogEntry getSelect() {
		int row = getSelectedRow();
		if (row >= 0) {
			return get(row);
		}
		return null;
	}

	public ArchiveLogEntry get(int row) {
		return tablemode.get(row);
	}

	public void addRow(ArchiveLogEntry entity) {
		tablemode.addRow(entity);
		table.revalidate();
	}

	public void deleteRow(int rowcount) {
		tablemode.removeRow(rowcount);
		table.revalidate();
	}

	public void removeAll() {
		tablemode.removeAll();
		table.revalidate();
	}

	public List<ArchiveLogEntry> getAll() {
		return tablemode.getAll();
	}

	public void modifyRow(int row, ArchiveLogEntry entity) {
		tablemode.modify(row, entity);
		table.revalidate();
	}

	public boolean modify(int row, ArchiveLogEntry entity) {
		modifyRow(row, entity);
		return true;
	}

	public boolean add(ArchiveLogEntry entity) {
		addRow(entity);
		return true;
	}
	
	
	static class LogTableEditor extends DefaultCellEditor{

		private static final long serialVersionUID = 1L;

		public LogTableEditor() {
			super(new JTextField());
		}
		
		public Component  getTableCellEditorComponent(JTable table,Object value, boolean isSelected,int row, int column){
			JTextField editor= (JTextField)super.getTableCellEditorComponent(table,value,isSelected,row,column);
			if(null!=value){
				editor.setText(value.toString());
			}
			if (column == 7 || column == 10){ 
				editor.setHorizontalAlignment(SwingConstants.RIGHT);
				editor.setFont(new Font("Serif",Font.ITALIC,12));
			}
			return editor;
		}
	}
}
