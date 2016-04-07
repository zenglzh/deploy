package com.jiuqi.deplay.db;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public interface TablespaceProperties extends Serializable, Cloneable {

	public TablespaceProperties clone();

	public LinkedHashMap<String, String> getPropertiesMap();

	public List<String> validate();
}
