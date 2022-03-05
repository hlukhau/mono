package by.psu.vs.mono.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenController {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String hello() {
        return "Hello World";
    }
}
