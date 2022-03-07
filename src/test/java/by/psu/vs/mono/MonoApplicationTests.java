package by.psu.vs.mono;

import by.psu.vs.mono.utils.EchoClient;
import by.psu.vs.mono.utils.EchoServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootTest
@Slf4j
class MonoApplicationTests {

	static {
		System.setProperty("java.awt.headless", "false");
	}
	private final Map<String, String> simpleMap = new ConcurrentHashMap<>();//new HashMap<>();

	public int getRandomNumber(int max) {
		return (int) ((Math.random() * (max)));
	}

	private void sleep(int m) {
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
					log.info("------------------------------------");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void makeScreenShotTest() throws AWTException {
		try {
			Robot robot = new Robot();

	// Захват определенной области на экране
			int x = 100;
			int y = 100;
			int width = 200;
			int height = 200;
			Rectangle area = new Rectangle(x, y, width, height);
			BufferedImage bufferedImage = robot.createScreenCapture(area);

			// Захватывать весь экран
			area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			bufferedImage = robot.createScreenCapture(area);

			try {
				File outputfile = new File("saved.png");
				ImageIO.write(bufferedImage, "png", outputfile);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch(AWTException e) {
			e.printStackTrace();
		}
	}

	private String getComputerName()
	{
		Map<String, String> env = System.getenv();
		if (env.containsKey("COMPUTERNAME"))
			return env.get("COMPUTERNAME");
		else if (env.containsKey("HOSTNAME"))
			return env.get("HOSTNAME");
		else
			return "Unknown Computer";
	}

	private String getComputerName2()
	{
		String hostname = "";
		String ip = "";
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
			ip = addr.getHostAddress();
		}
		catch (UnknownHostException ex) {
			System.out.println("Hostname can not be resolved");
		}
		//if (hostname.length() == 0) {
			hostname = ip;
		//}
		return hostname;
	}

	@Test
	public void getComputerNameTest() {

		log.info(getComputerName2());
		log.info(getComputerName());
	}

	@Test
	@Disabled
	public void testUDP() throws Exception {
		//EchoServer server = new EchoServer();
		//server.start();

		EchoClient client = new EchoClient();

		String result = client.sendEcho("from client");

		log.info("===============" + result);
	}
}
