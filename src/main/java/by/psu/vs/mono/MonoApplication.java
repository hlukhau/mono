package by.psu.vs.mono;

import by.psu.vs.mono.utils.EchoServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MonoApplication {

	public static void main(String[] args) throws Exception {
//		EchoServer server = new EchoServer();
//		server.start();

		SpringApplication.run(MonoApplication.class, args);
	}

}
