package com.framework.xx.threadtest.blockqueue;

public class Test {
	
	public static void main(String [] a) throws InterruptedException {
		
		while (true) {
			
			Thread.sleep(100);
			
			Produce p=new Produce();
			
			new Thread(p).start();
			
			Consumer c=new Consumer();
			
			new Thread(c).start();
			
		}
		
	}

}
