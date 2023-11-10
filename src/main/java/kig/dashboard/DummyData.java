package kig.dashboard;


import kig.dashboard.member.MemberRole;
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
        private final CategoryRepository categoryRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void save() {

            List<Member> all = memberRepository.findAll();
            Member writer1 = all.get(0);

            IntStream.range(0, 50).forEach(i -> {

                Post build = Post.builder()
                        .title("제목입니다 " + i)
                        .writer(writer1)
                        .content("내용입니다" + i)
                        .build();
                postRepository.save(build);
            });

            IntStream.range(0, 10).forEach(i -> {

                Member member = Member.builder()
                        .username("test" + i)
                        .password(passwordEncoder.encode("test"))
                        .nickname("닉네임" + i)
                        .role(MemberRole.USER)
                        .build();

                memberRepository.save(member);
            });

            IntStream.range(0, 3).forEach(i -> {

                Member member = Member.builder()
                        .username("admin" + i)
                        .password(passwordEncoder.encode("test"))
                        .nickname("관리자" + i)
                        .role(MemberRole.ADMIN)
                        .build();

                memberRepository.save(member);
            });

        }

    }
}
