package by.psu.vs.mono.controllers;

import by.psu.vs.mono.documents.Desktop;
import by.psu.vs.mono.services.ScreenshotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String template(Model model) throws IOException {
        File jsonFile = new File("configuration.json");
        String json = new String(Files.readAllBytes(jsonFile.toPath()));
        ArrayList result =
                new ObjectMapper().readValue(json, ArrayList.class);

        String msg = "Welcome to Vladimir Template";
        // adding the attribute(key-value pair)
        model.addAttribute("message", msg);
        model.addAttribute("desktops", result);
        // returning the view name
        return "index";
    }
}
