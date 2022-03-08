package by.psu.vs.mono.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;

@Service
public class ScreenshotService {

    static {
        System.setProperty("java.awt.headless", "false");
    }

    @Scheduled(fixedDelay = 1000)
    synchronized public void  scheduleFixedDelayTask() {
        makeScreenShot("saved.png");
    }

    synchronized public byte[] getScreenShot()  throws IOException {
        File outputFile = new File("saved.png");
        return Files.readAllBytes(outputFile.toPath());
    }

    public String getComputerIp() {
        String ip = "";
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
        }
        catch (UnknownHostException ex) {
            System.out.println("Hostname can not be resolved");
        }
        return ip;
    }

    public String getComputerName()
    {
        String hostname = "";
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex) {
            System.out.println("Hostname can not be resolved");
        }
        return hostname;
    }

    public void makeScreenShot(String path) {
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
                File outputFile = new File(path);
                ImageIO.write(bufferedImage, "png", outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch(AWTException e) {
            e.printStackTrace();
        }
    }
}
