package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.utils.Options;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deploy.db.ArchiveMonitorDBInfo;
import com.jiuqi.deploy.exe.QueryMonitor;
import com.jiuqi.deploy.intf.ISelect;
import com.jiuqi.deploy.server.HistorySQLManage;
import com.jiuqi.deploy.util.IMonitor;
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.ShowMessage;
import com.jiuqi.deploy.util.StringHelper;

public class SQLAllFrame extends JFrame implements TableModelListener {

	private static final long serialVersionUID = -8179569551237648141L;
	private final JPanel mainPanel = new JPanel();
	private RSyntaxTextArea editor;
	private ShowMessage showMessage;
	private AllQueryDataPanel dataPanel;
	private List<ArchiveMonitorDBInfo> monitorDBInfos;
	private String lastSQL;
	private JLabel statusLabel;
	private IMonitor monitor;
	private JTextField t_time;
	private JCheckBox chk_auto;
	private JButton btn_hisotry;
	private JButton btn_execute;
	private HistorySQLManage sqlmeme;

	/**
	 * Create the dialog.
	 * 
	 * @param sqlmeme
	 */
	public SQLAllFrame(List<ArchiveMonitorDBInfo> monitorDBInfos, HistorySQLManage sqlmeme) {
		this.monitorDBInfos = monitorDBInfos;
		this.sqlmeme = sqlmeme;
		init();
		initData();
	}

	private void init() {
		getContentPane().setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { RowSpec.decode("18dlu"),
				FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("max(204dlu;default):grow"), RowSpec.decode("20dlu"), RowSpec.decode("max(2dlu;default)"), }));

		JPanel panel = new JPanel();
		mainPanel.add(panel, "2, 1, fill, fill");
		btn_hisotry = new JButton("");
		btn_hisotry.setIcon(ImageRes.getIcon(ImageRes.PNG_HISTORY));
		btn_hisotry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHitoryDialog();
			}
		});
		panel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("52px"), FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("40px"), ColumnSpec.decode("1dlu"),
				ColumnSpec.decode("24px"), FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("18px"), FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("51px"),
				ColumnSpec.decode("1dlu"), ColumnSpec.decode("49px"), FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("1px"), }, new RowSpec[] { RowSpec.decode("27px"), }));

		chk_auto = new JCheckBox("\u81EA\u52A8");
		panel.add(chk_auto, "1, 1, left, center");

		JLabel lblNewLabel = new JLabel("\u5468\u671F\uFF1A");
		panel.add(lblNewLabel, "3, 1, left, center");

		btn_execute = new JButton("");
		btn_execute.setIcon(ImageRes.getIcon(ImageRes.PNG_EXECUTE));
		btn_execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});

		t_time = new JTextField();
		t_time.setText("10");
		panel.add(t_time, "5, 1, left, center");
		t_time.setColumns(3);

		t_time.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				int keyChar = e.getKeyChar();
				if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {
				} else {
					e.consume();
				}
			}
		});

		JLabel lblNewLabel_1 = new JLabel("\u79D2 ");
		panel.add(lblNewLabel_1, "7, 1, left, center");
		panel.add(btn_execute, "9, 1, fill, fill");
		panel.add(btn_hisotry, "11, 1, fill, fill");

		JLabel lbl_status = new JLabel("");
		showMessage = new ShowMessage(lbl_status);
		panel.add(lbl_status, "13, 1, left, center");
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		editor = new RSyntaxTextArea(20, 60);
		editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		editor.setCodeFoldingEnabled(true);
		splitPane.add(editor, JSplitPane.TOP);
		monitor = new QueryMonitor(showMessage);
		this.dataPanel = new AllQueryDataPanel(monitorDBInfos, this, monitor);// this
		splitPane.add(dataPanel, JSplitPane.BOTTOM);
		splitPane.setDividerLocation(150);

		mainPanel.add(splitPane, "2, 3, fill, fill");

		JPanel statePanel = new JPanel();
		statePanel.setBorder(BorderFactory.createEtchedBorder());
		statePanel.setMinimumSize(new Dimension(14, 24));
		statePanel.setPreferredSize(new Dimension(14, 24));
		statePanel.setLayout(new BorderLayout());
		mainPanel.add(statePanel, "2, 4, 1, 2, fill, fill");
		statePanel.setLayout(new BorderLayout(0, 0));
		statusLabel = new JLabel("");
		statePanel.add(statusLabel);
	}

	private void initData() {
		if (!sqlmeme.isEmpty()) {
			editor.setText(sqlmeme.last());
		}
	}

	public String getLastSQL() {
		return lastSQL;
	}

	@Override
	public void tableChanged(TableModelEvent e) {

	}

	private void showErrorStatus(String text) {
		showMessage.wshow(text, Color.RED);
	}

	private void showOkStatus(String text) {
		showMessage.show(text, Color.DARK_GRAY);
	}

	private void showHitoryDialog() {
		HistorySQLDialog dialog = new HistorySQLDialog(new ISelect<String>() {

			@Override
			public void select(String sql) {
				editor.setText(sql);
			}

		}, sqlmeme);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("历史SQL");
		dialog.setModal(true);
		dialog.setLocationRelativeTo(SQLAllFrame.this);
		dialog.setVisible(true);
	}

	private void execute() {
		if (null != editor.getText() && !StringHelper.isEmpty(editor.getText().trim())) {
			if (chk_auto.isSelected()) {
				autoExecute();
			} else {
				manExecute();
			}
		}
	}

	private boolean isStarted = false;
	private Timer timer;
	private int refreshTime = 0;// 刷新次数

	private void start() {
		String interval = t_time.getText();
		if (StringHelper.isEmpty(interval)) {
			return;
		}
		refreshTime = 0;
		chk_auto.setEnabled(false);
		t_time.setEnabled(false);
		btn_hisotry.setEnabled(false);
		btn_execute.setIcon(ImageRes.getIcon(ImageRes.PNG_STOP));
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				refreshTime++;
				if (refreshTime > Integer.MAX_VALUE - 10) {
					stop();
					showOkStatus(String.format("再也跑不动了。"));
				} else {
					showOkStatus(String.format("已执行 %d 次.", refreshTime));
					manExecute();
				}
			}
		}, 1000, Integer.valueOf(interval) * 1000);
	}

	private void stop() {
		chk_auto.setEnabled(true);
		t_time.setEnabled(true);
		btn_hisotry.setEnabled(true);
		btn_execute.setIcon(ImageRes.getIcon(ImageRes.PNG_EXECUTE));
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
	}

	private void autoExecute() {
		if (isStarted) {
			isStarted = false;
			stop();
		} else {
			isStarted = true;
			start();
		}
		setStartBtnTitle();
	}

	private void setStartBtnTitle() {

	}

	private void manExecute() {
		if (editor.getSelectedText() != null)
			executeSQL(editor.getSelectedText());
		else if (editor.getText().trim().length() > 0)
			executeSQL(editor.getText());
	}

	private void executeSQL(String sql) {
		String newsql = parseRows(sql);
		// parse sql to find out parameters...
		executeSQLWithValues(newsql, new Vector<Object>());
	}

	public final void executeSQLWithValues(String newsql, Vector values) {
		if (newsql.trim().toUpperCase().startsWith("SELECT ")) {
			long time = System.currentTimeMillis();
			dataPanel.query(newsql, values);
			dataPanel.getTable().setShowGrid(true);
			if (dataPanel.getTableModel() instanceof CustomTableModel) {
				((CustomTableModel) dataPanel.getTableModel()).setEditMode(CustomTableModel.DETAIL_REC);
			}
			statusLabel.setText(Options.getInstance().getResource("query execution") + ": " + (System.currentTimeMillis() - time) + " ms");
		} else {
			showErrorStatus("只能执行 SELECT 语句！");
			// statusLabel.setText(dbConnUtil.executeStmt(newsql, values) + " "
			// + Options.getInstance().getResource("records processed."));
		}

	}

	private String parseRows(String sql) {
		lastSQL = sql;
		String temp = "";
		String newsql = "";
		StringTokenizer st = new StringTokenizer(sql, "\n");
		boolean isComment = false;
		while (st.hasMoreTokens()) {
			temp = st.nextToken();
			if (temp.indexOf("*/") != -1) {
				temp = " " + temp.substring(temp.indexOf("*/") + 2);
				isComment = false;
			}
			if (temp.indexOf("--") != -1)
				temp = temp.substring(0, temp.indexOf("--"));
			if (temp.indexOf("/*") != -1) {
				newsql += " " + temp.substring(0, temp.indexOf("/*"));
				isComment = true;
			} else if (!isComment)
				newsql += " " + temp;
			if (temp.indexOf("*/") != -1) {
				newsql += " " + temp.substring(temp.indexOf("*/") + 2);
				isComment = false;
			}
		}
		newsql = newsql.trim();
		if (newsql.endsWith(";"))
			newsql = newsql.substring(0, newsql.length() - 1);
		return newsql;
	}

}
