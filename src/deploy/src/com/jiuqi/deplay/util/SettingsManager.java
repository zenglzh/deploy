package com.jiuqi.deplay.util;

import java.util.ArrayList;

/**
 *
 * @author esalaza
 */
public interface SettingsManager {
    
    public String getSetting(String key);
    public void setSetting(String key, String value);
    public ArrayList<ConnectionInfo> getConnectionsInfo();
    public void setConnectionsInfo(ArrayList<ConnectionInfo> connectionSettings);
}
