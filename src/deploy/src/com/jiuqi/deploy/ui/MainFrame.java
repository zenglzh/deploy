package com.jiuqi.deploy.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.intf.IPropertyListener;
import com.jiuqi.deploy.server.ConfigDistEntity;
import com.jiuqi.deploy.server.ConfigEntity;
import com.jiuqi.deploy.server.ConfigInfoService;
import com.jiuqi.deploy.server.Contants;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.IOUtils;
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.SaveException;
import com.jiuqi.deploy.util.ShowMessage;
import com.jiuqi.deploy.util.StringHelper;

public class MainFrame {

	private static final String WINDOW_TIITLE = "DNA部署工具";
	private JFrame frmDna;
	private PageContext pageContext;
	private JTextField t_context;
	private JTextField t_dnaroot;
	private JTextArea t_startparam;
	private JTextField t_cluster_code;
	private JTextField t_cluster_num;
	private JTextArea t_node_ip;
	private JTextField t_hartbeat;
	private JTextField t_timeout;
	private JTextField t_ntlm_server;
	private JTextField t_ntlm_user;
	private JTextField t_ntlm_expdomain;
	private JPasswordField p_ntlm_password;
	private JTextField t_ldap_factory;
	private JTextField t_ldap_server;
	private JTextField t_ldap_domainname;
	private JTextField t_ldap_entryitem;
	private JCheckBox ck_cluster;
	private JCheckBox ck_distri;
	private JCheckBox ck_ntlm;
	private JCheckBox ck_ldap;
	private JComboBox cb_logontype;
	private ShowMessage showMessage;

	private boolean resized = true;
	private DistNodesTableWrapper distTableWrapper;
	private ConfigInfoService configService;

	public static void main(String[] args) {
		_launch();
	}

	/**
	 * Launch the application.
	 */
	private static void _launch() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frmDna.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void launch() {
		_launch();
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		this.pageContext = new PageContext();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDna = new JFrame();
		frmDna.setIconImage(ImageRes.getImage(ImageRes.PNG_BIRD));
		frmDna.setTitle(WINDOW_TIITLE);
		frmDna.setBounds(100, 100, 933, 779);
		frmDna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDna.setLocationRelativeTo(null);
		JMenuBar menuBar = new JMenuBar();
		frmDna.setJMenuBar(menuBar);
		JMenu menu_1 = new JMenu("\u5F00\u59CB");
		menuBar.add(menu_1);

		JMenuItem mi_exit = new JMenuItem("\u9000\u51FA");
		mi_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

		JMenuItem mi_save = new JMenuItem("\u4FDD\u5B58");
		mi_save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});
		menu_1.add(mi_save);

		JSeparator separator_4 = new JSeparator();
		menu_1.add(separator_4);

		JMenuItem mi_expwar = new JMenuItem("\u5BFC\u51FA WAR");
		mi_expwar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				WarExportDialog dialog = new WarExportDialog();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setTitle("导出 WAR");
				dialog.setModal(true);
				dialog.setLocationRelativeTo(frmDna);
				dialog.setVisible(true);
			}
		});
		
		JMenuItem mi_dbmonitor = new JMenuItem("\u6570\u636E\u5E93\u76D1\u63A7");
		mi_dbmonitor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ArchiveLogMonitorFrame.launch();
			}
		});
		menu_1.add(mi_dbmonitor);
		menu_1.add(mi_expwar);

		JSeparator separator_3 = new JSeparator();
		menu_1.add(separator_3);
		menu_1.add(mi_exit);

		JMenu menu_2 = new JMenu("\u6570\u636E\u5E93");
		menuBar.add(menu_2);

		JMenuItem mi_connect = new JMenuItem("\u8FDE\u63A5...");
		menu_2.add(mi_connect);

		JMenuItem mi_createUser = new JMenuItem("\u521B\u5EFA\u7528\u6237");
		menu_2.add(mi_createUser);
		mi_createUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pageContext.isDBValid()) {
					CreateUserDialog dialog = new CreateUserDialog(pageContext);
					dialog.setTitle("创建用户");
					dialog.setModal(true);
					dialog.setLocationRelativeTo(frmDna);
					dialog.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(frmDna, "请先连接数据库。", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JMenuItem mi_createTablespace = new JMenuItem("\u521B\u5EFA\u8868\u7A7A\u95F4");
		mi_createTablespace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (pageContext.isDBValid()) {
					CreateTablespaceDialog dialog = new CreateTablespaceDialog(pageContext);
					dialog.setTitle("创建表空间");
					dialog.setLocationRelativeTo(frmDna);
					dialog.setModal(true);
					dialog.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(frmDna, "请先连接数据库。", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JMenuItem mi_modifyuser = new JMenuItem("\u4FEE\u6539\u7528\u6237");
		mi_modifyuser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pageContext.isDBValid()) {
					EditUserDialog dialog = new EditUserDialog(pageContext);
					dialog.setTitle("修改用户");
					dialog.setModal(true);
					dialog.setLocationRelativeTo(frmDna);
					dialog.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(frmDna, "请先连接数据库。", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		menu_2.add(mi_modifyuser);
		menu_2.add(mi_createTablespace);

		JSeparator separator = new JSeparator();
		menu_2.add(separator);

		JMenuItem mi_backup = new JMenuItem("\u5907\u4EFD");
		mi_backup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DBExportDialog dialog = new DBExportDialog(pageContext);
				dialog.setTitle("数据库备份");
				dialog.setModal(true);
				dialog.setLocationRelativeTo(frmDna);
				dialog.setVisible(true);
			}
		});
		menu_2.add(mi_backup);

		JMenuItem mi_restore = new JMenuItem("\u6062\u590D");
		mi_restore.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DBImportDialog dialog = new DBImportDialog(pageContext);
				dialog.setTitle("数据库恢复");
				dialog.setModal(true);
				dialog.setLocationRelativeTo(frmDna);
				dialog.setVisible(true);
			}
		});
		menu_2.add(mi_restore);

		JMenu menu = new JMenu("\u5E2E\u52A9");
		menuBar.add(menu);

		JMenuItem mi_about = new JMenuItem("\u5173\u4E8E...");
		mi_about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog aboutDialog = new AboutDialog();
				aboutDialog.setLocationRelativeTo(frmDna);
				aboutDialog.setModal(true);
				aboutDialog.setVisible(true);
			}
		});
		menu.add(mi_about);

		JPanel panel_3 = new JPanel();
		menuBar.add(panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 403, 1, 0 };
		gbl_panel_3.rowHeights = new int[] { 1, 0 };
		gbl_panel_3.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		JLabel l_message = new JLabel("");
		l_message.setFont(new Font("宋体", Font.PLAIN, 12));
		showMessage = new ShowMessage(l_message);
		GridBagConstraints gbc_l_message = new GridBagConstraints();
		gbc_l_message.anchor = GridBagConstraints.NORTHWEST;
		gbc_l_message.gridx = 1;
		gbc_l_message.gridy = 0;
		panel_3.add(l_message, gbc_l_message);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 898, 0 };
		gridBagLayout.rowHeights = new int[] { 655, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		frmDna.getContentPane().setLayout(gridBagLayout);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		frmDna.getContentPane().add(panel_1, gbc_panel_1);
		panel_1.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(66dlu;default)"),
						FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("max(47dlu;default)"), FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("max(155dlu;default)"), },
				new RowSpec[] { FormSpecs.MIN_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(50dlu;default):grow"), FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(45dlu;default):grow"), FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(40dlu;default):grow"), FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JToolBar toolBar_2 = new JToolBar();
		panel_1.add(toolBar_2, "1, 1, 8, 1");
		toolBar_2.setFloatable(false);

		JButton b_save = new JButton("\u4FDD\u5B58");
		b_save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});
		b_save.setIcon(ImageRes.getIcon(ImageRes.PNG_SAVE));
		toolBar_2.add(b_save);

		JButton b_connect = new JButton("\u8FDE\u63A5\u6570\u636E\u5E93");
		b_connect.setIcon(ImageRes.getIcon(ImageRes.PNG_CONNECT));
		b_connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DBConnectDialog connect = new DBConnectDialog(pageContext);
				connect.setModal(true);
				connect.setLocationRelativeTo(frmDna);
				connect.setVisible(true);
			}
		});
		toolBar_2.add(b_connect);

		JButton b_exit = new JButton("\u9000\u51FA");
		b_exit.setIcon(ImageRes.getIcon(ImageRes.PNG_CLOSE));
		b_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

		JButton b_more = new JButton("\u9AD8\u7EA7");
		b_more.setIcon(ImageRes.getIcon(ImageRes.PNG_ADVANCED));
		b_more.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resize();
			}
		});
		toolBar_2.add(b_more);
		toolBar_2.add(b_exit);

		JLabel lblNewLabel = new JLabel("\u914D\u7F6EDNA\u4E0A\u4E0B\u6587\u8DEF\u5F84");
		panel_1.add(lblNewLabel, "2, 3, right, default");

		t_context = new JTextField();
		t_context.setToolTipText(
				"DNA\u4E0A\u4E0B\u6587\u8DEF\u5F84\u3002\u5FC5\u987B\u548C\u4E2D\u95F4\u4EF6\u914D\u7F6E\u7684\u6570\u636E\u6E90\u540D\u79F0\u76F8\u540C");
		panel_1.add(t_context, "4, 3, fill, default");
		t_context.setColumns(10);
		final JButton b_conf_context = new JButton("\u914D\u7F6E");
		b_conf_context.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (initDNAContext(t_context.getText())) {
					ConfigEntity configEntity = pageContext.getConfigEntity();
					if (null != configEntity) {
						fillField(configEntity);
					}
					showOkStatus("OK!");
					b_conf_context.setEnabled(false);
				}
			}
		});
		t_context.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				ConfigEntity configEntity = pageContext.getConfigEntity();
				if (null != configEntity) {
					if (null != t_context.getText() && !t_context.getText().equals(configEntity.getContextPath())) {
						b_conf_context.setEnabled(true);
					}
				}
			}
		});
		panel_1.add(b_conf_context, "6, 3");

		JLabel l_tip = new JLabel("");
		panel_1.add(l_tip, "8, 3");

		JLabel lblNewLabel_1 = new JLabel("\u53C2\u6570\u914D\u7F6E");
		lblNewLabel_1.setFont(new Font("宋体", Font.BOLD, 12));
		panel_1.add(lblNewLabel_1, "2, 5");

		JLabel lblNewLabel_2 = new JLabel("DNA\u6839\u76EE\u5F55");
		panel_1.add(lblNewLabel_2, "2, 7, right, default");

		t_dnaroot = new JTextField();
		t_dnaroot.setToolTipText(
				"\u4E00\u5B9A\u8981\u586B\u5199\u5DF2\u7ECF\u5B58\u5728\u7684\u800C\u4E14DNA\u6709\u5199\u6743\u9650\u7684\u76EE\u5F55\u3002\u548C\u4E2D\u95F4\u4EF6\u5728\u540C\u4E00\u4E2A\u670D\u52A1\u5668\u4E0A");
		panel_1.add(t_dnaroot, "4, 7, fill, default");
		t_dnaroot.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("\u542F\u52A8\u53C2\u6570");
		panel_1.add(lblNewLabel_3, "2, 9, right, default");

		JScrollPane spane_param = new JScrollPane();
		spane_param.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panel_1.add(spane_param, "4, 9, fill, fill");
		JPanel panel = new JPanel();
		spane_param.setViewportView(panel);
		t_startparam = new JTextArea();
		t_startparam.setToolTipText(
				"\u586B\u5199DNA\u7684\u542F\u52A8\u53C2\u6570(\u76EE\u524D\u53EA\u652F\u6301-D\u7684\u53C2\u6570)");
		t_startparam.setLineWrap(true);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		panel.add(t_startparam);

		JLabel label_3 = new JLabel("\u4F1A\u8BDD\u914D\u7F6E");
		label_3.setFont(new Font("宋体", Font.BOLD, 12));
		panel_1.add(label_3, "2, 11");

		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4, "4, 11, fill, fill");
		panel_4.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("80px"),
						FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, ColumnSpec.decode("66px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("80px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("66px"), },
				new RowSpec[] { FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("21px"), }));

		JLabel lblNewLabel_19 = new JLabel("\u5FC3\u8DF3\u65F6\u95F4");
		panel_4.add(lblNewLabel_19, "1, 2, right, default");

		t_hartbeat = new JTextField();
		t_hartbeat.setToolTipText(
				"\u5FC3\u8DF3\u65F6\u95F4\u662F\u6307\u6BCF\u9694\u4E00\u5B9A\u65F6\u95F4\u68C0\u67E5\u4E00\u4E0B\u4F1A\u8BDD\u662F\u5426\u8FC7\u671F\uFF0C\u5B83\u4E0E\u4F1A\u8BDD\u8D85\u65F6\u65F6\u95F4\u4E00\u8D77\u51B3\u5B9A\u7740\u5F53\u524D\u4F1A\u8BDD\u7684\u8FC7\u671F\u60C5\u51B5\u3002\u8981\u6C42\uFF0C\u5FC3\u8DF3\u65F6\u95F4\u8981\u5C0F\u4E8E\u4F1A\u8BDD\u8D85\u65F6\u65F6\u95F4\u3002");
		panel_4.add(t_hartbeat, "3, 2, left, top");
		t_hartbeat.setColumns(10);

		JLabel lblNewLabel_18 = new JLabel("\u4F1A\u8BDD\u8FC7\u671F");
		panel_4.add(lblNewLabel_18, "6, 2, right, default");

		t_timeout = new JTextField();
		t_timeout.setToolTipText(
				"\u4F1A\u8BDD\u8FC7\u671F\u987E\u540D\u601D\u4E49\u662F\u8D85\u8FC7\u7279\u5B9A\u7684\u65F6\u95F4\uFF0C\u4E3A\u6B64\u7CFB\u7EDF\u6216\u8F6F\u4EF6\u81EA\u52A8\u8BA4\u4E3A\u662F\u653E\u5F03\u5904\u7406\uFF0C\u82E5\u662F\u91CD\u65B0\u542F\u7528\u4F1A\u663E\u793A\u201C\u4F1A\u8BDD\u8FC7\u671F\u201D\uFF0C\u53EA\u6709\u653E\u5F03\u91CD\u65B0\u8BA4\u9886\u7533\u8BF7\uFF0C\u8FDB\u5165\u4E0B\u4E00\u4E2A\u4F1A\u8BDD\u3002");
		panel_4.add(t_timeout, "8, 2, left, top");
		t_timeout.setColumns(10);

		JSeparator separator_1 = new JSeparator();
		panel_1.add(separator_1, "2, 13, 7, 1");

		JLabel label = new JLabel("\u96C6\u7FA4\u914D\u7F6E");
		label.setFont(new Font("宋体", Font.BOLD, 12));
		panel_1.add(label, "2, 15");

		ck_cluster = new JCheckBox("\u542F\u7528");
		panel_1.add(ck_cluster, "4, 15, left, default");

		JLabel lblNewLabel_4 = new JLabel("\u96C6\u7FA4\u6807\u8BC6");
		panel_1.add(lblNewLabel_4, "2, 17, right, default");

		t_cluster_code = new JTextField();
		t_cluster_code.setToolTipText(
				"\u914D\u7F6E\u96C6\u7FA4\u7684\u6807\u8BC6\u4FE1\u606F\uFF0C\u53EA\u80FD\u7531\u5B57\u6BCD\u6216\u6570\u5B57\u7EC4\u6210");
		t_cluster_code.setText("");
		panel_1.add(t_cluster_code, "4, 17, fill, default");
		t_cluster_code.setColumns(10);
		t_cluster_code.setEnabled(ck_cluster.isSelected());
		JLabel lblNewLabel_5 = new JLabel("\u96C6\u7FA4\u5E8F\u53F7");
		panel_1.add(lblNewLabel_5, "6, 17, right, default");

		t_cluster_num = new JTextField();
		t_cluster_num.setToolTipText("\u96C6\u7FA4\u73AF\u5883\u4E0B\u5F53\u524D\u8282\u70B9\u7684\u5E8F\u53F7");
		panel_1.add(t_cluster_num, "8, 17, fill, default");
		t_cluster_num.setColumns(10);
		t_cluster_num.setEnabled(ck_cluster.isSelected());
		JLabel lblNewLabel_6 = new JLabel("\u8282\u70B9\u5730\u5740");
		panel_1.add(lblNewLabel_6, "2, 19, right, default");

		t_node_ip = new JTextArea();
		t_node_ip.setToolTipText(
				"\u540C\u4E00\u96C6\u7FA4\u4E0B\u672C\u8282\u70B9\u548C\u5176\u4ED6\u96C6\u7FA4\u8282\u70B9\u7684\u5730\u5740\u4FE1\u606F");
		panel_1.add(t_node_ip, "4, 19, fill, fill");
		t_node_ip.setEnabled(ck_cluster.isSelected());
		ck_cluster.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				t_cluster_code.setEnabled(ck_cluster.isSelected());
				t_cluster_num.setEnabled(ck_cluster.isSelected());
				t_node_ip.setEnabled(ck_cluster.isSelected());
			}
		});
		JLabel label_2 = new JLabel("\u5206\u5E03\u5F0F\u914D\u7F6E");
		label_2.setFont(new Font("宋体", Font.BOLD, 12));
		panel_1.add(label_2, "2, 21");

		JToolBar toolBar_1 = new JToolBar();
		toolBar_1.setFloatable(false);
		panel_1.add(toolBar_1, "4, 21");

		ck_distri = new JCheckBox("\u542F\u7528");
		toolBar_1.add(ck_distri);
		this.distTableWrapper = new DistNodesTableWrapper();
		final JButton b_new = new JButton("\u65B0\u589E");
		b_new.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DistributedConfigDialog dialog = new DistributedConfigDialog(-1, distTableWrapper);
				dialog.setModal(true);
				dialog.setLocationRelativeTo(frmDna);
				dialog.setVisible(true);

			}
		});
		toolBar_1.add(b_new);
		b_new.setEnabled(ck_distri.isSelected());
		final JButton b_modify = new JButton("\u4FEE\u6539");
		toolBar_1.add(b_modify);
		b_modify.setEnabled(ck_distri.isSelected());
		final JButton b_delete = new JButton("\u5220\u9664");
		toolBar_1.add(b_delete);
		b_delete.setEnabled(ck_distri.isSelected());
		JLabel label_1 = new JLabel("\u8282\u70B9\u4FE1\u606F");
		panel_1.add(label_1, "2, 23, right, default");
		final JTable table_node = distTableWrapper.getTable();
		JScrollPane spane_table = new JScrollPane(table_node);
		table_node.setEnabled(ck_distri.isSelected());
		panel_1.add(spane_table, "4, 23, 5, 1, fill, fill");
		ck_distri.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				b_new.setEnabled(ck_distri.isSelected());
				b_modify.setEnabled(ck_distri.isSelected());
				b_delete.setEnabled(ck_distri.isSelected());
				table_node.setEnabled(ck_distri.isSelected());
			}
		});
		b_modify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (-1 < table_node.getSelectedRow()) {
					DistributedConfigDialog dialog = new DistributedConfigDialog(table_node.getSelectedRow(),
							distTableWrapper);
					dialog.setModal(true);
					dialog.setLocationRelativeTo(frmDna);
					dialog.setVisible(true);
				}
			}
		});
		b_delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (-1 < table_node.getSelectedRow()) {
					distTableWrapper.deleteRow(table_node.getSelectedRow());
				}
			}
		});
		///////////////
		JLabel lblNewLabel_12 = new JLabel("NTLM\u914D\u7F6E");
		lblNewLabel_12.setFont(new Font("宋体", Font.BOLD, 12));
		panel_1.add(lblNewLabel_12, "2, 25");

		ck_ntlm = new JCheckBox("\u542F\u7528");
		panel_1.add(ck_ntlm, "4, 25");

		JLabel lblNewLabel_14 = new JLabel("\u57DF\u670D\u52A1\u5668\u5730\u5740");
		panel_1.add(lblNewLabel_14, "2, 27, right, default");

		t_ntlm_server = new JTextField();
		t_ntlm_server.setEnabled(ck_ntlm.isSelected());
		panel_1.add(t_ntlm_server, "4, 27, fill, default");
		t_ntlm_server.setColumns(10);

		JLabel label_6 = new JLabel("\u4E0D\u91C7\u7528\u57DF\u9A8C\u8BC1\u5165\u53E3");
		panel_1.add(label_6, "6, 27, right, default");

		t_ntlm_expdomain = new JTextField();
		t_ntlm_expdomain.setEnabled(ck_ntlm.isSelected());
		panel_1.add(t_ntlm_expdomain, "8, 27, fill, default");
		t_ntlm_expdomain.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel("\u7528\u6237\u540D");
		panel_1.add(lblNewLabel_9, "2, 29, right, default");

		t_ntlm_user = new JTextField();
		panel_1.add(t_ntlm_user, "4, 29, fill, default");
		t_ntlm_user.setEnabled(ck_ntlm.isSelected());
		t_ntlm_user.setColumns(10);

		JLabel lblNewLabel_10 = new JLabel("\u5BC6\u7801");
		panel_1.add(lblNewLabel_10, "6, 29, right, default");

		p_ntlm_password = new JPasswordField();
		p_ntlm_password.setEnabled(ck_ntlm.isSelected());
		panel_1.add(p_ntlm_password, "8, 29, fill, default");

		ck_ntlm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				t_ntlm_server.setEnabled(ck_ntlm.isSelected());
				t_ntlm_expdomain.setEnabled(ck_ntlm.isSelected());
				t_ntlm_user.setEnabled(ck_ntlm.isSelected());
				p_ntlm_password.setEnabled(ck_ntlm.isSelected());
			}
		});

		JLabel lblNewLabel_8 = new JLabel("LDAP\u914D\u7F6E");
		lblNewLabel_8.setFont(new Font("宋体", Font.BOLD, 12));
		panel_1.add(lblNewLabel_8, "2, 31");

		ck_ldap = new JCheckBox("\u542F\u7528");
		panel_1.add(ck_ldap, "4, 31");

		JLabel lblNewLabel_15 = new JLabel("LDAP\u5904\u7406\u5DE5\u5382");
		panel_1.add(lblNewLabel_15, "2, 33, right, default");

		t_ldap_factory = new JTextField();
		panel_1.add(t_ldap_factory, "4, 33, fill, default");
		t_ldap_factory.setEnabled(ck_ldap.isSelected());
		t_ldap_factory.setColumns(10);

		JLabel lblNewLabel_16 = new JLabel("LDAP\u9A8C\u8BC1\u670D\u52A1\u5668\u5730\u5740");
		panel_1.add(lblNewLabel_16, "6, 33, right, default");

		t_ldap_server = new JTextField();
		panel_1.add(t_ldap_server, "8, 33, fill, default");
		t_ldap_server.setEnabled(ck_ldap.isSelected());
		t_ldap_server.setColumns(10);

		JLabel lblNewLabel_11 = new JLabel("\u57DF\u540D");
		panel_1.add(lblNewLabel_11, "2, 35, right, default");

		t_ldap_domainname = new JTextField();
		t_ldap_domainname.setEnabled(ck_ldap.isSelected());
		panel_1.add(t_ldap_domainname, "4, 35, fill, default");
		t_ldap_domainname.setColumns(10);

		JLabel lblNewLabel_13 = new JLabel("\u767B\u5F55\u7C7B\u578B");
		panel_1.add(lblNewLabel_13, "6, 35, right, default");

		cb_logontype = new JComboBox(Contants.LDAP_LOGIN_TYPE);
		cb_logontype.setEnabled(ck_ldap.isSelected());
		panel_1.add(cb_logontype, "8, 35, fill, default");

		JLabel lblNewLabel_17 = new JLabel("\u5165\u53E3\u9879");
		panel_1.add(lblNewLabel_17, "2, 37, right, default");

		t_ldap_entryitem = new JTextField();
		t_ldap_entryitem.setEnabled(ck_ldap.isSelected());
		panel_1.add(t_ldap_entryitem, "4, 37, fill, default");
		t_ldap_entryitem.setColumns(10);

		ck_ldap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				t_ldap_factory.setEnabled(ck_ldap.isSelected());
				t_ldap_server.setEnabled(ck_ldap.isSelected());
				t_ldap_domainname.setEnabled(ck_ldap.isSelected());
				cb_logontype.setEnabled(ck_ldap.isSelected());
				t_ldap_entryitem.setEnabled(ck_ldap.isSelected());
			}
		});

		JSeparator separator_2 = new JSeparator();
		panel_1.add(separator_2, "2, 39, 7, 1");
		panel_1.setSize(600, 400);
		mi_connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DBConnectDialog connect = new DBConnectDialog(pageContext);
				connect.setLocationRelativeTo(frmDna);
				connect.setModal(true);
				connect.setVisible(true);
			}
		});
		pageContext.addPropertyListener(new IPropertyListener() {

			@Override
			public void propertyChanged(Object source, int propId) {
				if (propId == DatabaseConnectionInfo.DBConnectPro) {
					shouConnectStatus();
				}
			}
		});
		shouConnectStatus();
		resize();
	}

	protected void resize() {
		if (resized) {
			frmDna.setSize(666, 312);
		} else {
			frmDna.setSize(930, 798);
		}
		resized = !resized;
		frmDna.setResizable(resized);
	}

	private void showErrorStatus(String text) {
		showMessage.wshow(text, Color.RED);
	}

	private void showOkStatus(String text) {
		showMessage.wshow(text, Color.DARK_GRAY);
	}

	private boolean initDNAContext(String contextPath) {
		if (null == pageContext.getConnectionInfo()) {
			showErrorStatus("请先连接数据库！");
			return false;
		}
		if (StringHelper.isEmpty(contextPath)) {
			showErrorStatus("请先配置DNA上下文路径！");
			return false;
		}
		if (pageContext.getConnectionInfo().isDBA()) {
			showErrorStatus("不能使用 SYSDBA 身份配置，请使用 Normal 身份连接数据库！");
			return false;
		}
		ESBDBClient esbdbClient = new ESBDBClient(pageContext.getConnectionInfo());
		try {
			esbdbClient.testConnect();
		} catch (Exception e1) {
			showErrorStatus("数据库连接发生异常！");
			return false;
		}
		try {
			configService = new ConfigInfoService(esbdbClient, getLegalBDName(contextPath));
			configService.openConnect();
			if (configService.checkConfigTableExists()) {
				configService.dropConfigTable();
				configService.createConfigTable();
			}
			ConfigEntity readConfigEntity = configService.readConfigEntity(contextPath);
			readConfigEntity.setContextPath(contextPath);
			pageContext.setConfigEntity(readConfigEntity);
			configService.closeConnect();
		} catch (Exception e) {
			showErrorStatus(e.getMessage());
			return false;
		}
		return true;
	}

	private void initCheckBox(JCheckBox cbBox, String enableStr) {
		boolean enabled = Contants.ATTRIBUTE_ENABLE.equals(enableStr) || "true".equals(enableStr);
		if (cbBox.isSelected() != enabled) {
			cbBox.doClick();
		}
		cbBox.setSelected(enabled);
	}

	private void fillField(ConfigEntity configEntity) {
		t_dnaroot.setText(configEntity.getRootPath());
		t_startparam.setText(configEntity.getSetupParam());
		initCheckBox(ck_cluster, configEntity.getClusterEnable());
		t_cluster_code.setText(configEntity.getClusterID());
		t_cluster_num.setText(configEntity.getClusterIndex());
		t_node_ip.setText(configEntity.getNodesUrl());
		initCheckBox(ck_distri, configEntity.getDistEnable());
		distTableWrapper.removeAll();
		List<ConfigDistEntity> distNodes = configEntity.getDistNodes();
		if (null != distNodes) {
			for (ConfigDistEntity configDistEntity : distNodes) {
				distTableWrapper.addRow(configDistEntity);
			}
		}
		t_hartbeat.setText(configEntity.getSessionHeartBeat());
		t_timeout.setText(configEntity.getSessionTimeout());
		initCheckBox(ck_ntlm, configEntity.getNtlmEnable());
		t_ntlm_server.setText(configEntity.getDomainController());
		t_ntlm_expdomain.setText(configEntity.getExcludeEntrys());
		t_ntlm_user.setText(configEntity.getNtlmUser());
		p_ntlm_password.setText(configEntity.getNtlmPwd());
		initCheckBox(ck_ldap, configEntity.getLdapEnable());
		t_ldap_factory.setText(configEntity.getFactoryInitial());
		t_ldap_domainname.setText(configEntity.getLdapDomain());
		t_ldap_entryitem.setText(configEntity.getIncludeEntrys());
		t_ldap_server.setText(configEntity.getProviderUrl());
		cb_logontype.setSelectedItem(configEntity.getAuthentication());
	}

	private ConfigEntity getEntity() {
		ConfigEntity entity = new ConfigEntity();
		entity.setSessionHeartBeat(t_hartbeat.getText());
		entity.setSessionTimeout(t_timeout.getText());
		entity.setNtlmEnable(StringHelper.getEnableStr(ck_ntlm.isSelected()));
		entity.setDomainController(t_ntlm_server.getText());
		entity.setNtlmUser(t_ntlm_user.getText());
		entity.setNtlmPwd(new String(p_ntlm_password.getPassword()));
		entity.setExcludeEntrys(t_ntlm_expdomain.getText());
		entity.setLdapEnable(StringHelper.getEnableStr(ck_ldap.isSelected()));
		entity.setFactoryInitial(t_ldap_factory.getText());
		entity.setProviderUrl(t_ldap_server.getText());
		entity.setAuthentication(String.valueOf(cb_logontype.getSelectedItem()));
		entity.setLdapDomain(t_ldap_domainname.getText());
		entity.setIncludeEntrys(t_ldap_entryitem.getText());
		entity.setSetupParam(t_startparam.getText().trim());
		entity.setRootPath(t_dnaroot.getText().trim());
		entity.setContextPath(t_context.getText());
		entity.setClusterID(t_cluster_code.getText());
		entity.setClusterIndex(t_cluster_num.getText());
		entity.setClusterEnable(StringHelper.getEnableStr(ck_cluster.isSelected()));
		entity.setNodes(StringHelper.getUrlList(t_node_ip.getText()));
		entity.setDistEnable(StringHelper.getEnableStr(ck_distri.isSelected()));
		// table.
		entity.setDistNodes(distTableWrapper.getAll());
		return entity;
	}

	private String getLegalBDName(String contextPath) {
		String s = contextPath;
		if (contextPath == null || "/".equals(contextPath)) {
			s = "";
		}
		return Contants.DB_PREFIX + "_" + s;
	}

	private void doSave() {
		if (null == configService && !initDNAContext(t_context.getText())) {
			showErrorStatus("保存失败！");
			JOptionPane.showMessageDialog(frmDna, "保存失败！" + "请先配置DNA上下文路径或DNA上下文路径配置错误！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!IOUtils.workDirIsOk(t_dnaroot.getText())) {
			int showConfirmDialog = JOptionPane.showConfirmDialog(frmDna,
					"DNA工作目录在当前机器上不存在或为只读！\nDNA工作目录和应用中间件必须在同一个机器上。\n如果确认工作目录没有问题可以选择【是】。",
					UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION);
			if (showConfirmDialog != JOptionPane.YES_OPTION) {
				return;
			}
		}
		try {
			save();
			JOptionPane.showMessageDialog(frmDna, "保存成功！", "提示", JOptionPane.NO_OPTION);
			showOkStatus("保存成功！");
		} catch (SaveException e) {
			showErrorStatus("保存失败！");
			JOptionPane.showMessageDialog(frmDna, "保存失败！" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void save() throws SaveException {
		ConfigEntity configEntity = getEntity();
		try {
			configService.openConnect();
			configService.storeConfigEntity(configEntity);// 保存dna-server.xml配置信息
			configService.storeDistConfigEntity(configEntity);
			configService.closeConnect();
		} catch (Exception e) {
			throw new SaveException(e.getMessage());
		} // 保存distributed.xml配
	}

	public void shouConnectStatus() {
		if (null != pageContext.getConnectionInfo()) {
			setFrameStatus(pageContext.getConnectionInfo().toString());
		} else {
			setFrameStatus("未连接数据库");
		}
	}

	private void setFrameStatus(String status) {
		frmDna.setTitle(WINDOW_TIITLE + " - " + status);
	}

	private void exit() {
		System.exit(1);
	}

}
