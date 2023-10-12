package kig.dashboard.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:8081/"})
public class MemberController {

    @GetMapping
    public String getData() {
        return "API Data 준비중 ...";
    }

}
