package com.jiuqi.deploy.intf;

import java.util.EventListener;

public interface IPropertyListener extends EventListener {

	public void propertyChanged(Object source, int propId);

}