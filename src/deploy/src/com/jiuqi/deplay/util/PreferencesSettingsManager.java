package com.jiuqi.deplay.util;

import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 *
 * @author esalaza
 */
public class PreferencesSettingsManager implements SettingsManager {

    private final String DEFAULT_STRING_VALUE = "?";

    Preferences preferences = null;
    ArrayList<ConnectionInfo> connectionsInfo = null;

    public PreferencesSettingsManager() {
        initialize();
        loadConnectionsInfo();
    }

    private void initialize() {
        preferences = Preferences.userNodeForPackage(getClass());
    }

    private void loadConnectionsInfo() {
        String connectionNames = preferences.get("connectionNames", DEFAULT_STRING_VALUE);
        if (connectionNames == null || connectionNames.equals(DEFAULT_STRING_VALUE)) {
            return;
        }

        String[] connectionNamesArray = connectionNames.split(",");
        int connectionsCount = connectionNamesArray.length;

        if (connectionsCount == 0) {
            connectionsInfo = null;
            return;
        }

        connectionsInfo = new ArrayList<ConnectionInfo>(connectionsCount);

        for (int i = 0; i < connectionsCount; i++) {
            ConnectionInfo connectionInfo = new ConnectionInfo();
            String name = connectionNamesArray[i].trim();
            connectionInfo.setName(name);

            DatabaseConnectionInfo database = new DatabaseConnectionInfo();
            database.setHost(preferences.get("database." + name + ".host", DEFAULT_STRING_VALUE));
            database.setPort(preferences.get("database." + name + ".port", DEFAULT_STRING_VALUE));
            database.setUsername(preferences.get("database." + name + ".username", DEFAULT_STRING_VALUE));
            database.setPassword(preferences.get("database." + name + ".password", DEFAULT_STRING_VALUE));
            database.setSid(preferences.get("database." + name + ".sid", DEFAULT_STRING_VALUE));
            connectionInfo.setDatabaseConnectionInfo(database);

            connectionsInfo.add(i, connectionInfo);
        }

    }

    private void saveConnectionsInfo() {
        if (connectionsInfo == null || connectionsInfo.size() == 0) {
            return;
        }

        for(ConnectionInfo connectionInfo : connectionsInfo) {
            String name = connectionInfo.getName().trim();
            DatabaseConnectionInfo database = connectionInfo.getDatabaseConnectionInfo();
            preferences.put("database." + name + ".host", database.getHost().trim());
            preferences.put("database." + name + ".port", database.getPort().trim());
            preferences.put("database." + name + ".username", database.getUsername().trim());
            preferences.put("database." + name + ".password", database.getPassword().trim());
            preferences.put("database." + name + ".sid", database.getSid().trim());
        }

        String connectionNamesList = ConnectionInfo.getCommaSeparatedListOfConnectionNames(connectionsInfo);
        preferences.put("connectionNames", connectionNamesList);
    }


    public ArrayList<ConnectionInfo> getConnectionsInfo() {
        loadConnectionsInfo();
        return connectionsInfo;
    }

    public void setConnectionsInfo(ArrayList<ConnectionInfo> connectionSettings) {
        this.connectionsInfo = connectionSettings;
        saveConnectionsInfo();
    }

    public String getSetting(String key) {
        if (preferences == null) {
            return null;
        }
        return preferences.get(key, DEFAULT_STRING_VALUE);
    }

    public void setSetting(String key, String value) {
        preferences.put(key, value);
    }
    
}
