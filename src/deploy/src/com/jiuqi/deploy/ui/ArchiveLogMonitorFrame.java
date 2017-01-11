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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import org.jsqltool.utils.Options;

import com.jiuqi.deploy.db.ArchiveMonitorDBInfo;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.exe.DBConnectTable;
import com.jiuqi.deploy.exe.DBInfoConsumerQueue;
import com.jiuqi.deploy.exe.DBInfoProducerQueue;
import com.jiuqi.deploy.exe.QueryMonitor;
import com.jiuqi.deploy.server.ArchiveLogEntry;
import com.jiuqi.deploy.server.HistorySQLManage;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.IMonitor;
import com.jiuqi.deploy.util.ShowMessage;
import com.jiuqi.deploy.util.StringHelper;

/**
 * @author: zenglizhi
 * @time: 2016��12��23��
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
	private int refreshTime = 0;// ˢ�´���
	private JLabel l_refreshTime;
	private boolean isStarted = false;
	private final static String[] Start_Title = { "����", "ֹͣ" };
	private final static String[] Full_Title = { "ȫ��", "�˳�ȫ��" };
	private File homePath;
	private List<ArchiveMonitorDBInfo> monitorDBInfos;
	private Timer timer;
	private ShowMessage showMessage;
	private JButton btnSql;
	private JButton btnSql_all;
	private JSeparator separator;
	private HistorySQLManage sqlmeme;

	private void setStartBtnTitle() {
		int idt = isStarted ? 1 : 0;
		btn_start.setText(Start_Title[idt]);
	}

	private void updateRefreshLabel() {
		l_refreshTime.setText(String.format("��ˢ�� %d ��", refreshTime));
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
		init();
		initialize();
	}

	/**
	 * Load from file system the application profile file
	 * (profile/jsqltool.ini). If profile file doesn't exists, then create a new
	 * one.
	 */
	private void init() {
		try {
			Properties p = new Properties();
			File profileFile = new File(homePath, "jsqltool.ini");
			if (!profileFile.exists()) {
				p.setProperty("DATE_FORMAT", "dd-MM-yyyy hh:mm:ss");
				p.setProperty("ORACLE_EXPLAIN_PLAN_TABLE", "TOAD_PLAN_TABLE");
				p.setProperty("UPDATE_WHEN_NO_PK", "true");
				p.setProperty("LANGUAGE", Locale.getDefault().getLanguage());
				p.storeToXML(new FileOutputStream(profileFile), "JSQLTOOL Properties");
			} else {
				p.loadFromXML(new FileInputStream(profileFile));
			}
			// set onto Options singleton the values retrieved from profile.
			Options.getInstance().setDateFormat(p.getProperty("DATE_FORMAT", "dd-MM-yyyy hh:mm:ss"));
			Options.getInstance().setOracleExplainPlanTable(p.getProperty("ORACLE_EXPLAIN_PLAN_TABLE", "TOAD_PLAN_TABLE"));
			Options.getInstance().setUpdateWhenNoPK(p.getProperty("UPDATE_WHEN_NO_PK", "true").equals("true"));
			Options.getInstance().setLanguage(p.getProperty("LANGUAGE", Locale.getDefault().getLanguage()));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this.frame, Options.getInstance().getResource("error while loading jsqltool.ini") + ":\n" + ex.getMessage(), Options.getInstance().getResource("error"),
					JOptionPane.ERROR_MESSAGE);
		}
		sqlmeme = new HistorySQLManage();
		sqlmeme.load(homePath.getAbsolutePath());
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
		l_status.setFont(new Font("����", Font.PLAIN, 12));
		panel.add(l_status);
		showMessage = new ShowMessage(l_status);
		monitor = new QueryMonitor(showMessage);
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

		JTable table = new JTable();
		this.tableWrapper = new ArchiveLogTableWrapper(table);

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
		jfc.showDialog(new JLabel(), "ѡ��");
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
		File[] listFiles = homePath.listFiles(new java.io.FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".xlsx") || f.getName().endsWith(".xls");
			}
		});
		if (null == listFiles || listFiles.length < 1) {
			showErrorStatus("���ȵ������ݿ������ļ���");
			return;
		}
		showOkStatus("���ڲ�������...");
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
			showErrorStatus("����ģ����û��ʡ�����ݿ�������Ϣ��");
			return;
		}
		if (invalidCodes.length() > 0) {
			showErrorStatus("��Щʡ���ݿⲻ���������ӣ�" + invalidCodes);
		} else {
			showOkStatus("OK��");
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

	private IMonitor monitor;

	private void refresh() {
		if (null == monitorDBInfos) {
			connecte();
		}
		DBConnectTable connectTable = new DBConnectTable();
		for (ArchiveMonitorDBInfo dbInfo : monitorDBInfos) {
			DBInfoProducerQueue thread = new DBInfoProducerQueue(connectTable, dbInfo, monitor);
			thread.start();
		}
		connectTable.buildMode();
		Thread thread = new Thread(new DBInfoConsumerQueue(connectTable, tableWrapper));
		thread.start();

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
		if (null != select && select.isConnected()) {
			SQLEditorFrame frame = new SQLEditorFrame(select.getDBClient());
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setMinimumSize(new Dimension(800, 600));
			frame.setVisible(true);
			frame.setTitle("SQL Editor - " + select);
		} else {
			showOkStatus("���ȴ��б���ѡ��һ�����ӡ�");
		}
	}

	/**
	 * ������Դִ�д���
	 */
	protected void showSqlAllWindow() {
		if (null == monitorDBInfos) {
			connecte();
		}
		final SQLAllFrame frame = new SQLAllFrame(monitorDBInfos, sqlmeme);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.setTitle("SQL ȫ��");
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				String lastSql = frame.getLastSQL();
				if (!StringHelper.isEmpty(lastSql)) {
					sqlmeme.push(lastSql);
					sqlmeme.store(homePath.getAbsolutePath());
				}
			}
		});
	}

	private void screenControl() {

	}
}
