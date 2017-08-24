package com.jiuqi.deploy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.jsqltool.model.CustomTableModel;

import com.jiuqi.deploy.intf.ISelect;
import com.jiuqi.deploy.server.HistorySQLManage;

public class HistorySQLDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private CustomTableModel oldModel = new CustomTableModel(new String[] { "SQL" }, new Class[] { String.class }, new int[] { 100 });
	private CustomTableModel commonlyModel = new CustomTableModel(new String[] { "����","SQL" }, new Class[] { String.class,String.class }, new int[] {30, 100 });
	private JTable oldQueries,commonly;
	private HistorySQLManage sqlmeme;
	private JTabbedPane tabbedPane ;
	/**
	 * Create the dialog.
	 * 
	 * @param sqlmeme
	 */
	public HistorySQLDialog(final ISelect<String> iSelect, HistorySQLManage sqlmeme) {
		this.sqlmeme = sqlmeme;
		init();
		setBounds(100, 100, 667, 474);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			contentPanel.add(tabbedPane);
			{
				oldQueries = new JTable(oldModel);
				oldQueries.addMouseListener(new MouseAdapter() {

					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							int row = oldQueries.getSelectedRow();
							Object valueAt = oldModel.getValueAt(row, oldQueries.getColumnCount() - 1);
							selectRow(iSelect,valueAt);
						}
					}
				});
				JScrollPane scrollPane = new JScrollPane(oldQueries);
				tabbedPane.addTab("��ʷSQL", null, scrollPane, null);

			}
			{
				commonly  = new JTable(commonlyModel);
				commonly.addMouseListener(new MouseAdapter() {

					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							int row = commonly.getSelectedRow();
							Object valueAt = commonlyModel.getValueAt(row, commonly.getColumnCount() - 1);
							selectRow(iSelect,valueAt);
						}
					}
				});
				JScrollPane scrollPane = new JScrollPane(commonly);
				tabbedPane.addTab("����DBA", null, scrollPane, null);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u9009\u62E9");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int selectedIndex = tabbedPane.getSelectedIndex();
						if(selectedIndex==0){
							int row = oldQueries.getSelectedRow();
							Object valueAt = oldModel.getValueAt(row, oldQueries.getColumnCount() - 1);
							selectRow(iSelect,valueAt);
						}else {
							int row = commonly.getSelectedRow();
							Object valueAt = commonlyModel.getValueAt(row, commonly.getColumnCount() - 1);
							selectRow(iSelect,valueAt);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\u53D6\u6D88");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void selectRow(final ISelect<String> iSelect,Object valueAt) {
		if (null != valueAt && null != iSelect) {
			iSelect.select(String.valueOf(valueAt).trim());
		}
		dispose();
	}

	private void init() {
		initOldModel();
		initCommonlyModel();
	}

	private void initOldModel() {
		oldModel.setEditMode(CustomTableModel.DETAIL_REC);
		List<String> allSQLs = sqlmeme.allSQLs();
		for (int i = allSQLs.size() - 1; i >= 0; i--) {
			oldModel.addRow(new Object[] { allSQLs.get(i) });
		}
	}
	private void initCommonlyModel() {
		commonlyModel.setEditMode(CustomTableModel.DETAIL_REC);
		for (int i = 0;i<COMMON_SQL.length;i++) {
			commonlyModel.addRow(new Object[] {COMMON_SQL[i][0],COMMON_SQL[i][1]});
		}
	}
	
	private final static String[][] COMMON_SQL = {		
		{"��ѯ������SQL���","select sql_text from v$sql where hash_value in (select sql_hash_value from v$session where sid in (select session_id from v$locked_object));"},
		{"��ѯOracle����ִ�е�sql��估ִ�и������û�",
		"SELECT b.sid oracleID, 						"+		
		"	       b.username ��¼Oracle�û���,         "+
		"	       b.serial#,                           "+
		"	       spid ����ϵͳID,                     "+
		"	       paddr,                               "+
		"	       sql_text ����ִ�е�SQL,              "+
		"	       c.SQL_FULLTEXT,                      "+
		"	       b.machine �������                   "+
		"	FROM v$process a, v$session b, v$sqlarea c  "+
		"	WHERE a.addr = b.paddr                      "+
		"	   AND b.sql_hash_value = c.hash_value      "
		},
		{
			"���oracle��ǰ�ı�������",
			"--���oracle��ǰ�ı�������                       \n "+
			"SELECT l.session_id sid,                        \n "+
			"       s.serial#,                                 "+
			"       l.locked_mode ��ģʽ,                      "+
			"       l.oracle_username ��¼�û�,                "+
			"       l.os_user_name ��¼�����û���,             "+
			"       s.machine ������,                          "+
			"       s.terminal �ն��û���,                     "+
			"       o.object_name ����������,                  "+
			"       s.logon_time ��¼���ݿ�ʱ��                "+
			"FROM v$locked_object l, all_objects o, v$session s"+
			"WHERE l.object_id = o.object_id                   "+
			"   AND l.session_id = s.sid                       "+
			"ORDER BY sid, s.serial#;                          "
		},
		{
			"1���鿴��ռ�����Ƽ���С",
			"select t.tablespace_name, round(sum(bytes/(1024*1024)),0) ts_size from dba_tablespaces t, dba_data_files d where t.tablespace_name = d.tablespace_name group by t.tablespace_name;"			
		},
		{
			"�鿴��ռ������ļ������Ƽ���С",
			"select tablespace_name, file_id, file_name, round(bytes/(1024*1024),0) total_space from dba_data_files order by tablespace_name;"
		},
		{
			"�鿴�ع������Ƽ���С",
			"select segment_name, tablespace_name, r.status, (initial_extent/1024) InitialExtent,(next_extent/1024) NextExtent, max_extents, v.curext CurExtent From dba_rollback_segs r, v$rollstat v Where r.segment_id = v.usn(+) order by segment_name ;"
		},
		{
			"�鿴�����ļ�",
			"select name from v$controlfile;"
		},
		{
			"�鿴��־�ļ�",
			"select member from v$logfile;"
		},
		{
			"�鿴��ռ��ʹ�����",
			"select sum(bytes)/(1024*1024) as free_space,tablespace_name from dba_free_space group by tablespace_name;"
		},
		{
			"�鿴��ռ��ʹ�����2",
			"SELECT A.TABLESPACE_NAME,A.BYTES TOTAL,B.BYTES USED, C.BYTES FREE, (B.BYTES*100)/A.BYTES \"% USED\",(C.BYTES*100)/A.BYTES \"% FREE\" FROM SYS.SM$TS_AVAIL A,SYS.SM$TS_USED B,SYS.SM$TS_FREE C WHERE A.TABLESPACE_NAME=B.TABLESPACE_NAME AND A.TABLESPACE_NAME=C.TABLESPACE_NAME; "
		},
		{
			"�鿴���ݿ�����",
			"select owner, object_type, status, count(*) count# from all_objects group by owner, object_type, status;"
		},
		{
			"�鿴���ݿ�İ汾",
			"Select version FROM Product_component_version Where SUBSTR(PRODUCT,1,6)='Oracle';"
		},{
			"�鿴���ݿ�Ĵ������ں͹鵵��ʽ",
			"Select Created, Log_Mode, Log_Mode From V$Database;"
		},
		{
			"��׽���кܾõ�SQL",
			"column username format a12 column opname format a16 column progress format a8 select username,sid,opname,  round(sofar*100 / totalwork,0) || '%' as progress, time_remaining,sql_text from v$session_longops , v$sql where time_remaining <> 0 and sql_address = address and sql_hash_value = hash_value /"
		},
		{
			"�鿴���ݱ�Ĳ�����Ϣ",
			"SELECT   partition_name, high_value, high_value_length, tablespace_name,         pct_free, pct_used, ini_trans, max_trans, initial_extent,         next_extent, min_extent, max_extent, pct_increase, FREELISTS,         freelist_groups, LOGGING, BUFFER_POOL, num_rows, blocks,         empty_blocks, avg_space, chain_cnt, avg_row_len, sample_size,         last_analyzed    FROM dba_tab_partitions   --WHERE table_name = :tname AND table_owner = :townerORDER BY partition_position"
		},
		{
			"�鿴��û�ύ������",
			"select * from v$locked_object;select * from v$transaction;"
		},
		{
			"����objectΪ��Щ��������",
			"select p.spid,s.sid,s.serial# serial_num,s.username user_name,a.type  object_type,s.osuser os_user_name,a.owner,a.object object_name,decode(sign(48 - command),1,to_char(command), 'Action Code #' || to_char(command) ) action,p.program oracle_process,s.terminal terminal,s.program program,s.status session_status   from v$session s, v$access a, v$process p   where s.paddr = p.addr and      s.type = 'USER' and         a.sid = s.sid   and   a.object='SUBSCRIBER_ATTR'order by s.username, s.osuser"
		},
		{
			"�ع��β鿴",
			"select rownum, sys.dba_rollback_segs.segment_name Name, v$rollstat.extents Extents, v$rollstat.rssize Size_in_Bytes, v$rollstat.xacts XActs, v$rollstat.gets Gets, v$rollstat.waits Waits, v$rollstat.writes Writes, sys.dba_rollback_segs.status status from v$rollstat, sys.dba_rollback_segs, v$rollname where v$rollname.name(+) = sys.dba_rollback_segs.segment_name and v$rollstat.usn (+) = v$rollname.usn order by rownum"
		},
		{
			"����Դ�Ľ��̣�top session��",
			"select s.schemaname schema_name,   decode(sign(48 - command), 1, to_char(command), 'Action Code #' || to_char(command) ) action,   status session_status,  s.osuser os_user_name,  s.sid,         p.spid ,        s.serial# serial_num,  nvl(s.username, '[Oracle process]') user_name,  s.terminal terminal,   s.program program,  st.value criteria_value  from v$sesstat st,  v$session s  , v$process p   where st.sid = s.sid and  st.statistic# = to_number('38') and  ('ALL' = 'ALL' or s.status = 'ALL') and p.addr = s.paddr order by st.value desc,  p.spid asc, s.username asc, s.osuser asc"
		},
		{
			"�鿴����lock�����",
			""
		},
		{
			"",
			""
		},
		{
			"",
			""
		},
		{
			"��ѯ��ռ�ʹ�����",
			"select a.tablespace_name \"��ռ�����\",100-round((nvl(b.bytes_free,0)/a.bytes_alloc)*100,2) \"ռ����(%)\",round(a.bytes_alloc/1024/1024,2) \"����(M)\",round(nvl(b.bytes_free,0)/1024/1024,2) \"����(M)\",round((a.bytes_alloc-nvl(b.bytes_free,0))/1024/1024,2) \"ʹ��(M)\",Largest \"�����չ��(M)\",to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') \"����ʱ��\" from  (select f.tablespace_name,  sum(f.bytes) bytes_alloc,  sum(decode(f.autoextensible,'YES',f.maxbytes,'NO',f.bytes)) maxbytes from dba_data_files f group by tablespace_name) a,(select  f.tablespace_name,  sum(f.bytes) bytes_free from dba_free_space f group by tablespace_name) b,(select round(max(ff.length)*16/1024,2) Largest,  ts.name tablespace_name from sys.fet$ ff, sys.file$ tf,sys.ts$ ts where ts.ts#=ff.ts# and ff.file#=tf.relfile# and ts.ts#=tf.ts# group by ts.name, tf.blocks) c where a.tablespace_name = b.tablespace_name and a.tablespace_name = c.tablespace_name" 
		},
		
		{
			"",
			""
		},
		{
			"��ѯ����Щ���ݿ�ʵ��������",
			"select inst_name from v$active_instances;"
		},
	};
}
