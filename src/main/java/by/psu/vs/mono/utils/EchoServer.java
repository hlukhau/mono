package by.psu.vs.mono.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class EchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];

    public EchoServer() throws Exception {
        socket = new DatagramSocket(4000);
    }

    public void run()  {
        try{
            running = true;

            while (running)  {
                System.out.println("Start");
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String s = new String(packet.getData());
                s = "server receive: " + s;
                buf = s.getBytes();

                System.out.println("Server buf = " + buf.length + " " + new String(buf));
                packet = new DatagramPacket(buf, buf.length, address, port);

                socket.send(packet);
            }
            socket.close();
        }catch(IOException e)
        {
            System.out.println(e);
        }

    }
}