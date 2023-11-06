package kig.dashboard;

import kig.dashboard.comment.CommentRepository;
import kig.dashboard.member.repository.GroupRepository;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.entity.Group;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.repository.RoleRepository;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.String.valueOf;

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
        private final GroupRepository groupRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void save() {


            Group basic = Group.builder()
                    .name("기본그룹")
                    .build();
            groupRepository.save(basic);

            Group it2 = Group.builder()
                    .name("기업IT개발2팀")
                    .build();
            groupRepository.save(it2);

            Member member = Member.builder()
                    .username("admin@gmail.com")
                    .password(passwordEncoder.encode("pass"))
                    .nickname("nickname1")
                    .build();
            member.setGroup(it2);
            Member save1 = memberRepository.save(member);

            Member member1 = Member.builder()
                    .username("test1")
                    .password(passwordEncoder.encode("test"))
                    .nickname("nickname2")
                    .group(it2)
                    .build();
            member1.setGroup(it2);
            Member save2 = memberRepository.save(member1);

            Member member2 = Member.builder()
                    .username("test2")
                    .password(passwordEncoder.encode("test"))
                    .nickname("nickname3")
                    .group(it2)
                    .build();
            member2.setGroup(it2);
            Member save3 = memberRepository.save(member2);

            List<Member> list = new ArrayList<>(Arrays.asList(save1, save2, save3));
            IntStream.rangeClosed(1, 50).forEach((idx) -> {

                Post post = Post.builder().title(format("게시글 %s", idx)).content(format("내용 %s", idx)).build();
                post.confirmWriter(Objects.requireNonNull(memberRepository.findById(list.get(idx % 3).getId()).orElse(null)));
                postRepository.save(post);
            });

        }

    }
}
