package kig.dashboard;


import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.Role;
import kig.dashboard.member.entity.RoleMember;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.post.entity.Category;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.repository.CategoryRepository;
import kig.dashboard.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.lang.String.format;


//@Component
@RequiredArgsConstructor
public class DummyData {

    private final Init init;

    @PostConstruct
    public void init(){

        init.save();
    }

    @RequiredArgsConstructor
    @Component
    public static class Init {

        private final MemberRepository memberRepository;
        private final PostRepository postRepository;
        private final PasswordEncoder passwordEncoder;
        private final CategoryRepository categoryRepository;


        @Transactional
        public void save() {



        }

    }
}
