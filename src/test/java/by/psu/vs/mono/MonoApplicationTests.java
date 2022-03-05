package by.psu.vs.mono;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.ranges.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
class MonoApplicationTests {

	private final Map<String, String> simpleMap = new ConcurrentHashMap<>();//new HashMap<>();

	public int getRandomNumber(int max) {
		return (int) ((Math.random() * (max)));
	}

	private void sleep(int m){
		try {
			Thread.sleep(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void contextLoads() {

		try {
			Runnable r1 = () -> {
				while (true) {
					for (String key : simpleMap.keySet()) {
						if ("Two".equals(simpleMap.get(key))) {
							simpleMap.remove(key);
							simpleMap.put(key, "One");
						}
					}
					simpleMap.put("key" + getRandomNumber(10), "One");
					sleep(10);
				}
			};

			Runnable r2 = () -> {
				while (true) {
					for (String key : simpleMap.keySet()) {
						if ("One".equals(simpleMap.get(key))) {
							simpleMap.remove(key);
							simpleMap.put(key, "Two");
						}
					}
					simpleMap.put("key" + getRandomNumber(10), "Two");
					sleep(10);
				}
			};

			Runnable r3 = () -> {
				while (true) {
					log.info("====================");
					for (String key : simpleMap.keySet()) {
						log.info(key + " - " + simpleMap.get(key));
					}
					sleep(100);
				}
			};

			new Thread(r1).start();
			new Thread(r2).start();
			new Thread(r3).start();

			sleep(1000);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyThread extends Thread {
		private String s;
		private Object o;
		private int counter = 0;

		public MyThread(String s, Object o) {
			super();
			this.s = s;
			this.o = o;
		}

		private void print() {
			try {
				synchronized (o) {
					IntStream.range(1, 100).forEach(i -> System.out.print(s));
					Thread.sleep(10);
					System.out.print(" " + getName() + "\n");
					counter++;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (true) {
				print();

				if (! "1".equals(s) && counter == 2) break;
			}
		}
	}

	@Test
	public void threadsTest() {
		for(int i = 0; i < 10; i++) {
			new MyThread("" + i, this).start();
		}

		sleep(1000);
	}
}
