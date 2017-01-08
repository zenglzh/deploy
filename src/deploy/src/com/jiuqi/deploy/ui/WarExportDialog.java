package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deploy.exe.ExpWarThread;
import com.jiuqi.deploy.exe.Monitor;
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.StringHelper;

public class WarExportDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField t_dapath;
	private Monitor monitor;
	private JTextField t_project;
	private JLabel l_tip;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			WarExportDialog dialog = new WarExportDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public WarExportDialog() {
		setIconImage(ImageRes.getImage(ImageRes.PNG_WAR));
		setBounds(100, 100, 465, 449);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC, },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("default:grow"), FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));
		{
			JLabel lblwar = new JLabel("\u5BFC\u51FA WAR");
			lblwar.setFont(new Font("宋体", Font.BOLD, 12));
			contentPanel.add(lblwar, "2, 2");
		}
		{
			l_tip = new JLabel("");
			contentPanel.add(l_tip, "4, 2, left, default");
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "2, 4, 5, 1");
		}
		{
			JLabel label = new JLabel("\u5DE5\u7A0B\u540D");
			contentPanel.add(label, "2, 6, right, default");
		}
		{
			t_project = new JTextField();
			t_project.setToolTipText("war \u5305\u540D\u5B57");
			contentPanel.add(t_project, "4, 6, fill, default");
			t_project.setColumns(10);
		}
		{
			JLabel lblNewLabel = new JLabel("DA \u5305");
			contentPanel.add(lblNewLabel, "2, 8, right, default");
		}
		{
			t_dapath = new JTextField();
			contentPanel.add(t_dapath, "4, 8, fill, default");
			t_dapath.setColumns(10);
		}
		{
			JButton b_choose = new JButton("\u9009\u62E9");
			b_choose.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					fileChoose();
				}
			});
			contentPanel.add(b_choose, "6, 8");
		}
		{
			JScrollPane spane_log = new JScrollPane();
			spane_log.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			contentPanel.add(spane_log, "2, 10, 5, 3, fill, fill");
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
								export();
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
		if (StringHelper.isEmpty(t_project.getText())) {
			showErrorStatus("工程名字不能为空!");
			return false;
		}
		if (StringHelper.isEmpty(t_dapath.getText())) {
			showErrorStatus("请先选择DA包!");
			return false;
		} else if (StringHelper.isEmpty(t_dapath.getText())) {
			showErrorStatus("文件名不能为空！");
			return false;
		} else if (!t_dapath.getText().endsWith(".da")) {
			showErrorStatus("只能选择da包！");
			return false;
		}
		showOkStatus("");
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

	private File dafile;
	private void fileChoose() {
		JFileChooser jfc = new JFileChooser();
		DAFileFilter fileFilter = new DAFileFilter();
		jfc.addChoosableFileFilter(fileFilter);
		jfc.setFileFilter(fileFilter);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.showDialog(new JLabel(), "选择");
		dafile = jfc.getSelectedFile();
		if (null != dafile) {
			t_dapath.setText(dafile.getAbsolutePath());
		}
	}

	private void export() {
		ExpWarThread thread = new ExpWarThread(monitor, t_project.getText(), dafile);
		Thread ct = new Thread(thread);
		ct.start();
	}


	//////////////////////////////////////////////

	class DAFileFilter extends FileFilter {
		@Override
		public String getDescription() {
			return "DA包(*.da)";
		}

		@Override
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".da") || f.isDirectory();
		}
	}

}
