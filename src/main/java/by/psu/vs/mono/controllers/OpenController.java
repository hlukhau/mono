package by.psu.vs.mono.controllers;

import by.psu.vs.mono.services.ScreenshotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;


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
            produces = "image/jpg"
    )
    public @ResponseBody byte[] getImageWithMediaType() throws IOException {
        return screenshotService.getScreenShot();
    }

    @GetMapping("/")
    public String template(Model model) throws IOException {

        // Пересканирование локальной сети только при первом запуске
        if (screenshotService.ips.size() == 0) {
            screenshotService.scanIps();
        }

        var result = makeArrayListOfDesktops();

        model.addAttribute("desktops", result);

        // Формирование страницы index.html по шаблону /templates/index.html
        return "index";
    }

    @GetMapping("/name")
    public @ResponseBody String getName() {
        return screenshotService.getComputerName();
    }

    @PostMapping("/save")
    public String save(@RequestBody String json) throws IOException {

        System.out.println(json);

        FileWriter jsonFile = new FileWriter("configuration.json");
        jsonFile.write(json);
        jsonFile.close();
        return "ok";
    }

    @GetMapping("/rescan")
    public RedirectView rescan(Model model) throws IOException {

        // Пересканирование локальной сети всегда
        screenshotService.scanIps();

        var result = makeArrayListOfDesktops();

        model.addAttribute("desktops", result);

        // Формирование страницы index.html по шаблону /templates/index.html
        return new RedirectView("/");
    }

    ArrayList makeArrayListOfDesktops() throws IOException {
        ArrayList<String> presence = new ArrayList<String>();
        File jsonFile = new File("configuration.json");
        String json = new String(Files.readAllBytes(jsonFile.toPath()));

        var result =
                new ObjectMapper().readValue(json, ArrayList.class);
        int left = 300;
        int top = 300;

        for (Object o : result) {
            Map<String, String> map = (Map<String, String>)o;
            presence.add(map.get("name"));
        }

        for (Map.Entry<String, String> entry : screenshotService.ips.entrySet()) {
            String url = entry.getKey();
            String name = entry.getValue();
            HashMap<String, String> desktop = new HashMap<>();
            desktop.put("url", url);
            desktop.put("name", name);
            desktop.put("style", "left: " + left + "px; top: " + top + "px; z-index: 108;");
            left += 30;
            top += 30;
            if (! presence.contains(name)) {
                result.add(desktop);
                presence.add(name);
            }
        }
        return result;
    }
}
