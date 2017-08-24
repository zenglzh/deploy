package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jsqltool.conn.DbConnection;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.utils.Options;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.intf.ISelect;
import com.jiuqi.deploy.server.HistorySQLManage;
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.ShowMessage;
import com.jiuqi.deploy.util.StringHelper;
import com.jiuqi.util.csv.CsvReader;
import com.jiuqi.util.type.GUID;

public class SQLEditorFrame extends JFrame implements TableModelListener {

	private static final long serialVersionUID = 1L;
	private final JPanel mainPanel = new JPanel();
	private RSyntaxTextArea editor;
	private ShowMessage showMessage;
	private QueryDataPanel dataPanel;
	private ESBDBClient dbconnect;
	private String lastSQL = null;
	private JLabel statusLabel;
	private HistorySQLManage sqlmeme;

	/**
	 * Create the dialog.
	 * 
	 * @param sqlmeme
	 */
	public SQLEditorFrame(ESBDBClient connection, HistorySQLManage sqlmeme) {
		this.dbconnect = connection;
		this.sqlmeme = sqlmeme;
		init();
		initData();
	}

	private void initData() {
		if (!sqlmeme.isEmpty()) {
			editor.setText(sqlmeme.last());
		}
	}

	private void init() {
		getContentPane().setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel
				.setLayout(new FormLayout(new ColumnSpec[] {
						FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), }, new RowSpec[] {
						RowSpec.decode("28px"),
						FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
						RowSpec.decode("max(204dlu;default):grow"),
						RowSpec.decode("20dlu"),
						RowSpec.decode("max(2dlu;default)"), }));

		JPanel panel = new JPanel();
		mainPanel.add(panel, "2, 1, fill, fill");

		JButton btn_execute = new JButton("");
		btn_execute.setIcon(ImageRes.getIcon(ImageRes.PNG_EXECUTE));
		btn_execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("center:50px"),
				ColumnSpec.decode("center:50px"), ColumnSpec.decode("50px"),
				FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				ColumnSpec.decode("284px:grow"), }, new RowSpec[] { RowSpec
				.decode("28px"), }));
		panel.add(btn_execute, "1, 1, fill, center");

		JButton btn_explain = new JButton("");
		btn_explain.setIcon(ImageRes.getIcon(ImageRes.PNG_EXPLAIN));
		btn_explain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				explain(e);
			}
		});
		panel.add(btn_explain, "2, 1, fill, center");

		JButton btn_history = new JButton("");
		btn_history.setIcon(ImageRes.getIcon(ImageRes.PNG_HISTORY));
		btn_history.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHitoryDialog();
			}
		});
		panel.add(btn_history, "3, 1, fill, fill");
		if (false) {// TEST
			snpDataRecover(panel);
		}
		JLabel lbl_status = new JLabel("");
		showMessage = new ShowMessage(lbl_status);
		panel.add(lbl_status, "6, 1, left, center");
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		editor = new RSyntaxTextArea(20, 60);
		editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		editor.setCodeFoldingEnabled(true);
		editor.setTemplatesEnabled(true);
		splitPane.add(editor, JSplitPane.TOP);
		this.dataPanel = new QueryDataPanel(dbconnect, this);// this
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
		statusLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		statePanel.add(statusLabel);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setVgap(3);
		flowLayout.setHgap(2);
		statePanel.add(panel_1, BorderLayout.WEST);

		JButton btn_next = new JButton("NEXT");
		btn_next.setEnabled(false);
		btn_next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		panel_1.add(btn_next);

		JButton btn_all = new JButton("All");
		btn_all.setEnabled(false);
		btn_all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_1.add(btn_all);
	}

	private void snpDataRecover(JPanel panel) {
		JButton btnData = new JButton("Data");
		btnData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					try {
						csvread();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					dataRevert();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnData, "5, 1");
	}

	private void showHitoryDialog() {
		HistorySQLDialog dialog = new HistorySQLDialog(new ISelect<String>() {

			@Override
			public void select(String sql) {
				editor.setText(sql);
			}

		}, sqlmeme);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("SQL...");
		dialog.setModal(true);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub

	}

	private void showErrorStatus(String text) {
		showMessage.wshow(text, Color.RED);
	}

	private void showOkStatus(String text) {
		showMessage.wshow(text, Color.DARK_GRAY);
	}

	public void execute(String sql) {
		if (!StringHelper.isEmpty(sql)) {
			editor.setText(sql);
			execute();
		}
	}

	private void execute() {
		if (editor.getSelectedText() != null)
			executeSQL(editor.getSelectedText());
		else if (editor.getText().trim().length() > 0)
			executeSQL(editor.getText());
	}

	private void executeSQL(String sql) {
		String newsql = parseRows(sql);
		// parse sql to find out parameters...
		Vector values = new Vector();
		executeSQLWithValues(newsql, values);
	}

	public final void executeSQLWithValues(String newsql, Vector values) {
		if (newsql.trim().toUpperCase().startsWith("SELECT ")) {
			long time = System.currentTimeMillis();
			try {
				dataPanel.setQuery(newsql, values);
			} catch (SQLSyntaxErrorException e) {
				showErrorStatus("SQL 语法错误: " + e.getMessage());
			}
			dataPanel.getTable().setShowGrid(true);
			if (dataPanel.getTableModel() instanceof CustomTableModel) {
				((CustomTableModel) dataPanel.getTableModel())
						.setEditMode(CustomTableModel.DETAIL_REC);
			}
			statusLabel.setText(Options.getInstance().getResource(
					"query execution")
					+ ": " + (System.currentTimeMillis() - time) + " ms");
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

	private void explain(ActionEvent e) {
		String newsql = parseRows(editor.getText());
		long time = System.currentTimeMillis();
		String id = "EP_" + String.valueOf(Math.random() * time);
		if (id.length() > 16)
			id = id.substring(0, 16);
		if (dbconnect.getType() == DbConnection.ORACLE_TYPE) {
			try {
				try {
					dbconnect.connect();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				Statement stmt = dbconnect.getConn().createStatement();
				stmt.execute("EXPLAIN PLAN SET STATEMENT_ID = '" + id
						+ "' INTO "
						+ Options.getInstance().getOracleExplainPlanTable()
						+ " FOR " + newsql);
				stmt.close();
				dataPanel
						.setQuery(
								"SELECT LPAD(' ',2*(LEVEL-1))||operation operation, options,"
										+ "object_name, position "
										+ "FROM "
										+ Options.getInstance()
												.getOracleExplainPlanTable()
										+ " "
										+ "START WITH id = 0 AND statement_id = '"
										+ id
										+ "'"
										+ "CONNECT BY PRIOR id = parent_id AND statement_id = '"
										+ id + "'", new Vector());
				dataPanel.getTable().setShowGrid(false);
				((CustomTableModel) dataPanel.getTableModel())
						.setEditMode(CustomTableModel.DETAIL_REC);
			} catch (SQLException ex) {
				showErrorStatus(ex.getMessage());
			}
		} else {
			JOptionPane.showMessageDialog(
					this,
					Options.getInstance().getResource(
							"feature not supported for this database type."),
					Options.getInstance().getResource("attention"),
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public String getLastSQL() {
		return lastSQL;
	}

	// //////////////////
	public void dataRevert() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		try {
			dbconnect.connect();
			conn = dbconnect.getConn();
			for (String region : regionMap.keySet()) {
				StringBuilder sql = new StringBuilder();
				sql.append("insert into SNP_DATAMSGLOG(RECID,MSG_NAME,MSG_TYPE,MSG_CONTENT,PCS_TYPE,PCS_RESULT,PCS_TIME) values(?,?,?,?,?,?,?) ");

				PreparedStatement prest = conn.prepareStatement(sql.toString());
				prest.setString(1, GUID.newGUID());
				String name = "3NP_603_1_";
				if (region.equals("6561")) {
					name += "65610000000000_20170706143542";
				} else {
					name += region;
					name += "000000000000_20170706143542";
				}
				prest.setString(2, name);

				prest.setInt(3, 1);
				String content = "{\"NAME\":\""
						+ name
						+ "\",\"XXFL\":\"1\",\"XXNR\":[{\"YSJB\":\"01\",\"BH\":\"603\",\"YSZT\":\"1\",\"RWDM\":\"3NP\",\"TJJGDM\":\"000000000000\",\"YSSJ\":\"20170706143434\",\"BGQ\":\"201601YY\",\"TJJGMC\":\"全国\",\"DCDXXTMLIST\":[\""
						+ regionMap.get(region)
						+ "\"],\"YSR\":\"孙腾蛟\",\"YSYJ\":\"2017-07-06 14:35:34 国家验收不通过，验收说明：有农、牧、渔或服务业经营，但没有填写农林牧渔各业经营支出，也没有填写经营收入。请核实修改。无错误的请说明原因。\"}]}";

				prest.setString(4, content);
				prest.setInt(5, 1);
				prest.setInt(6, 0);
				prest.setString(7, "06-7月 -17 02.35.42.643 下午");
				int executeUpdate = prest.executeUpdate();
				prest.close();
			}

			
			for (String region : regionMap.keySet()) {
				StringBuilder sql = new StringBuilder();
				sql.append("insert into SNP_DATAMSGLOG(RECID,MSG_NAME,MSG_TYPE,MSG_CONTENT,PCS_TYPE,PCS_RESULT,PCS_TIME) values(?,?,?,?,?,?,?) ");

				PreparedStatement prest = conn.prepareStatement(sql.toString());
				prest.setString(1, GUID.newGUID());
				String name = "3NP_603_1_";
				if (region.equals("6561")) {
					name += "65610000000000_20170706143642";
				} else {
					name += region;
					name += "000000000000_20170706143642";
				}
				prest.setString(2, name);

				prest.setInt(3, 1);
				String content = "{\"NAME\":\""
						+ name
						+ "\",\"XXFL\":\"1\",\"XXNR\":[{\"YSJB\":\"02\",\"BH\":\"603\",\"YSZT\":\"0\",\"RWDM\":\"3NP\",\"TJJGDM\":\"000000000000\",\"YSSJ\":\"20170706143434\",\"BGQ\":\"201601YY\",\"TJJGMC\":\"全国\",\"DCDXXTMLIST\":[\""
						+ regionMap.get(region)
						+ "\"],\"YSR\":\"孙腾蛟\",\"YSYJ\":\"2017-07-06 14:36:34 国家验收不通过，验收说明：有农、牧、渔或服务业经营，但没有填写农林牧渔各业经营支出，也没有填写经营收入。请核实修改。无错误的请说明原因。\"}]}";

				prest.setString(4, content);
				prest.setInt(5, 1);
				prest.setInt(6, 0);
				prest.setString(7, "06-7月 -17 02.36.42.643 下午");
				int executeUpdate = prest.executeUpdate();
				prest.close();
			}
			
			
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			if (null != conn) {
				conn.close();
			}
		}// end try

	}

	private Map<String, String> regionMap = new HashMap<String, String>();

	void csvread() throws IOException {
		CsvReader r = new CsvReader("C://3.csv", ',', Charset.forName("GBK"));
		r.readHeaders();
		StringBuilder sb = new StringBuilder();
		String region = null;
		String c = null;
		while (r.readRecord()) {
			String target = r.get("TARGET_CODE");
			c = r.get("REGION_CODE");
			if (c.equals(region)) {
				if (sb.length() > 0) {
					sb.append("\",\"");
				}
				sb.append(target);
			} else {
				if (0 < sb.length()) {
					String[] content = { region, "\"" + sb.toString() + "\"" };
					regionMap.put(region, sb.toString());
					System.out.println(content[0] + ":" + content[1]);
				}
				region = c;
				sb = new StringBuilder("");
			}

		}
		if (0 < sb.length() && c != null) {
			String[] content = { c, "\"" + sb.toString() + "\"" };
			regionMap.put(region, sb.toString());
			System.out.println(content[0] + ":" + content[1]);
		}
		r.close();
	}

//	String guoStr = "{\"NAME\":\""
//			+ 1
//			+ "\",\"XXFL\":\"1\",\"XXNR\":[{\"YSJB\":\"01\",\"BH\":\"603\",\"YSZT\":\"1\",\"RWDM\":\"3NP\",\"TJJGDM\":\"000000000000\",\"YSSJ\":\"20170706143434\",\"BGQ\":\"201601YY\",\"TJJGMC\":\"全国\",\"DCDXXTMLIST\":["
//			+ 2
//			+ "],YSR\":\"孙腾蛟\",\"YSYJ\":\"2017-07-06 14:35:34 国家验收不通过，验收说明：有农、牧、渔或服务业经营，但没有填写农林牧渔各业经营支出，也没有填写经营收入。请核实修改。无错误的请说明原因。\"}]}";
//
//	String shengStr = "{\"NAME\":\""
//			+ 1
//			+ "\",\"XXFL\":\"1\",\"XXNR\":[{\"YSJB\":\"02\",\"BH\":\"603\",\"YSZT\":\"0\",\"RWDM\":\"3NP\",\"TJJGDM\":\"000000000000\",\"YSSJ\":\"20170706143434\",\"BGQ\":\"201601YY\",\"TJJGMC\":\"全国\",\"DCDXXTMLIST\":["
//			+ 2
//			+ "],YSR\":\"孙腾蛟\",\"YSYJ\":\"2017-07-06 14:35:34 国家验收不通过，验收说明：有农、牧、渔或服务业经营，但没有填写农林牧渔各业经营支出，也没有填写经营收入。请核实修改。无错误的请说明原因。\"}]}";

}
