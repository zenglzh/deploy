package com.jiuqi.deploy.intf;

import java.util.Vector;

public interface IProduct {

	public boolean hasHeader();

	public Vector<Object> getRow();

	public int rowIndex();
}
