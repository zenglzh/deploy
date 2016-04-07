package com.jiuqi.deplay.ui;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.deplay.db.ESBDBClient;
import com.jiuqi.deplay.intf.IPropertyListener;
import com.jiuqi.deplay.server.ConfigEntity;
import com.jiuqi.deplay.util.DatabaseConnectionInfo;

public class PageContext {

	private DatabaseConnectionInfo connectionInfo;
	private ConfigEntity configEntity;
	private List<IPropertyListener> listeners;
	public PageContext() {
		listeners = new ArrayList<IPropertyListener>();
	}

	public void setConnectionInfo(DatabaseConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	public DatabaseConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConfigEntity(ConfigEntity configEntity) {
		this.configEntity = configEntity;
	}

	public ConfigEntity getConfigEntity() {
		return configEntity;
	}

	public boolean isDBValid() {
		if (null != connectionInfo) {
			try {
				ESBDBClient client = new ESBDBClient(connectionInfo);
				client.testConnect();
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public void addPropertyListener(IPropertyListener listener) {
		listeners.add(listener);
	}

	public void removePropertyListener(IPropertyListener listener) {
		listeners.remove(listener);
	}

	public void firePropertyChange(int propertyId) {
		for (IPropertyListener iPropertyListener : listeners) {
			iPropertyListener.propertyChanged(this, propertyId);
		}
	}

	public void propertyChanged(Object source, int propId) {
		firePropertyChange(propId);
	}

}
