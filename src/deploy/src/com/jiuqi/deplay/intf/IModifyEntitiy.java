package com.jiuqi.deplay.intf;

public interface IModifyEntitiy<T> {

	public T get(int row);
	public boolean modify(int row, T entity);
}