package com.jiuqi.deplay.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import com.jiuqi.deplay.db.ESBDBClient;
import com.jiuqi.deplay.db.TablespaceInfo;
import com.jiuqi.deplay.db.UserProperties;
import com.jiuqi.deplay.util.DatabaseConnectionInfo;
import com.jiuqi.deplay.util.StringHelper;

public class CreateUserDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private PageContext pageContext;
	private JTextField t_username;
	private JPasswordField t_password;
	private JPasswordField t_password_conf;
	private JComboBox cb_def_tablespace;
	private JLabel l_tip;
	private JComboBox cb_temp_tablespace;
	private JCheckBox cb_connect;
	private JCheckBox ch_resource;
	private JCheckBox cb_dba;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CreateUserDialog dialog = new CreateUserDialog(new PageContext());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CreateUserDialog(PageContext pageContext) {
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(CreateUserDialog.class.getResource("/com/jiuqi/deplay/images/user.png")));
		this.pageContext = pageContext;
		setBounds(100, 100, 375, 401);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(1, 0, 0, 0));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("\u5E38\u89C4", null, panel, null);
				panel.setLayout(
new FormLayout(
						new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
								FormSpecs.RELATED_GAP_COLSPEC,
								new ColumnSpec(ColumnSpec.FILL,
										Sizes.bounded(Sizes.DEFAULT, Sizes.constant("50dlu", true),
												Sizes.constant("50dlu", true)),
										1),
								FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(0dlu;default)"),
								FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, },
						new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
								RowSpec.decode("default:grow"), FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));
				{
					JLabel lblNewLabel_6 = new JLabel("\u65B0\u5EFA\u7528\u6237");
					lblNewLabel_6.setFont(new Font("宋体", Font.BOLD, 12));
					panel.add(lblNewLabel_6, "2, 2");
				}
				{
					l_tip = new JLabel("");
					panel.add(l_tip, "4, 2, 3, 1, left, default");
				}
				{
					JLabel lblNewLabel = new JLabel("\u7528\u6237\u540D");
					panel.add(lblNewLabel, "2, 4, right, default");
				}
				{
					t_username = new JTextField();
					panel.add(t_username, "4, 4, fill, default");
					t_username.setColumns(10);
				}
				{
					JLabel lblNewLabel_3 = new JLabel("*");
					panel.add(lblNewLabel_3, "6, 4");
				}
				{
					JLabel lblNewLabel_1 = new JLabel("\u5BC6\u7801");
					panel.add(lblNewLabel_1, "2, 6, right, default");
				}
				{
					t_password = new JPasswordField();
					panel.add(t_password, "4, 6, fill, default");
				}
				{
					JLabel lblNewLabel_4 = new JLabel("*");
					panel.add(lblNewLabel_4, "6, 6");
				}
				{
					JLabel lblNewLabel_2 = new JLabel("\u786E\u8BA4\u5BC6\u7801");
					panel.add(lblNewLabel_2, "2, 8, right, default");
				}
				{
					t_password_conf = new JPasswordField();
					panel.add(t_password_conf, "4, 8, fill, default");
				}
				{
					JLabel lblNewLabel_5 = new JLabel("*");
					panel.add(lblNewLabel_5, "6, 8");
				}
				{
					JLabel label = new JLabel("\u9ED8\u8BA4\u8868\u7A7A\u95F4");
					panel.add(label, "2, 10, right, default");
				}
				{
					cb_def_tablespace = new JComboBox();
					panel.add(cb_def_tablespace, "4, 10, fill, default");
				}
				{
					JLabel lblNewLabel_7 = new JLabel("\u4E34\u65F6\u8868\u7A7A\u95F4");
					panel.add(lblNewLabel_7, "2, 12, right, default");
				}
				{
					cb_temp_tablespace = new JComboBox();
					panel.add(cb_temp_tablespace, "4, 12, fill, default");
				}
				{
					JLabel lblNewLabel_8 = new JLabel("\u9ED8\u8BA4\u6388\u6743");
					lblNewLabel_8.setVerticalAlignment(SwingConstants.TOP);
					lblNewLabel_8.setFont(new Font("宋体", Font.BOLD, 12));
					panel.add(lblNewLabel_8, "2, 14, default, top");
				}
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, "4, 14, left, fill");
					{
						cb_connect = new JCheckBox("CONNECT");
						panel_1.add(cb_connect);
					}
					{
						ch_resource = new JCheckBox("RESOURCE");
						panel_1.add(ch_resource);
					}
					{
						cb_dba = new JCheckBox("DBA");
						panel_1.add(cb_dba);
					}
				}
				initTablespacefield();
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("\u786E\u5B9A");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (validateFields()) {
							try {
								createUser();
								int showConfirmDialog = JOptionPane.showOptionDialog(CreateUserDialog.this, "创建用户成功！",
										"提示", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
										null, null, null);
								if (showConfirmDialog == JOptionPane.YES_OPTION) {
									CreateUserDialog.this.dispose();
								}
							} catch (ClassNotFoundException e1) {
								JOptionPane.showMessageDialog(null, "数据库驱动未找到！", "错误", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (SQLException e1) {
								JOptionPane.showMessageDialog(null, "创建用户失败！" + e1.getMessage(), "错误",
										JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							}
						}
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				final JButton cancelButton = new JButton("\u53D6\u6D88");
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

	private void initTablespacefield() {
		DatabaseConnectionInfo connectionInfo = pageContext.getConnectionInfo();
		if (null != connectionInfo) {
			ESBDBClient client = new ESBDBClient(connectionInfo);
			List<TablespaceInfo> queryTablespace;
			try {
				queryTablespace = client.queryTablespace();
				if (null != queryTablespace && !queryTablespace.isEmpty()) {
					for (TablespaceInfo tablespace : queryTablespace) {
						cb_def_tablespace.addItem(tablespace.getName());
						cb_temp_tablespace.addItem(tablespace.getName());
					}
				} else {
					showErrorStatus("请先创建表空间!");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				showErrorStatus(e.getMessage() + "，可能是没有权限。");
			}
		} else {
			showErrorStatus("请先连接数据库！");
		}
		cb_def_tablespace.setSelectedItem(null);
		cb_temp_tablespace.setSelectedItem(null);
	}

	public boolean validateFields() {
		boolean isValid = true;
		String message = "";
		if (StringHelper.isEmpty(t_username.getText())) {
			isValid = false;
			message = "用户名不能为空！";
			requestFocus(t_username);
		} else if (StringHelper.isEmpty(new String(t_password.getPassword()))) {
			isValid = false;
			message = "密码不能为空！";
			requestFocus(t_password);
		} else
			if (!StringHelper.equals(new String(t_password.getPassword()), new String(t_password_conf.getPassword()))) {
			isValid = false;
			message = "两次设置密码不一致！";
			requestFocus(t_password);
		}
		if (!isValid) {
			showErrorStatus(message);
		}
		showOkStatus("");
		return isValid;
	}


	private void requestFocus(JTextField text) {
		clearFieldBorder();
		text.requestFocus();
		text.setBorder(new LineBorder(Color.RED));
	}

	private void clearFieldBorder() {
		t_username.setBorder(new LineBorder(Color.GRAY));
		t_password.setBorder(new LineBorder(Color.GRAY));
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

	private void createUser() throws ClassNotFoundException, SQLException {
		UserProperties properties = new UserProperties();
		properties.setUserName(t_username.getText());
		properties.setPassword(new String(t_password.getPassword()));
		if (null != cb_def_tablespace.getSelectedItem()) {
			properties.setDefaultTablespace(cb_def_tablespace.getSelectedItem().toString());
		}
		if (null != cb_temp_tablespace.getSelectedItem()) {
			properties.setTempTablespace(cb_temp_tablespace.getSelectedItem().toString());
		}
		if (cb_connect.isSelected()) {
			properties.addprivileges(UserProperties.PRIV_CONNECT);
		}
		if (ch_resource.isSelected()) {
			properties.addprivileges(UserProperties.PRIV_RESOURCE);
		}

		if (cb_dba.isSelected()) {
			properties.addprivileges(UserProperties.PRIV_DBA);
		}

		ESBDBClient client = new ESBDBClient(pageContext.getConnectionInfo());
		client.createUser(properties);
		client.grantUser(properties);
	}

}
