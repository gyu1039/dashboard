package kig.dashboard;

import kig.dashboard.board.BoardRepository;
import kig.dashboard.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DashboardApplication{

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

    @Autowired
    private MemberRepository employeeRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Bean
    public CommandLineRunner testData() {

        return args -> {

        };
    }
}
