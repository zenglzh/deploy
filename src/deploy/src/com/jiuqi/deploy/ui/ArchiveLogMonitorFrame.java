/*
 * @(#)LogMonitor.java  
 */
package com.jiuqi.deploy.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;

import com.jiuqi.deploy.db.ArchiveMonitorDBInfo;
import com.jiuqi.deploy.db.Constant;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.db.JDBCUtil;
import com.jiuqi.deploy.exe.ConnectPipe;
import com.jiuqi.deploy.exe.DBArchiveThread;
import com.jiuqi.deploy.server.ArchiveLogEntry;
import com.jiuqi.deploy.server.ArchiveLogEntry.CONNECT;
import com.jiuqi.deploy.util.ConnectionInfo;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.ShowMessage;
import com.jiuqi.deploy.util.StringHelper;

/**
 * @author: zenglizhi
 * @time: 2016年12月23日
 */
public class ArchiveLogMonitorFrame {
	private JFrame frame;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField tf_interval;
	private ArchiveLogTableWrapper tableWrapper;
	private JButton btn_import;
	private JButton btn_conn;
	private JRadioButton rb_man;
	private JRadioButton rb_auto;
	private JButton btn_start;
	private JButton btn_refresh;
	private int refreshTime = 0;// 刷新次数
	private JLabel l_refreshTime;
	private boolean isStarted = false;
	private final static String[] Start_Title = { "启动", "停止" };
	private File homePath;
	private List<ArchiveMonitorDBInfo> monitorDBInfos;
	private Timer timer;
	private ShowMessage showMessage;
	private JButton btnSql;
	private JButton btnSql_all;
	private JSeparator separator;

	private void setStartBtnTitle() {
		int idt = isStarted ? 1 : 0;
		btn_start.setText(Start_Title[idt]);
	}

	private void updateRefreshLabel() {
		l_refreshTime.setText(String.format("已刷新 %d 次", refreshTime));
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		_launch();
	}

	public static void launch() {
		_launch();
	}

	private static void _launch() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ArchiveLogMonitorFrame window = new ArchiveLogMonitorFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ArchiveLogMonitorFrame() {
		String userHome = System.getProperty("user.home");
		this.homePath = new File(userHome + File.separator + ".deploy");
		if (!homePath.exists()) {
			homePath.mkdirs();
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u6570\u636E\u5E93\u76D1\u63A7\u7A0B\u5E8F");
		frame.setBounds(100, 100, 1202, 740);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 30, 30, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);

		JToolBar toolBar = new JToolBar();
		panel.add(toolBar, "1, 1, 8, 1");
		toolBar.setFloatable(false);

		btn_import = new JButton("\u5BFC\u5165\u6570\u636E\u5E93\u6A21\u677F");
		btn_import.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileChoose();
			}
		});
		toolBar.add(btn_import);

		btn_conn = new JButton("\u6D4B\u8BD5\u8FDE\u63A5");
		btn_conn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connecte();
			}
		});
		toolBar.add(btn_conn);

		btnSql = new JButton("SQL");
		btnSql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSqlWindow();
			}

		});

		separator = new JSeparator();
		toolBar.add(separator);
		toolBar.add(btnSql);

		btnSql_all = new JButton("SQL \u5168\u5E93");
		btnSql_all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSqlAllWindow();
			}
		});
		toolBar.add(btnSql_all);

		JLabel l_status = new JLabel("");
		l_status.setFont(new Font("宋体", Font.PLAIN, 12));
		panel.add(l_status);
		showMessage = new ShowMessage(l_status);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		frame.getContentPane().add(panel_1, gbc_panel_1);

		rb_man = new JRadioButton("\u624B\u52A8");
		rb_man.setSelected(true);
		buttonGroup.add(rb_man);
		panel_1.add(rb_man);

		rb_auto = new JRadioButton("\u81EA\u52A8");
		buttonGroup.add(rb_auto);
		panel_1.add(rb_auto);
		rb_man.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btn_start.setEnabled(false);
				btn_refresh.setEnabled(true);
			}
		});
		rb_auto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btn_start.setEnabled(true);
				btn_refresh.setEnabled(false);
			}
		});

		JLabel label_1 = new JLabel("\u6BCF");
		panel_1.add(label_1);

		tf_interval = new JTextField();
		tf_interval.setHorizontalAlignment(SwingConstants.RIGHT);
		tf_interval.setText("5");
		panel_1.add(tf_interval);
		tf_interval.setColumns(5);
		tf_interval.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				int keyChar = e.getKeyChar();
				if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {

				} else {
					e.consume();
				}
			}
		});

		JLabel label_2 = new JLabel("\u79D2\u5237\u65B0\u4E00\u6B21");
		panel_1.add(label_2);

		btn_start = new JButton();
		btn_start.setEnabled(false);
		setStartBtnTitle();
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		panel_1.add(btn_start);

		btn_refresh = new JButton("\u5237\u65B0");
		btn_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		panel_1.add(btn_refresh);

		l_refreshTime = new JLabel("");
		l_refreshTime.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(l_refreshTime);

		this.tableWrapper = new ArchiveLogTableWrapper();
		JTable table = tableWrapper.getTable();

		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);

	}

	private void fileChoose() {
		final JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "*.xls ;  *.xlsx";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return f.getName().endsWith(".xlsx") || f.getName().endsWith(".xls");
			}
		});
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
					File[] list = homePath.listFiles();
					for (File f : list) {
						FileUtils.deleteQuietly(f);
					}
					File file = jfc.getSelectedFile();
					try {
						FileUtils.copyFileToDirectory(file, homePath);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		jfc.showDialog(new JLabel(), "选择");
	}

	interface Filter {
		public boolean accept(ArchiveMonitorDBInfo dbinfo);
	}

	private List<ArchiveMonitorDBInfo> parseFile(File file, Filter filter) {
		List<ArchiveMonitorDBInfo> infoList = new ArrayList<ArchiveMonitorDBInfo>();
		jxl.Workbook rwb = null;
		try {
			rwb = jxl.Workbook.getWorkbook(file);
			jxl.Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();
			for (int i = 1; i < rsRows; i++) {
				String provinceCode = rs.getCell(0, i).getContents().trim();
				String dataSourceName = rs.getCell(1, i).getContents().trim();
				String isUseCluster = rs.getCell(2, i).getContents().trim();
				String url = rs.getCell(3, i).getContents().trim();
				String username = rs.getCell(4, i).getContents().trim();
				String pwd = rs.getCell(5, i).getContents().trim();
				String dbsize = rs.getCell(6, i).getContents().trim();
				String alogsize = rs.getCell(7, i).getContents().trim();

				ArchiveMonitorDBInfo info = new ArchiveMonitorDBInfo();
				info.setDataSourceName(dataSourceName.trim());
				info.setIsUseCluster(isUseCluster);
				info.setUrl(url);
				info.setUserName(username);
				info.setPassword(pwd);
				info.setProvinceCode(provinceCode);
				if (!StringHelper.isEmpty(dbsize))
					info.setDbInitSize(Double.valueOf(dbsize));
				if (!StringHelper.isEmpty(alogsize))
					info.setArchiveInitSize(Double.valueOf(alogsize));
				if (filter.accept(info)) {
					infoList.add(info);
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (rwb != null) {
				rwb.close();
			}
		}
		return infoList;
	}

	private void connecte() {
		File[] listFiles = homePath.listFiles();
		if (null == listFiles || listFiles.length < 1) {
			showErrorStatus("请先导入数据库连接文件！");
			return;
		}
		showOkStatus("正在测试连接...");
		final StringBuilder invalidCodes = new StringBuilder();
		monitorDBInfos = parseFile(listFiles[0], new Filter() {

			@Override
			public boolean accept(ArchiveMonitorDBInfo dbinfo) {
				boolean isEmpty = StringHelper.isEmpty(dbinfo.getProvinceCode().trim());
				if (!isEmpty) {
					/*
					 * DatabaseConnectionInfo connect = getConnect(dbinfo); if
					 * (!isDBValid(connect)) {
					 * invalidCodes.append(dbinfo.getProvinceCode
					 * ()).append(","); dbinfo.setValid(false); } else {
					 * dbinfo.setValid(true); }
					 */
				}
				return !isEmpty;
			}
		});
		if (monitorDBInfos.isEmpty()) {
			showErrorStatus("导入模板中没有省的数据库连接信息。");
			return;
		}
		if (invalidCodes.length() > 0) {
			showErrorStatus("这些省数据库不能正常连接：" + invalidCodes);
		} else {
			showOkStatus("OK！");
		}

	}

	private void showErrorStatus(String text) {
		showMessage.wshow(text, Color.RED);
	}

	private void showOkStatus(String text) {
		showMessage.wshow(text, Color.DARK_GRAY);
	}

	private void execute() {
		if (isStarted) {
			isStarted = false;
			stop();
		} else {
			isStarted = true;
			start();
		}
		setStartBtnTitle();

	}

	private void start() {
		String interval = tf_interval.getText();
		if (StringHelper.isEmpty(interval)) {
			return;
		}
		refreshTime = 0;
		btn_import.setEnabled(false);
		btn_conn.setEnabled(false);
		rb_auto.setEnabled(false);
		rb_man.setEnabled(false);
		tf_interval.setEditable(false);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				refreshTime++;
				if (refreshTime > Integer.MAX_VALUE - 10) {
					stop();
				}
				updateRefreshLabel();
				refresh();
			}
		}, 1000, Integer.valueOf(interval) * 1000);

	}

	private void stop() {
		btn_import.setEnabled(true);
		btn_conn.setEnabled(true);
		tf_interval.setEditable(true);
		rb_auto.setEnabled(true);
		rb_man.setEnabled(true);
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
	}

	private void refresh() {
		if (null == monitorDBInfos) {
			connecte();
		}
		tableWrapper.removeAll();
		List<ArchiveLogEntry> entryList = query();
		if (null != entryList) {
			for (ArchiveLogEntry entry : entryList) {
				tableWrapper.addRow(entry);
			}
			tableWrapper.getTable().repaint();
		}
	}

	private void refresh2() {

	}

	private List<ArchiveLogEntry> query3() {
		List<ArchiveLogEntry> logentrylist = new ArrayList<ArchiveLogEntry>();
		for (int i = 0; i < monitorDBInfos.size(); i++) {
			ArchiveMonitorDBInfo dbInfo = monitorDBInfos.get(i);
			ArchiveLogEntry entry = new ArchiveLogEntry();
			entry.setPcode(dbInfo.getProvinceCode());
			entry.setUrl(dbInfo.getUrl());
			entry.setDataSource(dbInfo.getDataSourceName());
			entry.setDbInitSize(dbInfo.getDbInitSize());
			entry.setArchiveInitSize(dbInfo.getArchiveInitSize());
			entry.setUser(dbInfo.getUserName());
			entry.setPwd(dbInfo.getPassword());
			entry.setLogon(DatabaseConnectionInfo.CONNECTID[0]);
			ConnectPipe pipe = new ConnectPipe(i, entry, tableWrapper);
			DBArchiveThread thread = new DBArchiveThread(pipe);
			Thread ct = new Thread(thread);
			ct.start();
			logentrylist.add(entry);
		}
		return logentrylist;
	}

	private List<ArchiveLogEntry> query() {
		List<ArchiveLogEntry> logentrylist = new ArrayList<ArchiveLogEntry>();
		for (ArchiveMonitorDBInfo dbInfo : monitorDBInfos) {
			ArchiveLogEntry entry = new ArchiveLogEntry();
			entry.setPcode(dbInfo.getProvinceCode());
			entry.setUrl(dbInfo.getUrl());
			entry.setDataSource(dbInfo.getDataSourceName());
			entry.setDbInitSize(dbInfo.getDbInitSize());
			entry.setArchiveInitSize(dbInfo.getArchiveInitSize());
			DatabaseConnectionInfo databaseConnectionInfo = dbInfo.getConnect();
			Connection conn = null;
			try {
				ESBDBClient dbClient = new ESBDBClient(databaseConnectionInfo);
				dbClient.connect();
				conn = dbClient.getConn();
				double archiveSize = JDBCUtil.getDbOneFieldSize(conn, Constant.sql_archive);
				entry.setConnected(CONNECT.CONNECTED);
				entry.setArchiveSize(archiveSize);
				entry.setTableSpaceSize(JDBCUtil.getDbOneFieldSize(conn, Constant.sql_tableSpace));
			} catch (Exception e) {
				entry.setConnected(CONNECT.UNCONNECTED);
				showErrorStatus(e.getMessage());
				System.out.println(e.getMessage());
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			logentrylist.add(entry);
		}
		return logentrylist;
	}

	private DatabaseConnectionInfo getConnect(ArchiveMonitorDBInfo dbInfo) {
		ConnectionInfo connectionInfo = new ConnectionInfo();
		DatabaseConnectionInfo databaseConnectionInfo = connectionInfo.getDatabaseConnectionInfo();
		if (databaseConnectionInfo == null) {
			databaseConnectionInfo = new DatabaseConnectionInfo();
			connectionInfo.setDatabaseConnectionInfo(databaseConnectionInfo);
		}
		databaseConnectionInfo.setUrl(dbInfo.getUrl());
		databaseConnectionInfo.setUsername(dbInfo.getUserName());
		databaseConnectionInfo.setPassword(dbInfo.getPassword());
		databaseConnectionInfo.setConnectID(DatabaseConnectionInfo.CONNECTID[0]);
		connectionInfo.setName(databaseConnectionInfo.toString());// name
		return databaseConnectionInfo;
	}

	public boolean isDBValid(DatabaseConnectionInfo connectionInfo) {
		if (null != connectionInfo) {
			ESBDBClient client = new ESBDBClient(connectionInfo);
			return client.isValidConnection();
		}
		return false;
	}

	private void showSqlWindow() {
		ArchiveLogEntry select = tableWrapper.getSelect();
		if (null != select) {
			SQLEditorFrame frame = new SQLEditorFrame(select.getDBConnection());
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setVisible(true);
			frame.setTitle("SQL Editor");
		}
	}

	/**
	 * 多数据源执行窗口
	 */
	protected void showSqlAllWindow() {
		if (null == monitorDBInfos) {
			connecte();
		}
		SQLAllFrame frame = new SQLAllFrame(monitorDBInfos);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.setTitle("SQL 全库");
	}
}
