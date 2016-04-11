package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.db.OracleTablespaceProperties;
import com.jiuqi.deploy.util.ImageRes;
import com.jiuqi.deploy.util.StringHelper;

public class CreateTablespaceDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private PageContext pageContext;
	private JTextField t_tablespace_name;
	private JTextField t_maxfile_size;
	private JTextField t_store_extend_size;
	private JTextField t_data_size;
	private JTextField t_data_name;
	private JTextField t_data_dir;

	private JLabel l_tip;

	private JComboBox cb_data_size;

	private JComboBox cb_extend_size;

	private JComboBox cb_maxfile_size;

	private JCheckBox ch_autoextend;

	private JCheckBox ch_reuse;

	private JRadioButton rd_unlimit;
	public static final String[] FILESIZEUNIT = { "K", "M", "G", "T" };
	private final static String MATCH_TBS_NAME = "(tablespace)[ ]+[^ ]+";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CreateTablespaceDialog dialog = new CreateTablespaceDialog(new PageContext());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CreateTablespaceDialog(PageContext pageContext) {
		setIconImage(ImageRes.getImage(ImageRes.PNG_TABLESPACE));
		this.pageContext = pageContext;
		setBounds(100, 100, 539, 392);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(164dlu;default):grow"),
						FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(5dlu;default):grow"), FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));
		{
			JLabel label = new JLabel("\u8868\u7A7A\u95F4");
			label.setFont(new Font("宋体", Font.BOLD, 12));
			contentPanel.add(label, "2, 2");
		}
		{
			l_tip = new JLabel("");
			l_tip.setHorizontalAlignment(SwingConstants.RIGHT);
			contentPanel.add(l_tip, "4, 2");
		}
		{
			JLabel label = new JLabel("\u8868\u7A7A\u95F4\u540D\u5B57");
			contentPanel.add(label, "2, 4, right, default");
		}
		{
			t_tablespace_name = new JTextField();
			contentPanel.add(t_tablespace_name, "4, 4, fill, top");
			t_tablespace_name.setColumns(10);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("*");
			contentPanel.add(lblNewLabel_1, "6, 4");
		}
		{
			JLabel label = new JLabel("\u6570\u636E\u6587\u4EF6");
			label.setFont(new Font("宋体", Font.BOLD, 12));
			contentPanel.add(label, "2, 6");
		}
		{
			JLabel label = new JLabel("\u6570\u636E\u6587\u4EF6\u540D");
			contentPanel.add(label, "2, 8, right, default");
		}
		{
			t_data_name = new JTextField();
			contentPanel.add(t_data_name, "4, 8, fill, default");
			t_data_name.setColumns(10);
		}
		{
			JLabel lblNewLabel_3 = new JLabel("*");
			contentPanel.add(lblNewLabel_3, "6, 8");
		}
		{
			JLabel lblNewLabel = new JLabel("\u6570\u636E\u6587\u4EF6\u76EE\u5F55");
			contentPanel.add(lblNewLabel, "2, 10, right, default");
		}
		{
			t_data_dir = new JTextField();
			contentPanel.add(t_data_dir, "4, 10, fill, default");
			t_data_dir.setColumns(10);
		}
		{
			JLabel label = new JLabel("*");
			contentPanel.add(label, "6, 10");
		}
		{
			JLabel lblmb = new JLabel("\u6587\u4EF6\u5927\u5C0F");
			contentPanel.add(lblmb, "2, 12, right, default");
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, "4, 12, left, fill");
			{
				t_data_size = new JTextField();
				t_data_size.setText("100");
				t_data_size.addKeyListener(new VoteElectKeyListener());
				panel.add(t_data_size);
				t_data_size.setColumns(10);
			}
			{
				cb_data_size = new JComboBox(FILESIZEUNIT);
				cb_data_size.setSelectedIndex(1);
				panel.add(cb_data_size);
			}
			{
				ch_reuse = new JCheckBox("\u91CD\u7528\u73B0\u6709\u6587\u4EF6");
				panel.add(ch_reuse);
			}
		}
		{
			JLabel label = new JLabel("\u5B58\u50A8");
			label.setFont(new Font("宋体", Font.BOLD, 12));
			contentPanel.add(label, "2, 14");
		}
		{
			ch_autoextend = new JCheckBox(
					"\u6570\u636E\u6587\u4EF6\u6162\u540E\u81EA\u52A8\u6269\u5C55\uFF08AUTOEXTEND\uFF09");
			contentPanel.add(ch_autoextend, "4, 14");
		}
		{
			JLabel label = new JLabel("\u589E\u91CF");
			contentPanel.add(label, "2, 16, right, default");
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, "4, 16, left, fill");
			{
				t_store_extend_size = new JTextField();
				t_store_extend_size.addKeyListener(new VoteElectKeyListener());
				panel.add(t_store_extend_size);
				t_store_extend_size.setColumns(10);
			}
			{
				cb_extend_size = new JComboBox(FILESIZEUNIT);
				panel.add(cb_extend_size);
			}
		}
		{
			JLabel lblNewLabel_2 = new JLabel("\u6700\u5927\u6587\u4EF6\u5927\u5C0F");
			lblNewLabel_2.setVerticalAlignment(SwingConstants.BOTTOM);
			contentPanel.add(lblNewLabel_2, "2, 18, right, default");
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, "4, 18, left, fill");
			{
				ButtonGroup bgroup1 = new ButtonGroup();
				rd_unlimit = new JRadioButton("\u65E0\u9650\u5236");
				rd_unlimit.setSelected(true);
				panel.add(rd_unlimit);
				JRadioButton rd_value = new JRadioButton("\u503C");
				panel.add(rd_value);
				bgroup1.add(rd_unlimit);
				bgroup1.add(rd_value);
			}
			{
				t_maxfile_size = new JTextField();
				t_maxfile_size.addKeyListener(new VoteElectKeyListener());
				panel.add(t_maxfile_size);
				t_maxfile_size.setColumns(10);
			}
			{
				cb_maxfile_size = new JComboBox(FILESIZEUNIT);
				cb_maxfile_size.setSelectedIndex(1);
				panel.add(cb_maxfile_size);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u786E\u5B9A");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (validateFields()) {
							try {
								createTablespace();
								int showConfirmDialog = JOptionPane.showOptionDialog(CreateTablespaceDialog.this,
										"创建表空间成功！", "提示", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
										null, null, null);
								if (showConfirmDialog == JOptionPane.YES_OPTION) {
									CreateTablespaceDialog.this.dispose();
								}
							} catch (ClassNotFoundException e1) {
								JOptionPane.showMessageDialog(null, "数据库驱动未找到！", "错误", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (SQLException e1) {
								JOptionPane.showMessageDialog(null, "创建表空间失败！" + e1.getMessage(), "错误",
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
				JButton cancelButton = new JButton("\u53D6\u6D88");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						CreateTablespaceDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public boolean validateFields() {
		boolean isValid = true;
		String message = "";
		if (StringHelper.isEmpty(t_tablespace_name.getText())) {
			isValid = false;
			message = "表空间名字不能为空！";
			requestFocus(t_tablespace_name);
		} else if (StringHelper.isEmpty(t_data_name.getText())) {
			isValid = false;
			message = "数据文件名不能为空！";
			requestFocus(t_data_name);
		} else if (!checkDataDir(t_data_dir.getText())) {
			isValid = false;
			message = "数据文件目录不合法！";
			requestFocus(t_data_dir);
		}
		if (!isValid) {
			showErrorStatus(message);
		}
		showOkStatus("");
		return isValid;
	}

	private boolean checkDataDir(String datafiledir) {
		if (StringHelper.isEmpty(datafiledir)) {
			return false;
		}
		return true;
	}

	private void requestFocus(JTextField text) {
		clearFieldBorder();
		text.requestFocus();
		text.setBorder(new LineBorder(Color.RED));
	}

	private void clearFieldBorder() {
		t_tablespace_name.setBorder(new LineBorder(Color.GRAY));
		t_data_name.setBorder(new LineBorder(Color.GRAY));
		t_data_dir.setBorder(new LineBorder(Color.GRAY));
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

	private void createTablespace() throws ClassNotFoundException, SQLException {
		OracleTablespaceProperties properties = new OracleTablespaceProperties();
		properties.setTablespaceName(t_tablespace_name.getText());
		properties.setDataFileName(t_data_name.getText());
		properties.setDataFileDirectory(t_data_dir.getText());
		properties.setFileSize(t_data_size.getText() + FILESIZEUNIT[cb_data_size.getSelectedIndex()]);
		properties.setReuse(ch_reuse.isSelected());
		properties.setAutoExtend(ch_autoextend.isSelected());
		properties.setAutoExtendSize(t_store_extend_size.getText() + FILESIZEUNIT[cb_extend_size.getSelectedIndex()]);
		properties.setUnlimited(rd_unlimit.isSelected());
		properties.setAutoExtendMaxSize(t_maxfile_size.getText() + FILESIZEUNIT[cb_maxfile_size.getSelectedIndex()]);
		ESBDBClient client = new ESBDBClient(pageContext.getConnectionInfo());
		client.createTablespace(properties);

	}

	class VoteElectKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			int keyChar = e.getKeyChar();
			if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {
			} else {
				e.consume();
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

	}

}
