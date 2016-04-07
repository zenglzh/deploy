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
import com.jiuqi.deplay.db.UserInfo;
import com.jiuqi.deplay.db.UserProperties;
import com.jiuqi.deplay.util.DatabaseConnectionInfo;

public class EditUserDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private PageContext pageContext;
	private JPasswordField t_password;
	private JComboBox cb_def_tablespace;
	private JLabel l_tip;
	private JComboBox cb_temp_tablespace;
	private JCheckBox cb_connect;
	private JCheckBox cb_resource;
	private JCheckBox cb_dba;
	private JComboBox cb_users;

	private UserInfo currentUser;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EditUserDialog dialog = new EditUserDialog(new PageContext());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EditUserDialog(PageContext pageContext) {
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(EditUserDialog.class.getResource("/com/jiuqi/deplay/images/user.png")));
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
								RowSpec.decode("default:grow"), FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));
				{
					JLabel lblNewLabel_6 = new JLabel("\u7F16\u8F91\u7528\u6237");
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
					cb_users = new JComboBox();
					cb_users.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							selectUser();
						}

					});
					panel.add(cb_users, "4, 4, fill, default");
				}
				{
					JLabel lblNewLabel_1 = new JLabel("\u65B0\u5BC6\u7801");
					panel.add(lblNewLabel_1, "2, 6, right, default");
				}
				{
					t_password = new JPasswordField();
					panel.add(t_password, "4, 6, fill, default");
				}
				{
					JLabel label = new JLabel("\u9ED8\u8BA4\u8868\u7A7A\u95F4");
					panel.add(label, "2, 8, right, default");
				}
				{
					cb_def_tablespace = new JComboBox();
					panel.add(cb_def_tablespace, "4, 8, fill, default");
				}
				{
					JLabel lblNewLabel_7 = new JLabel("\u4E34\u65F6\u8868\u7A7A\u95F4");
					panel.add(lblNewLabel_7, "2, 10, right, default");
				}
				{
					cb_temp_tablespace = new JComboBox();
					panel.add(cb_temp_tablespace, "4, 10, fill, default");
				}
				{
					JLabel lblNewLabel_8 = new JLabel("\u9ED8\u8BA4\u6388\u6743");
					lblNewLabel_8.setVerticalAlignment(SwingConstants.TOP);
					lblNewLabel_8.setFont(new Font("宋体", Font.BOLD, 12));
					panel.add(lblNewLabel_8, "2, 12, default, top");
				}
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, "4, 12, left, fill");
					{
						cb_connect = new JCheckBox("connect");
						panel_1.add(cb_connect);
					}
					{
						cb_resource = new JCheckBox("resource");
						panel_1.add(cb_resource);
					}
					{
						cb_dba = new JCheckBox("dba");
						panel_1.add(cb_dba);
					}
				}
				initTablespacefield();
			}
			initUsers();
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("\u5E94\u7528");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (validateFields()) {
							try {
								modifyUser();
								JOptionPane.showOptionDialog(EditUserDialog.this, "修改用户成功！",
										"提示", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
										null, null, null);
								resetUserList();
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

					private void resetUserList() {
						cb_users.removeAllItems();
						initUsers();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				final JButton cancelButton = new JButton("\u5173\u95ED");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						EditUserDialog.this.dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	private void selectUser() {
		cb_connect.setSelected(false);
		cb_dba.setSelected(false);
		cb_resource.setSelected(false);
		UserInfo selectedItem = (UserInfo) cb_users.getSelectedItem();
		currentUser = selectedItem;
		if(null== selectedItem){
			return;
		}
		t_password.setText(UserInfo.SHOW_PASSWORD);
		cb_def_tablespace.setSelectedItem(selectedItem.getDefaultTablespace());
		cb_temp_tablespace.setSelectedItem(selectedItem.getTemp_Tablespace());
		List<String> roles = selectedItem.getRolePries();
		if (null != roles && !roles.isEmpty()) {
			for (String r : roles) {
				if (UserProperties.PRIV_CONNECT.equalsIgnoreCase(r)) {
					cb_connect.setSelected(true);
				}
				if (UserProperties.PRIV_RESOURCE.equalsIgnoreCase(r)) {
					cb_resource.setSelected(true);
				}
				if (UserProperties.PRIV_DBA.equalsIgnoreCase(r)) {
					cb_dba.setSelected(true);
				}
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
						cb_users.addItem(user);
					}
				} else {
					showErrorStatus("请先创建用户!");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			cb_users.setSelectedItem(connectionInfo.getUsername());
		} else {
			showErrorStatus("请先连接数据库！");
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
		return isValid;
	}


	private void requestFocus(JTextField text) {
		clearFieldBorder();
		text.requestFocus();
		text.setBorder(new LineBorder(Color.RED));
	}

	private void clearFieldBorder() {
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

	private void modifyUser() throws ClassNotFoundException, SQLException {
		boolean usermodified = false;

		UserProperties properties = new UserProperties();
		properties.setUserName(currentUser.getUsername());
		if (null != t_password.getPassword() && !UserInfo.SHOW_PASSWORD.equals(new String(t_password.getPassword()))) {
			properties.setPassword(new String(t_password.getPassword()));
			usermodified = true;
		}
		if (!currentUser.getDefaultTablespace().equals(cb_def_tablespace.getSelectedItem())) {
			properties.setDefaultTablespace(cb_def_tablespace.getSelectedItem().toString());
			usermodified = true;
		}
		if (!currentUser.getTemp_Tablespace().equals(cb_temp_tablespace.getSelectedItem())) {
			properties.setTempTablespace(cb_temp_tablespace.getSelectedItem().toString());
			usermodified = true;
		}
		ESBDBClient client = new ESBDBClient(pageContext.getConnectionInfo());
		if (usermodified) {
			client.modifyUser(properties);
		}
		/////
		boolean rolemodified = false;
		rolemodified = collectModified(properties, rolemodified, UserProperties.PRIV_CONNECT, cb_connect);
		rolemodified = collectModified(properties, rolemodified, UserProperties.PRIV_RESOURCE, cb_resource);
		rolemodified = collectModified(properties, rolemodified, UserProperties.PRIV_DBA, cb_dba);
		if (rolemodified) {
			client.grantUser(properties);
		}
	}

	private boolean collectModified(UserProperties properties, boolean rolemodified, String priv, JCheckBox cb) {
		if (currentUser.hasRole(priv)) {
			if (cb.isSelected()) {

			} else {
				rolemodified = true;
				properties.addRevokeprivilegs(priv);
			}

		} else {
			if (cb.isSelected()) {
				rolemodified = true;
				properties.addprivileges(priv);
			} else {

			}
		}
		return rolemodified;
	}

}
