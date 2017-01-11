package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.jsqltool.model.CustomTableModel;

import com.jiuqi.deploy.intf.ISelect;
import com.jiuqi.deploy.server.HistorySQLManage;

public class HistorySQLDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private CustomTableModel model = new CustomTableModel(new String[] { "SQL" }, new Class[] { String.class }, new int[] { 100 });
	private JTable oldQueries;
	private HistorySQLManage sqlmeme;

	/**
	 * Create the dialog.
	 * 
	 * @param sqlmeme
	 */
	public HistorySQLDialog(final ISelect<String> iSelect, HistorySQLManage sqlmeme) {
		this.sqlmeme = sqlmeme;
		init();
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			oldQueries = new JTable(model);
			JScrollPane scrollPane = new JScrollPane(oldQueries);
			contentPanel.add(scrollPane);

		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u9009\u62E9");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int row = oldQueries.getSelectedRow();
						Object valueAt = model.getValueAt(row, model.getColumnCount() - 1);
						if (null != valueAt) {
							iSelect.select(String.valueOf(valueAt).trim());
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\u53D6\u6D88");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void init() {
		model.setEditMode(CustomTableModel.DETAIL_REC);
		List<String> allSQLs = sqlmeme.allSQLs();
		for (int i = allSQLs.size() - 1; i >= 0; i--) {
			model.addRow(new Object[] { allSQLs.get(i) });
		}
	}
}
