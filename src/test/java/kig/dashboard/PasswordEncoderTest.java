package kig.dashboard;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void 패스워드_암호화() throws Exception {

        String password = "password";

        String encode = passwordEncoder.encode(password);

        assertThat(encode).startsWith("{");
        assertThat(encode).contains("{bcrypt}");
        assertThat(encode).isNotEqualTo(password);

    }

    @Test
    public void 패스워드암호화_랜덤() {

        String password = "password";

        String encode1 = passwordEncoder.encode(password);
        String encode2 = passwordEncoder.encode(password);

        assertThat(encode1).isNotEqualTo(encode2);
    }

    @Test
    public void 암호화된비밀번호_일치여부확인() {

        String password = "password";
        String encode = passwordEncoder.encode(password);

        assertThat(passwordEncoder.matches(password, encode)).isTrue();
    }
}
