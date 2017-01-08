package com.jiuqi.deploy.exe;

import java.util.concurrent.BlockingQueue;

public class ProducerQueue implements Runnable {

	private final BlockingQueue proQueue;

	public ProducerQueue(BlockingQueue proQueue) {
		this.proQueue = proQueue;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {

			try {

				System.out.println("������������ƻ�����Ϊ : " + i); // ����ʮ��ƻ����� Ϊ1��10

				proQueue.put(i);

				/* Thread.sleep(3000); */

			} catch (InterruptedException e) {

				// TODO: handle exception

				e.printStackTrace();

			}

		}
	}

}
