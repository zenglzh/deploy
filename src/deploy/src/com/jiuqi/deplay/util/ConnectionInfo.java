package com.jiuqi.deplay.util;

import java.util.ArrayList;

/**
 *
 * @author esalaza
 */
public class ConnectionInfo {
    
    private String name = null;
    private DatabaseConnectionInfo  databaseConnectionInfo  = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public DatabaseConnectionInfo getDatabaseConnectionInfo() {
        return databaseConnectionInfo;
    }

    public void setDatabaseConnectionInfo(DatabaseConnectionInfo databaseConnectionInfo) {
        this.databaseConnectionInfo = databaseConnectionInfo;
    }

    public static ConnectionInfo getConnectionInfoByName(
            String name,
            ArrayList<ConnectionInfo> connectionsInfo) {
        ConnectionInfo c = null;
        for (ConnectionInfo connectionInfo : connectionsInfo) {
            if (connectionInfo.getName().equals(name)) {
                return connectionInfo;
            }
        }
        return c;
    }

    public static String getCommaSeparatedListOfConnectionNames(
            ArrayList<ConnectionInfo> connectionsInfo) {
        if (connectionsInfo == null || connectionsInfo.size() == 0) {
            return "";
        }
        String commaSeparatedListOfConnectionNames = "";
        for (int i = 0; i < connectionsInfo.size(); i++) {
            ConnectionInfo connectionInfo = connectionsInfo. get(i);
            commaSeparatedListOfConnectionNames += connectionInfo.getName();
            if (i != (connectionsInfo.size() - 1)) {
                commaSeparatedListOfConnectionNames += ",";
            }
        }
        return commaSeparatedListOfConnectionNames;
    }
    
}
