package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLSyntaxErrorException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.jsqltool.gui.graphics.DateCellEditor;
import org.jsqltool.gui.graphics.DateCellRenderer;
import org.jsqltool.gui.panel.Table;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.utils.Options;

import com.jiuqi.deploy.db.DBTools;
import com.jiuqi.deploy.db.ESBDBClient;

public class QueryDataPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private TableModelListener tableModelListener;
	private ESBDBClient dbClient;
	private Table table;
	private JScrollPane scrollPane;
	private String querySQL = null;
	private Vector parameters = new Vector();
	/**
	 * table model column index, related to the current sorted column; -1 = no
	 * sorted column
	 */
	private int sortColIndex = -1;
	private String originalQuery = null;

	/** number of records per block */
	private int BLOCK_SIZE = 100;

	/** current first row in block */
	private int inc = 0;

	/**
	 * Create the panel.
	 */
	public QueryDataPanel(ESBDBClient dbClient, TableModelListener tableModelListener) {
		this.dbClient = dbClient;
		this.tableModelListener = tableModelListener;
		jbInit();
		init();
	}

	private void init() {
		MouseAdapter listMouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					if (querySQL != null) {
					}
				} catch (Exception ex) {
				}
			}
		};
		JTableHeader th = table.getTableHeader();
		th.addMouseListener(listMouseListener);

		// key listener used to scroll table...
		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
			}
		});
	}

	private void jbInit() {
		this.setLayout(new BorderLayout());
		table = new Table();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().add(table, null);
	}

	public TableModel getTableModel() {
		return table.getModel();
	}

	public Table getTable() {
		return table;
	}

	public void setQuery(String query, Vector parameters) throws SQLSyntaxErrorException {
		this.querySQL = query;
		this.parameters = parameters;
		this.sortColIndex = -1;
		this.originalQuery = null;
		setQuery();
	}

	public void setQuery() throws SQLSyntaxErrorException {
		this.inc = 0;
		readBlock();
	}

	private void readBlock() throws SQLSyntaxErrorException {
		if (querySQL == null)
			return;
		updateSortIcons();
		table.getModel().removeTableModelListener(tableModelListener);
		TableModel tableModel = DBTools.queryData(dbClient, querySQL, parameters);
		if (null != tableModel) {
			table.setModel(tableModel);
			try {
				for (int i = 0; i < table.getColumnCount(); i++) {
					if (((CustomTableModel) table.getModel()).getColumnClass(table.convertColumnIndexToModel(i)).equals(java.util.Date.class)
							|| ((CustomTableModel) table.getModel()).getColumnClass(table.convertColumnIndexToModel(i)).equals(java.sql.Date.class)
							|| ((CustomTableModel) table.getModel()).getColumnClass(table.convertColumnIndexToModel(i)).equals(java.sql.Timestamp.class)) {
						table.getColumnModel().getColumn(i).setCellEditor(new DateCellEditor());
						table.getColumnModel().getColumn(i).setCellRenderer(new DateCellRenderer());
						table.getColumnModel().getColumn(i).setPreferredWidth(6 * Options.getInstance().getDateFormat().length());
					} else
						table.getColumnModel().getColumn(i).setPreferredWidth(((CustomTableModel) table.getModel()).getColSizes()[table.convertColumnIndexToModel(i)]);
				}
				if (table.getRowCount() > 0)
					table.setRowSelectionInterval(0, 0);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			table.getModel().addTableModelListener(tableModelListener);
		}
	}

	private void updateSortIcons() {
		// TODO Auto-generated method stub

	}

}
