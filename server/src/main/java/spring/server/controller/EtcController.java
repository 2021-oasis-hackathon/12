package spring.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/etc")
public class EtcController {
    @GetMapping("/notice")
    public String notice () {
        return "etc/notice";
    }
}
