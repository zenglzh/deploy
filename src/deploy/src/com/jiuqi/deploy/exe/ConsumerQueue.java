package com.jiuqi.deploy.exe;

import java.util.concurrent.BlockingQueue;

import com.jiuqi.deploy.intf.IProduct;

public class ConsumerQueue implements Runnable {
	private final BlockingQueue<IProduct> conQueue;

	public ConsumerQueue(BlockingQueue<IProduct> conQueue) {

		this.conQueue = conQueue;

	}

	@Override
	public void run() {
		while (!conQueue.isEmpty()) {
			try {
				IProduct product = conQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
