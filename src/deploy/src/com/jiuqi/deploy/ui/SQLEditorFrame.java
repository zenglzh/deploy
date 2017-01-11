package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.ShowMessage;

public class SQLEditorFrame extends JFrame implements TableModelListener {

	private static final long serialVersionUID = 1L;
	private final JPanel mainPanel = new JPanel();
	private RSyntaxTextArea editor;
	private ShowMessage showMessage;
	private QueryDataPanel dataPanel;
	private ESBDBClient dbconnect;
	private String lastSQL = null;
	private JLabel statusLabel;

	/**
	 * Create the dialog.
	 */
	public SQLEditorFrame(ESBDBClient connection) {
		this.dbconnect = connection;
		init();
	}

	private void init() {
		getContentPane().setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { RowSpec.decode("18dlu"),
				FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("max(204dlu;default):grow"), RowSpec.decode("20dlu"), RowSpec.decode("max(2dlu;default)"), }));

		JPanel panel = new JPanel();
		mainPanel.add(panel, "2, 1, fill, fill");

		JButton btn_execute = new JButton("");
		btn_execute.setIcon(ImageRes.getIcon(ImageRes.PNG_EXECUTE));
		btn_execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		panel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		panel.add(btn_execute);

		JButton btn_explain = new JButton("Explain");
		btn_explain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				explain(e);
			}
		});
		panel.add(btn_explain);
		JLabel lbl_status = new JLabel("");
		showMessage = new ShowMessage(lbl_status);
		panel.add(lbl_status);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		editor = new RSyntaxTextArea(20, 60);
		editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		editor.setCodeFoldingEnabled(true);
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
		statePanel.add(statusLabel);
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
				stmt.execute("EXPLAIN PLAN SET STATEMENT_ID = '" + id + "' INTO " + Options.getInstance().getOracleExplainPlanTable() + " FOR " + newsql);
				stmt.close();
				dataPanel.setQuery("SELECT LPAD(' ',2*(LEVEL-1))||operation operation, options," + "object_name, position " + "FROM " + Options.getInstance().getOracleExplainPlanTable() + " "
						+ "START WITH id = 0 AND statement_id = '" + id + "'" + "CONNECT BY PRIOR id = parent_id AND statement_id = '" + id + "'", new Vector());
				dataPanel.getTable().setShowGrid(false);
				((CustomTableModel) dataPanel.getTableModel()).setEditMode(CustomTableModel.DETAIL_REC);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, Options.getInstance().getResource("feature not supported for this database type."), Options.getInstance().getResource("attention"),
					JOptionPane.WARNING_MESSAGE);
		}
	}

}
