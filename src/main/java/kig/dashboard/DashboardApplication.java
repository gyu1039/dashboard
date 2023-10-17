package kig.dashboard;

import kig.dashboard.post.PostRepository;
import kig.dashboard.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DashboardApplication{

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

    @Autowired
    private MemberRepository employeeRepository;

    @Autowired
    private PostRepository postRepository;

    @Bean
    public CommandLineRunner testData() {

        return args -> {

        };
    }
}
