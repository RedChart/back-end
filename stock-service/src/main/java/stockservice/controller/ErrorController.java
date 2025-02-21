package server.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/stocks")
public class ErrorController {

    // 404 오류를 처리하는 엔드포인트
    @GetMapping("/handle-404")
    public ResponseEntity<String> handle404(@RequestParam String file) {
        String message = "정적 리소스를 찾을 수 없습니다 : " + file;
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}