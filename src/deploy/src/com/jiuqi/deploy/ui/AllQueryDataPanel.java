package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLSyntaxErrorException;
import java.util.List;
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

import com.jiuqi.deploy.db.ArchiveMonitorDBInfo;
import com.jiuqi.deploy.db.DBTools;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.exe.BlockTable;
import com.jiuqi.deploy.exe.QueryResultConsumerQueue;
import com.jiuqi.deploy.exe.QueryResultProducerQueue;
import com.jiuqi.deploy.server.HistorySQLManage;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.IMonitor;

public class AllQueryDataPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private TableModelListener tableModelListener;
	private List<ArchiveMonitorDBInfo> monitorDBInfos;
	private Table table;
	private JScrollPane scrollPane;
	private String query = null;
	private Vector<Object> parameters = new Vector<Object>();
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
	private IMonitor monitor;
	private HistorySQLManage sqlmeme;

	/**
	 * Create the panel.
	 * 
	 * @param sqlmeme
	 */
	public AllQueryDataPanel(List<ArchiveMonitorDBInfo> monitorDBInfos, TableModelListener tableModelListener, IMonitor monitor, HistorySQLManage sqlmeme) {
		this.monitorDBInfos = monitorDBInfos;
		this.tableModelListener = tableModelListener;
		this.sqlmeme = sqlmeme;
		this.monitor = monitor;
		jbInit();
		init();
	}

	private void init() {
		MouseAdapter listMouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					if (query != null) {
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
		table.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectRow();
				}
			}
		});
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().add(table, null);
	}

	protected void selectRow() {
		int selectedRow = table.getSelectedRow();
		ArchiveMonitorDBInfo select = monitorDBInfos.get(selectedRow);
		ESBDBClient connection = new ESBDBClient(select.getUrl(), select.getUserName(), select.getPassword(), DatabaseConnectionInfo.CONNECTID[0]);
		SQLEditorFrame frame = new SQLEditorFrame(connection, sqlmeme);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setVisible(true);
		frame.setTitle("SQL Editor - " + connection.getUrl());
		frame.execute(query);
	}

	public TableModel getTableModel() {
		return table.getModel();
	}

	public Table getTable() {
		return table;
	}

	public void query(String query, Vector<Object> parameters) {
		this.query = query;
		this.parameters = parameters;
		this.sortColIndex = -1;
		this.originalQuery = null;
		buildTable();
	}

	public void setQuery() throws SQLSyntaxErrorException {
		this.inc = 0;
		readBlock();
	}

	private void readBlock() throws SQLSyntaxErrorException {
		if (query == null)
			return;
		table.getModel().removeTableModelListener(tableModelListener);
		TableModel tableModel = DBTools.queryAll(monitorDBInfos, query, parameters);
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

	private void buildTable() {
		table.getModel().removeTableModelListener(tableModelListener);
		table.removeAll();
		BlockTable btable = new BlockTable();
		for (ArchiveMonitorDBInfo dbInfo : monitorDBInfos) {
			QueryResultProducerQueue pro = new QueryResultProducerQueue(btable, dbInfo, query, parameters, monitor);
			pro.start();
		}
		Thread con = new Thread(new QueryResultConsumerQueue(btable, table));
		con.start();

	}

}
