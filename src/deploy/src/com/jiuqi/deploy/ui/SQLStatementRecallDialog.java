package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.utils.Options;

/**
 * <p>
 * Title: JSqlTool Project
 * </p>
 * <p>
 * Description: Dialog used to select an old query.
 * </p>
 * <p>
 * Copyright: Copyright (C) 2006 Mauro Carniel
 * </p>
 *
 * <p>
 * This file is part of JSqlTool project. This library is free software; you can
 * redistribute it and/or modify it under the terms of the (LGPL) Lesser General
 * Public License as published by the Free Software Foundation;
 *
 * GNU LESSER GENERAL PUBLIC LICENSE Version 2.1, February 1999
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * The author may be contacted at: maurocarniel@tin.it
 * </p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */
public class SQLStatementRecallDialog extends JDialog {
	JPanel mainPanel = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel centerPanel = new JPanel();
	JPanel buttonsPanel = new JPanel();
	JButton cancelButton = new JButton();
	JButton okButton = new JButton();
	JScrollPane scrollPane = new JScrollPane();
	BorderLayout borderLayout2 = new BorderLayout();
	CustomTableModel model = new CustomTableModel(new String[] { "SQL" }, new Class[] { String.class });
	JTable oldQueries = new JTable(model);
	private SQLEditorFrame frame = null;

	public SQLStatementRecallDialog(JFrame parent, DbConnectionUtil dbConnUtil, SQLEditorFrame frame) {
		super(parent, Options.getInstance().getResource("old sql statements"), true);
		this.frame = frame;
		try {
			jbInit();
			Dimension frameSize = new Dimension(750, 400);
			setSize(frameSize);
			Dimension screenSize = parent.getSize();
			if (frameSize.height > screenSize.height) {
				frameSize.height = screenSize.height;
			}
			if (frameSize.width > screenSize.width) {
				frameSize.width = screenSize.width;
			}
			setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
			init(dbConnUtil);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public SQLStatementRecallDialog() {
		this(null, null, null);
	}

	private void init(DbConnectionUtil dbConnUtil) {
		ArrayList oldQueries = dbConnUtil.getDbConnection().getOldQueries();
		model.setEditMode(model.DETAIL_REC);
		for (int i = 0; i < oldQueries.size(); i++)
			model.addRow(new Object[] { oldQueries.get(i).toString() });
	}

	private void jbInit() throws Exception {
		mainPanel.setLayout(borderLayout1);
		cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
		cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
		cancelButton.addActionListener(new SQLStatementRecallDialog_cancelButton_actionAdapter(this));
		okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
		okButton.setText(Options.getInstance().getResource("okbutton.text"));
		okButton.addActionListener(new SQLStatementRecallDialog_okButton_actionAdapter(this));
		buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
		centerPanel.setLayout(borderLayout2);
		oldQueries.addMouseListener(new SQLStatementRecallDialog_oldQueries_mouseAdapter(this));
		getContentPane().add(mainPanel);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.add(okButton, null);
		buttonsPanel.add(cancelButton, null);
		scrollPane.getViewport().add(oldQueries, null);
	}

	void cancelButton_actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}

	void okButton_actionPerformed(ActionEvent e) {
		if (oldQueries.getSelectedRow() != -1) {
			// frame.setEditorContent(oldQueries.getValueAt(oldQueries.getSelectedRow(),0).toString());
			setVisible(false);
			dispose();
		}
	}

	void oldQueries_mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			okButton_actionPerformed(null);
		}
	}
}

class SQLStatementRecallDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
	SQLStatementRecallDialog adaptee;

	SQLStatementRecallDialog_cancelButton_actionAdapter(SQLStatementRecallDialog adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.cancelButton_actionPerformed(e);
	}
}

class SQLStatementRecallDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
	SQLStatementRecallDialog adaptee;

	SQLStatementRecallDialog_okButton_actionAdapter(SQLStatementRecallDialog adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.okButton_actionPerformed(e);
	}
}

class SQLStatementRecallDialog_oldQueries_mouseAdapter extends java.awt.event.MouseAdapter {
	SQLStatementRecallDialog adaptee;

	SQLStatementRecallDialog_oldQueries_mouseAdapter(SQLStatementRecallDialog adaptee) {
		this.adaptee = adaptee;
	}

	public void mouseClicked(MouseEvent e) {
		adaptee.oldQueries_mouseClicked(e);
	}
}