package com.jiuqi.deploy.exe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PublicBoxQueue {
	public static void main(String[] args) {

		BlockingQueue publicBoxQueue = new LinkedBlockingQueue(5); // ������һ����СΪ5�ĺ���

		Thread pro = new Thread(new ProducerQueue(publicBoxQueue));

		Thread con = new Thread(new ConsumerQueue(publicBoxQueue));

		pro.start();

		con.start();

	}
}
