package com.jiuqi.deplay.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import org.apache.log4j.Logger;


/**
 *
 * @author esalaza
 */
public class ESBDBClientSQL {

    static Logger logger = Logger.getLogger(ESBDBClientSQL.class);

    private Connection conn = null;

    // GetOperationInstances ---------------------------------------------------
    
    /**
     * select id, flow_id, timestamp, type
     * from esb_activity
     * where
     * TIMESTAMP>=?
     * AND TIMESTAMP<=?
     * AND (OPERATION_GUID IN (?))
     * ORDER BY flow_id
     *
     */
    private String getOperationInstancesSQL(String operationGuid, Date startDate, Date endDate) {
        boolean isFirstConditionInWhere = true;

        String sql =
                "select ID, FLOW_ID, TIMESTAMP, TYPE" + "\n" +
                "from ESB_ACTIVITY" + "\n" +
                "where " + "\n";

        if (startDate != null) {
            sql += " TIMESTAMP>=? " + "\n";
            isFirstConditionInWhere = false;
        }
        if (endDate != null) {
            if (isFirstConditionInWhere) {
                sql += "TIMESTAMP<=? " + "\n";
            } else {
                sql += "and TIMESTAMP<=? " + "\n";
            }
            isFirstConditionInWhere = false;
        }
        if (operationGuid != null && !operationGuid.equals("")) {
            if (isFirstConditionInWhere) {
                sql += "(OPERATION_GUID = ?) " + "\n";
            } else {
                sql += "and (OPERATION_GUID = ?) " + "\n";
            }
            isFirstConditionInWhere = false;
        }
        sql += "and TYPE <> 8 " + "\n";
        sql += "order by FLOW_ID, TIMESTAMP";
        
        logger.debug(sql);

        return sql;
    }

    public ResultSet getOperationInstancesResultSet(String operationGuid, Date startDate, Date endDate) throws Exception {
        ResultSet resultSet = null;
        PreparedStatement pstmt = null;
        int index = 1;

        String sql = getOperationInstancesSQL(operationGuid, startDate, endDate);
        pstmt = conn.prepareStatement(sql);
        
        if (startDate != null) {
            pstmt.setLong(index++, startDate.getTime());
        }
        if (endDate != null) {
            pstmt.setLong(index++, endDate.getTime());
        }
        if (operationGuid != null && !operationGuid.equals("")) {
            pstmt.setString(index++, operationGuid);
        }
        
        resultSet = pstmt.executeQuery();

        return resultSet;
    }
    //--------------------------------------------------------------------------



    public Connection getConnection() {
        return conn;
    }

    public void setConnection(Connection connection) {
        this.conn = connection;
    }

}
