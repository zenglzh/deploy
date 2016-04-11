package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jiuqi.deploy.intf.IAddEntitiy;
import com.jiuqi.deploy.intf.IDistNodeCallback;
import com.jiuqi.deploy.server.ConfigDistEntity;
import com.jiuqi.deploy.server.Contants;
import com.jiuqi.deploy.server.DistNodeType;
import com.jiuqi.deploy.util.StringHelper;

public class DistributedConfigDialog extends JDialog {

	private static final long serialVersionUID = 5825304370439506907L;
	private final JPanel contentPanel = new JPanel();
	private JTextField t_code;
	private JTextField t_ip;
	private JTextField t_port;
	private JTextField t_context;
	private JTextField t_dispatchrule;
	private JTextField t_colony;
	private JComboBox cb_nodetye;
	private JCheckBox ck_currnode;
	private IAddEntitiy<ConfigDistEntity> callback;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DistributedConfigDialog dialog = new DistributedConfigDialog(-2, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DistributedConfigDialog(final int rownum, final IDistNodeCallback callback) {
		this.callback = callback;
		setBounds(100, 100, 399, 337);
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
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, }));
		{
			JLabel lblNewLabel = new JLabel("\u5F53\u524D\u8282\u70B9");
			contentPanel.add(lblNewLabel, "2, 2");
		}
		{
			ck_currnode = new JCheckBox("\u662F");
			contentPanel.add(ck_currnode, "4, 2");
		}
		{
			JLabel lblNewLabel_1 = new JLabel("");
			contentPanel.add(lblNewLabel_1, "6, 2");
		}
		{
			JLabel lblNewLabel_2 = new JLabel("\u8282\u70B9\u7C7B\u578B");
			contentPanel.add(lblNewLabel_2, "2, 4, right, default");
		}
		{
			cb_nodetye = new JComboBox();
			cb_nodetye.setToolTipText("\u5206\u5E03\u5F0F\u4E0B\u8282\u70B9\u7684\u7C7B\u578B");
			contentPanel.add(cb_nodetye, "4, 4, fill, default");
		}
		{
			JLabel lblNewLabel_3 = new JLabel("\u8282\u70B9\u6807\u8BC6");
			contentPanel.add(lblNewLabel_3, "2, 6, right, default");
		}
		{
			t_code = new JTextField();
			t_code.setToolTipText("\u5206\u5E03\u5F0F\u4E0B\u5404\u4E2A\u8282\u70B9\u7684\u6807\u8BC6");
			contentPanel.add(t_code, "4, 6, fill, default");
			t_code.setColumns(10);
		}
		{
			JLabel lblNewLabel_4 = new JLabel("\u8282\u70B9\u5730\u5740");
			contentPanel.add(lblNewLabel_4, "2, 8, right, default");
		}
		{
			t_ip = new JTextField();
			t_ip.setToolTipText("\u8282\u70B9\u7684IP\u5730\u5740");
			contentPanel.add(t_ip, "4, 8, fill, default");
			t_ip.setColumns(10);
		}
		{
			JLabel lblNewLabel_5 = new JLabel("\u8282\u70B9\u7AEF\u53E3");
			contentPanel.add(lblNewLabel_5, "2, 10, right, default");
		}
		{
			t_port = new JTextField();
			t_port.setToolTipText("\u670D\u52A1\u6240\u5C5E\u4E2D\u95F4\u4EF6\u63D0\u4F9B\u7684\u7AEF\u53E3");
			contentPanel.add(t_port, "4, 10, fill, default");
			t_port.setColumns(10);
		}
		{
			JLabel lblNewLabel_6 = new JLabel("\u4E0A\u4E0B\u6587\u6839");
			contentPanel.add(lblNewLabel_6, "2, 12, right, default");
		}
		{
			t_context = new JTextField();
			t_context.setToolTipText("\u8282\u70B9\u90E8\u7F72\u65F6\u7684\u4E0A\u4E0B\u6587\u6839");
			contentPanel.add(t_context, "4, 12, fill, default");
			t_context.setColumns(10);
		}
		{
			JLabel lblNewLabel_7 = new JLabel("\u8F6C\u53D1\u89C4\u5219");
			contentPanel.add(lblNewLabel_7, "2, 14, right, default");
		}
		{
			t_dispatchrule = new JTextField();
			t_dispatchrule.setToolTipText(
					"\u914D\u7F6E\u53EF\u767B\u5F55\u5230\u5F53\u524D\u5E94\u7528\u8282\u70B9\u7684\u7EC4\u7EC7\u673A\u6784\u4FE1\u606F\uFF0C\u914D\u7F6E\u7684\u7EC4\u7EC7\u673A\u6784\u53CA\u5176\u4E0B\u7EA7\u5747\u53EF\u767B\u5F55\u5230\u5F53\u524D\u8282\u70B9");
			contentPanel.add(t_dispatchrule, "4, 14, fill, default");
			t_dispatchrule.setColumns(10);
		}
		{
			JLabel lblNewLabel_8 = new JLabel("\u96C6\u7FA4\u6807\u8BC6");
			contentPanel.add(lblNewLabel_8, "2, 16, right, default");
		}
		{
			t_colony = new JTextField();
			t_colony.setToolTipText("\u8282\u70B9\u6240\u5C5E\u7684\u96C6\u7FA4\u7684\u6807\u8BC6");
			contentPanel.add(t_colony, "4, 16, fill, default");
			t_colony.setColumns(10);
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
						if(null != callback ){
							boolean success = false;
							if (rownum == -1) {
								success=callback.add(getDistEntity());
							} else if (rownum >= 0) {
								success = callback.modify(rownum, getDistEntity());
							}
							if(success)
								dispose();
						}
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\u53D6\u6D88");
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
		initNodeTypeField();
		if (rownum > -1) {
			ConfigDistEntity entity = callback.get(rownum);
			fillFieds(entity);
		}
	}

	private void initNodeTypeField() {
		for (DistNodeType nodetype : Contants.DIST_NODES) {
			cb_nodetye.addItem(nodetype);
		}
	}

	public ConfigDistEntity getDistEntity() {
		ConfigDistEntity entity = new ConfigDistEntity();
		entity.setCurrentNode(StringHelper.getEnableStr(ck_currnode.isSelected()));
		entity.setNodeType(((DistNodeType) cb_nodetye.getSelectedItem()).getCode());
		entity.setNodeId(t_code.getText());
		entity.setNodeHost(t_ip.getText());
		entity.setNodePort(t_port.getText());
		entity.setNodeContextPath(t_context.getText());
		entity.setNodeDispatchRule(t_dispatchrule.getText());
		entity.setNodeClusterId(t_colony.getText());
		return entity;
	}

	private void fillFieds(ConfigDistEntity entity) {
		ck_currnode.setSelected(Contants.ATTRIBUTE_ENABLE.equals(entity.getCurrentNode()));
		cb_nodetye.setSelectedItem(DistNodeType.getDistNodeByCode(entity.getNodeType()));
		t_code.setText(entity.getNodeId());
		t_ip.setText(entity.getNodeHost());
		t_port.setText(entity.getNodePort());
		t_context.setText(entity.getNodeContextPath());
		t_dispatchrule.setText(entity.getNodeDispatchRule());
		t_colony.setText(entity.getNodeClusterId());

	}

}
