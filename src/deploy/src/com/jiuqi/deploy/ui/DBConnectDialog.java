package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.util.ConnectionInfo;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.SettingsManager;
import com.jiuqi.deploy.util.SettingsManagerFactory;
import com.jiuqi.deploy.util.StringHelper;

public class DBConnectDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField t_database;
	private JTextField t_ip;
	private JTextField t_port;
	private JTextField t_username;
	private JLabel l_testresult;
	private JPasswordField t_password;
	private SettingsManager settingsManager;
	private JComboBox cb_id;
	private PageContext pageContxt;
	private ArrayList<ConnectionInfo> connectionsInfo = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DBConnectDialog dialog = new DBConnectDialog(new PageContext());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initsetting() {
		this.settingsManager = SettingsManagerFactory.getSettingsManager();
		this.connectionsInfo = settingsManager.getConnectionsInfo();
		if (null == connectionsInfo) {
			connectionsInfo = new ArrayList<ConnectionInfo>();
		}
		DatabaseConnectionInfo dbconnectionInfo = pageContxt.getConnectionInfo();
		if (null == dbconnectionInfo && !connectionsInfo.isEmpty()) {// 从系统记录中获取上传存的值
			ConnectionInfo connectionInfo = connectionsInfo.get(connectionsInfo.size() - 1);
			pageContxt.setConnectionInfo(connectionInfo.getDatabaseConnectionInfo());
		}
	}

	/**
	 * Create the dialog.
	 */
	public DBConnectDialog(final PageContext pageContext) {
		setIconImage(ImageRes.getImage(ImageRes.PNG_CONNECT));
		this.pageContxt = pageContext;
		initsetting();
		setTitle("\u6570\u636E\u5E93\u8FDE\u63A5");
		setBounds(100, 100, 375, 327);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(53dlu;default)"),
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(97dlu;default):grow"),
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("13dlu"), FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("13dlu"), FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, }));
		{
			JLabel label = new JLabel("\u767B\u5F55");
			label.setFont(new Font("宋体", Font.BOLD, 12));
			contentPanel.add(label, "2, 2");
		}
		{
			JLabel lblNewLabel = new JLabel("\u6570\u636E\u5E93");
			contentPanel.add(lblNewLabel, "2, 4, right, default");
		}
		{
			t_database = new JTextField();
			contentPanel.add(t_database, "4, 4, fill, default");
			t_database.setColumns(10);
		}
		{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			contentPanel.add(panel, "6, 4, fill, fill");
			{
				JMenuBar menuBar = new JMenuBar();
				panel.add(menuBar);
				{
					JMenu mn_db = new JMenu("...");
					if (null != connectionsInfo) {
						for (int i = connectionsInfo.size() - 1; i >= 0; i--) {
							ConnectionInfo connectionInfo = connectionsInfo.get(i);
							mn_db.add(addDBMenuLocal(connectionInfo.getDatabaseConnectionInfo()));
						}
					}
					menuBar.add(mn_db);
				}
			}
		}
		{
			JLabel lblNewLabel_1 = new JLabel("\u670D\u52A1\u5730\u5740");
			contentPanel.add(lblNewLabel_1, "2, 6, right, default");
		}
		{
			t_ip = new JTextField();
			contentPanel.add(t_ip, "4, 6, fill, default");
			t_ip.setColumns(10);
		}
		{
			JLabel lblNewLabel_2 = new JLabel("\u7AEF\u53E3");
			contentPanel.add(lblNewLabel_2, "2, 8, right, default");
		}
		{
			t_port = new JTextField();
			contentPanel.add(t_port, "4, 8, fill, default");
			t_port.setColumns(10);
		}
		{
			JLabel lblNewLabel_3 = new JLabel("\u7528\u6237\u540D");
			contentPanel.add(lblNewLabel_3, "2, 10, right, default");
		}
		{
			t_username = new JTextField();
			contentPanel.add(t_username, "4, 10, fill, default");
			t_username.setColumns(10);
		}
		{
			JLabel lblNewLabel_4 = new JLabel("\u5BC6\u7801");
			contentPanel.add(lblNewLabel_4, "2, 12, right, default");
		}
		{
			t_password = new JPasswordField();
			contentPanel.add(t_password, "4, 12, fill, default");
		}
		{
			JLabel label = new JLabel("\u8FDE\u63A5\u8EAB\u4EFD");
			contentPanel.add(label, "2, 14, right, default");
		}
		{
			cb_id = new JComboBox(DatabaseConnectionInfo.CONNECTID);
			contentPanel.add(cb_id, "4, 14, fill, default");
		}
		{
			JButton b_test = new JButton("\u6D4B\u8BD5");
			contentPanel.add(b_test, "2, 16");
			l_testresult = new JLabel("");
			contentPanel.add(l_testresult, "4, 16");
			b_test.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (validateFields()) {
						ConnectionInfo connectionInfo = getCurrentConnectionInfo();
						DatabaseConnectionInfo db = connectionInfo.getDatabaseConnectionInfo();
						try {
							ESBDBClient dbClient = new ESBDBClient(db);
							dbClient.testConnect();
							showOkStatus("连接测试成功！");
						} catch (Exception de) {
							showErrorStatus("Error:\n" + de);
						}
					}
				}
			});
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("\u8FDE\u63A5");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						save();
						fileConnectChanged();
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
						DBConnectDialog.this.dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		fillFields(this.pageContxt.getConnectionInfo());
	}

	private JMenuItem addDBMenuLocal(final DatabaseConnectionInfo databaseConnectionInfo) {
		JMenuItem mitem = new JMenuItem(databaseConnectionInfo.toString());
		mitem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fillFields(databaseConnectionInfo);
			}
		});
		return mitem;
	}

	public void fileConnectChanged() {
		pageContxt.firePropertyChange(DatabaseConnectionInfo.DBConnectPro);
	}

	private void save() {
		ConnectionInfo connectionInfo = createOrSaveConnectionInfo();
		if (null != connectionInfo) {
			this.pageContxt.setConnectionInfo(connectionInfo.getDatabaseConnectionInfo());
			if (null != ConnectionInfo.getConnectionInfoByName(connectionInfo.getName(), connectionsInfo)) {
				connectionsInfo.add(connectionInfo);
				settingsManager.setConnectionsInfo(connectionsInfo);
			}
		}
		dispose();
	}

	/**
	 *
	 * @return el ConnectionInfo creado o actualizado
	 */
	public ConnectionInfo createOrSaveConnectionInfo() {
		if (!validateFields())
			return null;
		ConnectionInfo connectionInfo = new ConnectionInfo();
		initializeConnectionInfoWithGUIFields(connectionInfo);
		return connectionInfo;
	}

	private void initializeConnectionInfoWithGUIFields(ConnectionInfo connectionInfo) {
		if (connectionInfo == null) {
			connectionInfo = new ConnectionInfo();
		}

		DatabaseConnectionInfo databaseConnectionInfo = connectionInfo.getDatabaseConnectionInfo();
		if (databaseConnectionInfo == null) {
			databaseConnectionInfo = new DatabaseConnectionInfo();
			connectionInfo.setDatabaseConnectionInfo(databaseConnectionInfo);
		}
		databaseConnectionInfo.setHost(t_ip.getText().trim());
		databaseConnectionInfo.setPort(t_port.getText().trim());
		databaseConnectionInfo.setUsername(t_username.getText().trim());
		databaseConnectionInfo.setPassword(new String(t_password.getPassword()));
		databaseConnectionInfo.setSid(t_database.getText().trim());
		databaseConnectionInfo.setConnectID(DatabaseConnectionInfo.CONNECTID[cb_id.getSelectedIndex()]);

		connectionInfo.setName(databaseConnectionInfo.toString());// name
	}

	public ConnectionInfo getCurrentConnectionInfo() {
		ConnectionInfo connectionInfo = new ConnectionInfo();
		initializeConnectionInfoWithGUIFields(connectionInfo);
		return connectionInfo;
	}

	public boolean validateFields() {
		boolean isValid = true;
		String message = "";

		String databasePassword = new String(t_password.getPassword());

		if (isTextEmpty(t_database.getText())) {
			isValid = false;
			message = "Debe ingresar un sid de base de datos";
			requestFocus(t_database);
		} else if (isTextEmpty(t_ip.getText())) {
			isValid = false;
			message = "Debe ingresar un host de base de datos";
			requestFocus(t_ip);
		} else if (isTextEmpty(t_port.getText())) {
			isValid = false;
			message = "Debe ingresar un puerto de base de datos";
			requestFocus(t_port);
		} else if (isTextEmpty(t_username.getText())) {
			isValid = false;
			message = "Debe ingresar un usuario de base de datos";
			requestFocus(t_username);
		} else if (isTextEmpty(databasePassword)) {
			isValid = false;
			message = "Debe ingresar un password de base de datos";
			requestFocus(t_password);
		}

		if (!isValid) {
			showErrorStatus(message);
		} else {
			showOkStatus("");
			clearFieldBorder();
		}
		return isValid;
	}

	private void requestFocus(JTextField text) {
		clearFieldBorder();
		text.requestFocus();
		text.setBorder(new LineBorder(Color.RED));
	}

	private void clearFieldBorder() {
		t_database.setBorder(new LineBorder(Color.GRAY));
		t_ip.setBorder(new LineBorder(Color.GRAY));
		t_port.setBorder(new LineBorder(Color.GRAY));
		t_username.setBorder(new LineBorder(Color.GRAY));
		t_password.setBorder(new LineBorder(Color.GRAY));
	}

	private void showErrorStatus(String text) {
		l_testresult.setText(text);
		l_testresult.setToolTipText(text);
		l_testresult.setForeground(Color.RED);
	}

	private void showOkStatus(String text) {
		l_testresult.setText(text);
		l_testresult.setToolTipText(text);
		l_testresult.setForeground(Color.DARK_GRAY);
	}

	private static boolean isTextEmpty(String text) {
		return StringHelper.isEmpty(text);
	}

	public void clearFields() {
		t_ip.setText("");
		t_port.setText("");
		t_password.setText("");
		t_database.setText("");
		t_username.setText("");
	}

	public void fillFields(DatabaseConnectionInfo info) {
		if (null != info) {
			t_ip.setText(info.getHost());
			t_port.setText(info.getPort());
			t_password.setText(info.getPassword());
			t_database.setText(info.getSid());
			t_username.setText(info.getUsername());
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
