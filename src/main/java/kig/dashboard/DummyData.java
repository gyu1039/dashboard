package kig.dashboard;


import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.Role;
import kig.dashboard.member.entity.RoleMember;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.repository.RoleMemberRepository;
import kig.dashboard.member.repository.RoleRepository;
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


@Component
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



            Member member = Member.builder()
                    .username("test1")
                    .password(passwordEncoder.encode("test"))
                    .nickname("nickname2")
                    .build();

            Role user = Role.builder().name("팀원").build();
            RoleMember roleMember1 = RoleMember.builder()
                    .role(user)
                    .member(member)
                    .build();
            member.addRoleMember(roleMember1);
            memberRepository.save(member);


            Member adminUser = Member.builder()
                    .username("admin@gmail.com")
                    .password(passwordEncoder.encode("pass"))
                    .nickname("nickname1")
                    .build();

            Role admin = Role.builder().name("관리자").build();
            RoleMember adminRole = RoleMember.builder()
                    .role(admin)
                    .member(adminUser)
                    .build();
            adminUser.addRoleMember(adminRole);
            memberRepository.save(adminUser);


            Category free = Category.builder()
                    .name("자유게시판")
                    .build();

            Category notice = Category.builder()
                    .name("공지사항")
                    .build();

            categoryRepository.save(free);
            categoryRepository.save(notice);

            List<Member> list = new ArrayList<>(List.of(member));

            IntStream.rangeClosed(1, 20).forEach((idx) -> {

                Post post = Post.builder().title(format("게시글 %s", idx)).content(format("내용 %s", idx)).category(free).build();
                post.confirmWriter(Objects.requireNonNull(memberRepository.findById(list.get(0).getId()).orElse(null)));
                postRepository.save(post);
            });

            IntStream.rangeClosed(1, 3).forEach((idx) -> {

                Post post = Post.builder().title(format("공지사항 %s", idx)).content(format("공지사항 내용 %s", idx)).category(notice).build();
                post.confirmWriter(Objects.requireNonNull(memberRepository.findById(adminUser.getId()).orElse(null)));
                postRepository.save(post);
            });

        }

    }
}
