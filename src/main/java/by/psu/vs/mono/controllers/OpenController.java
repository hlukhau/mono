package by.psu.vs.mono.controllers;

import by.psu.vs.mono.services.ScreenshotService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
public class OpenController {


    @Autowired
    private ScreenshotService screenshotService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "<h1>Hello World</h1><br><img src='/image' width=20%>";
    }


    @RequestMapping(value = "/make", method = RequestMethod.GET)
    public void make() {
        screenshotService.makeScreenShot("saved.png");
    }

    @GetMapping(
            value = "/image",
            produces = "image/png"
    )
    public @ResponseBody byte[] getImageWithMediaType() throws IOException {
        File outputFile = new File("saved.png");
        byte[] fileContent = Files.readAllBytes(outputFile.toPath());
        return fileContent;
    }

}
