package by.psu.vs.mono.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class ScreenshotService {

    static {
        System.setProperty("java.awt.headless", "false");
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        makeScreenShot("saved.png");
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
