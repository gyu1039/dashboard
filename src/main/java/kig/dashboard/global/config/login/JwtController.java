package kig.dashboard.global.config.login;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class JwtController {

    private final JwtService jwtService;

    @GetMapping("/refresh")
    public ResponseEntity<?> update(
            @RequestHeader("Authorization-refresh") String refresh) {

        HttpHeaders httpHeaders = new HttpHeaders();
        if(refresh != null &&jwtService.isTokenValid(refresh)) {

            httpHeaders.add("Authorization", jwtService.createAccessToken(SecurityUtil.getLoginUsername()));
            httpHeaders.add("Autorization-refresh", refresh);

        };

        return ResponseEntity.ok().headers(httpHeaders).build();
    }
}
