package server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/websocket")
    public String getWebSocketTestPage() {
        return "websocket-test";
    }
}
