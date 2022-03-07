package by.psu.vs.mono.controllers;

import by.psu.vs.mono.services.ScreenshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")public class OpenController {


    @Autowired
    private ScreenshotService screenshotService;

    @RequestMapping(value = "/make", method = RequestMethod.GET)
    public void make() {
        screenshotService.makeScreenShot("saved.png");
    }

    @GetMapping(
            value = "/image",
            produces = "image/png"
    )
    public @ResponseBody byte[] getImageWithMediaType() throws IOException {
        synchronized (screenshotService) {
            File outputFile = new File("saved.png");
            byte[] fileContent = Files.readAllBytes(outputFile.toPath());
            return fileContent;
        }
    }

    @GetMapping("/")
    public String template(Model model) {
        String msg = "Welcome to Vladimir Template";
        // adding the attribute(key-value pair)
        model.addAttribute("message", msg);
        // returning the view name
        return "index";
    }
}
