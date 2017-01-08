package com.jiuqi.deploy.db;

public class Constant {
	
	/**
	 * 查看归档日志大小：单位M
	 */
	public static final String sql_archive = "SELECT SUM(BLOCKS*BLOCK_SIZE)/1024/1024/1024  FROM V$ARCHIVED_LOG WHERE DELETED='NO'";
	
	public static final String sql_tableSpace = " select sum(ts.ts_size) from ( "+
													 "SELECT t.tablespace_name, SUM(bytes / (1024 * 1024 * 1024)) ts_size "+
													 " FROM dba_tablespaces t, dba_data_files d "+
													 "WHERE t.tablespace_name = d.tablespace_name "+
													 "GROUP BY t.tablespace_name) ts";
}
