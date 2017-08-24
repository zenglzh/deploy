/*
 * @(#)CodeEditorDialog.java  
 */
package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author: zenglizhi
 * @time: 2017Äê6ÔÂ30ÈÕ
 */
public class CodeEditorDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CodeEditorDialog dialog = new CodeEditorDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CodeEditorDialog() {
		setType(Type.UTILITY);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		CodeEditor editor = new CodeEditor();
		contentPanel.add(editor);
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.NORTH);
		
		JInternalFrame internalFrame = new JInternalFrame("New JInternalFrame");
		contentPanel.add(internalFrame, BorderLayout.CENTER);
		internalFrame.setVisible(true);
	}

}
