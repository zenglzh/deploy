package com.jiuqi.deploy.exe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsqltool.model.CustomTableModel;

import com.jiuqi.deploy.intf.IProduct;
import com.jiuqi.deploy.server.TableBody;
import com.jiuqi.deploy.server.TableHeader;

public class BlockTable {

	private TableHeader header;
	private Map<String, IProduct> codeIndexs;
	private BlockingQueue<IProduct> rows;
	private CustomTableModel model;

	public BlockTable() {
		this.header = new TableHeader();
		this.rows = new LinkedBlockingQueue<IProduct>(40);
		this.codeIndexs = new LinkedHashMap<String, IProduct>();
	}

	public TableHeader getHeader() {
		return header;
	}

	public BlockingQueue<IProduct> getRows() {
		return rows;
	}

	public boolean initedHeader() {
		return null != model && header.inited();
	}

	public void putToQueue(IProduct e) throws InterruptedException {
		rows.put(e);
	}

	public void put(String code, IProduct product) {
		codeIndexs.put(code, product);
	}

	public IProduct get(String code) {
		return codeIndexs.get(code);
	}

	public int size() {
		return codeIndexs.size();
	}

	public void buildTableModel() {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		int index = 0;
		for (String code : codeIndexs.keySet()) {
			IProduct iProduct = codeIndexs.get(code);
			((TableBody) iProduct).setRowIndex(index);
			Vector<Object> row = iProduct.getRow();
			row.add("*" + code);
			data.add(row);
			index++;
		}
		this.model = new CustomTableModel(header.getColNames(), header.getClassTypes(), header.getColSizes());
		model.setDataVector(data);
	}

	public CustomTableModel getModel() {
		return model;
	}

}
