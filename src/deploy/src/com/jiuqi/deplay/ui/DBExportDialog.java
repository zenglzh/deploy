package com.jiuqi.deplay.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deplay.exe.ExpdmpThread;
import com.jiuqi.deplay.exe.Monitor;
import com.jiuqi.deplay.util.IOUtils;
import com.jiuqi.deplay.util.StringHelper;

public class DBExportDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private PageContext pageContext;
	private JTextField t_exportpath;
	private JLabel l_tip;
	private JTextField t_filename;
	private JProgressBar progressBar;
	private Monitor monitor;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DBExportDialog dialog = new DBExportDialog(new PageContext());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DBExportDialog(PageContext pageContext) {
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(DBExportDialog.class.getResource("/com/jiuqi/deplay/images/export.png")));
		this.pageContext = pageContext;
		setBounds(100, 100, 461, 362);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC, },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, }));
		{
			JLabel label = new JLabel("\u5BFC\u51FA");
			contentPanel.add(label, "2, 2");
		}
		{
			l_tip = new JLabel("");
			contentPanel.add(l_tip, "4, 2");
		}
		{
			JLabel lblNewLabel = new JLabel("\u5BFC\u51FA\u76EE\u5F55");
			contentPanel.add(lblNewLabel, "2, 4, right, default");
		}
		{
			t_exportpath = new JTextField();
			contentPanel.add(t_exportpath, "4, 4, fill, default");
			t_exportpath.setColumns(10);
		}
		{
			JButton b_choose = new JButton("\u9009\u62E9");
			b_choose.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					fileChoose();
				}
			});
			contentPanel.add(b_choose, "6, 4");
		}
		{
			JLabel lblNewLabel_1 = new JLabel("\u5BFC\u51FA\u6587\u4EF6\u540D");
			lblNewLabel_1.setToolTipText("");
			contentPanel.add(lblNewLabel_1, "2, 6");
		}
		{
			t_filename = new JTextField();
			t_filename.setText("EXPDAT.DMP");
			contentPanel.add(t_filename, "4, 6, fill, top");
			t_filename.setColumns(10);
		}
		{
			JScrollPane spane_log = new JScrollPane();
			spane_log.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			contentPanel.add(spane_log, "2, 8, 5, 3, fill, fill");
			{
				JPanel panel = new JPanel();
				spane_log.setViewportView(panel);
				panel.setLayout(new GridLayout(1, 0, 0, 0));
				monitor = new Monitor(panel);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u5BFC\u51FA");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (validateFields()) {
							try {
								backup();
							} catch (Exception ec) {
								showErrorStatus(ec.getMessage());
								ec.printStackTrace();
							}
						}
					}
				});
				{
					JSeparator separator = new JSeparator();
					buttonPane.add(separator);
				}
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\u5173\u95ED");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	private boolean validateFields() {
		if(StringHelper.isEmpty(t_exportpath.getText())){
			showErrorStatus("请先选择导出文件目录!");
			return false;
		} else if (!IOUtils.workDirIsOk(t_exportpath.getText())) {
			showErrorStatus("文件目录目录不合法或没有写权限!请重新选择。");
			return false;
		} else if (StringHelper.isEmpty(t_filename.getText())) {
			showErrorStatus("文件名不能为空！");
			return false;
		}
		return true;
	}

	private void showErrorStatus(String text) {
		l_tip.setText(text);
		l_tip.setToolTipText(text);
		l_tip.setForeground(Color.RED);
	}

	private void showOkStatus(String text) {
		l_tip.setText(text);
		l_tip.setToolTipText(text);
		l_tip.setForeground(Color.DARK_GRAY);
	}
	private void fileChoose() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.showDialog(new JLabel(), "选择");
		File file = jfc.getSelectedFile();
		if (null != file) {
			t_exportpath.setText(file.getAbsolutePath());
		}
	}

	private String getExportFullFileName() {
		String path = t_exportpath.getText();
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		return path + t_filename.getText();
	}

	private void backup() {
		ExpdmpThread thread = new ExpdmpThread(monitor, pageContext.getConnectionInfo(), getExportFullFileName());
		Thread ct = new Thread(thread);
		ct.start();
	}

}
