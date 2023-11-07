package kig.dashboard;

import kig.dashboard.comment.CommentRepository;
import kig.dashboard.member.entity.Role;
import kig.dashboard.member.entity.RoleMember;
import kig.dashboard.member.repository.GroupRepository;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.entity.Group;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.repository.RoleMemberRepository;
import kig.dashboard.member.repository.RoleRepository;
import kig.dashboard.post.entity.Category;
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
        private final RoleMemberRepository roleMemberRepository;
        private final RoleRepository roleRepository;
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


            Member normalUser = Member.builder()
                    .username("test1")
                    .password(passwordEncoder.encode("test"))
                    .nickname("nickname2")
                    .group(it2)
                    .build();
            normalUser.setGroup(it2);

            Role user = Role.builder().name("팀원").build();
            roleRepository.save(user);

            RoleMember roleMember1 = RoleMember.builder()
                    .role(user)
                    .member(normalUser)
                    .build();

            normalUser.addRoleMember(roleMember1);

            memberRepository.save(normalUser);

            Member normalUser2 = Member.builder()
                    .username("test2")
                    .password(passwordEncoder.encode("test"))
                    .nickname("nickname2")
                    .group(it2)
                    .build();
            normalUser2.setGroup(it2);
            RoleMember roleMember2 = RoleMember.builder()
                    .role(user)
                    .member(normalUser)
                    .build();
            normalUser2.addRoleMember(roleMember2);
            memberRepository.save(normalUser2);

            Member adminUser = Member.builder()
                    .username("admin@gmail.com")
                    .password(passwordEncoder.encode("pass"))
                    .nickname("nickname1")
                    .build();
            adminUser.setGroup(it2);
            Role admin = Role.builder().name("관리자").build();
            RoleMember adminRole = RoleMember.builder()
                    .role(admin)
                    .member(adminUser)
                    .build();
            adminUser.addRoleMember(adminRole);
            memberRepository.save(adminUser);

            Category it2team = Category.builder()
                    .name("기업2팀게시판")
                    .build();

            Category free = Category.builder()
                    .name("자유게시판")
                    .build();

            Category notice = Category.builder()
                    .name("공지사항")
                    .build();

            List<Member> list = new ArrayList<>(Arrays.asList(normalUser, normalUser2));
            IntStream.rangeClosed(1, 50).forEach((idx) -> {

                Post post = Post.builder().title(format("게시글 %s", idx)).content(format("내용 %s", idx)).category(it2team).build();
                post.confirmWriter(Objects.requireNonNull(memberRepository.findById(list.get(idx % 2).getId()).orElse(null)));
                postRepository.save(post);
            });

            IntStream.rangeClosed(1, 20).forEach((idx) -> {

                Post post = Post.builder().title(format("게시글 %s", idx)).content(format("내용 %s", idx)).category(free).build();
                post.confirmWriter(Objects.requireNonNull(memberRepository.findById(list.get(idx % 2).getId()).orElse(null)));
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
