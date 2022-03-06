package by.psu.vs.mono.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EchoClient {

    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public EchoClient() throws IOException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("255.255.255.255");
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4000);
        socket.send(packet);
        System.out.println("Отправили");
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = packet.getAddress() + " " + packet.getPort() + " text = " + new String(packet.getData());
        return received;
    }

    public void close() {
        socket.close();
    }

}