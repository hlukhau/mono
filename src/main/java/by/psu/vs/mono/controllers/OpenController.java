package by.psu.vs.mono.controllers;

import by.psu.vs.mono.services.ChatService;
import by.psu.vs.mono.services.ScreenshotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/")public class OpenController {

    @Autowired
    private ScreenshotService screenshotService;

    @Autowired
    private ChatService chatService;

    @GetMapping(
        value = "/image",
        produces = "image/png"
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
        String name = screenshotService.getComputerName();
        return name;
    }

    @PostMapping("/send")
    public String send(@RequestBody String text) {
        log.info("TEXT RECEIVED: {}", text);
        chatService.saveMessage(text);
        return "ok";
    }

    @GetMapping("/chat")
    public @ResponseBody String getChatMap() {
        return chatService.getChatMap();
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
