package newsfeedservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import newsfeedservice.dto.NewsfeedDTO;
import newsfeedservice.service.NewsfeedService;

@RestController
@RequestMapping("/newsfeeds")
@RequiredArgsConstructor
public class NewsfeedController {

    private NewsfeedService newsfeedService;

    @GetMapping("/details/{userId}")
    public ResponseEntity<?> getNewsfeedDetails(@PathVariable("userId") Long userId) {
        NewsfeedDTO newsfeedDTO = newsfeedService.getNewsfeedDetails(userId);
        return ResponseEntity.ok(newsfeedDTO);
    }
}