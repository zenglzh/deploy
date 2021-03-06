package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.db.UserInfo;
import com.jiuqi.deploy.exe.ImpdmpThread;
import com.jiuqi.deploy.exe.Monitor;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.StringHelper;

public class DBImportDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private PageContext pageContext;
	private JTextField t_exportpath;
	private JLabel l_tip;
	private Monitor monitor;
	private JComboBox cb_touser;
	private JComboBox cb_fromuser;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DBImportDialog dialog = new DBImportDialog(new PageContext());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * 
	 * @param pageContext
	 */
	public DBImportDialog(PageContext pageContext) {
		setIconImage(ImageRes.getImage(ImageRes.PNG_IMPORT));
		this.pageContext = pageContext;
		setBounds(100, 100, 517, 468);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(55dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
 RowSpec.decode("24dlu"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		{
			JLabel label = new JLabel("\u5BFC\u5165");
			contentPanel.add(label, "2, 2");
		}
		{
			l_tip = new JLabel("");
			contentPanel.add(l_tip, "4, 2");
		}
		{
			JLabel lblNewLabel = new JLabel("\u5BFC\u5165\u6587\u4EF6\uFF08dmp\uFF09");
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
			JPanel panel = new JPanel();
			contentPanel.add(panel, "2, 6, 5, 1, fill, fill");
			panel.setLayout(
new FormLayout(
					new ColumnSpec[] { ColumnSpec.decode("max(35dlu;default)"), FormSpecs.RELATED_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL,
									Sizes.bounded(Sizes.DEFAULT, Sizes.constant("90dlu", true),
											Sizes.constant("100dlu", true)),
									0),
							FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL,
									Sizes.bounded(Sizes.DEFAULT, Sizes.constant("80dlu", true),
											Sizes.constant("100dlu", true)),
									0), },
					new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));
			{
				JLabel lblNewLabel_2 = new JLabel("  From User");
				panel.add(lblNewLabel_2, "1, 2, right, default");
			}
			{
				cb_fromuser = new JComboBox();
				panel.add(cb_fromuser, "3, 2, fill, default");
			}
			{
				JLabel lblNewLabel_3 = new JLabel(" To User");
				panel.add(lblNewLabel_3, "5, 2, right, default");
			}
			{
				cb_touser = new JComboBox();
				panel.add(cb_touser, "7, 2, fill, default");
			}
			initUsers();
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
				JButton okButton = new JButton("\u5BFC\u5165");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (validateFields()) {
							try {
								restore();
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

	private void initUsers() {
		DatabaseConnectionInfo connectionInfo = pageContext.getConnectionInfo();
		if (null != connectionInfo) {
			ESBDBClient client = new ESBDBClient(connectionInfo);
			List<UserInfo> userInfos;
			try {
				userInfos = client.queryUsers();
				if (null != userInfos && !userInfos.isEmpty()) {
					for (UserInfo user : userInfos) {
						cb_fromuser.addItem(user);
						cb_touser.addItem(user);
					}
				} else {
					showErrorStatus("请先创建用户!");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			cb_fromuser.setSelectedItem(connectionInfo.getUsername());
			cb_touser.setSelectedItem(connectionInfo.getUsername());
		} else {
			showErrorStatus("请先连接数据库！");
		}
	}

	private boolean validateFields() {
		if (StringHelper.isEmpty(t_exportpath.getText())) {
			showErrorStatus("请先选择备份文件!");
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
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.showDialog(new JLabel(), "选择");
		File file = jfc.getSelectedFile();
		t_exportpath.setText(file.getAbsolutePath());
	}

	protected void restore() throws Exception {
		String fromuser = cb_fromuser.getSelectedItem().toString();
		String touser = cb_touser.getSelectedItem().toString();
		ImpdmpThread thread = new ImpdmpThread(monitor, pageContext.getConnectionInfo(), t_exportpath.getText(),
				fromuser, touser);
		Thread ct = new Thread(thread);
		ct.start();
	}

}
