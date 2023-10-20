package kig.dashboard;

import kig.dashboard.comment.Comment;
import kig.dashboard.comment.CommentRepository;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.MemberRole;
import kig.dashboard.post.Post;
import kig.dashboard.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
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
        private final CommentRepository commentRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void save() {
            Member save1 = memberRepository.save(
                    Member.builder()
                            .username("test1@gmail.com")
                            .password(passwordEncoder.encode("test"))
                            .nickname("nickname1")
                            .role(MemberRole.USER)
                            .build());

            Member save2 = memberRepository.save(
                    Member.builder()
                            .username("test2@gmail.com")
                            .password(passwordEncoder.encode("test"))
                            .nickname("nickname2")
                            .role(MemberRole.USER)
                            .build());

            Member save3 = memberRepository.save(
                    Member.builder()
                            .username("test3@gmail.com")
                            .password(passwordEncoder.encode("test"))
                            .nickname("nickname3")
                            .role(MemberRole.USER)
                            .build());
            List<Member> list = new ArrayList<>(Arrays.asList(save1, save2, save3));

            IntStream.rangeClosed(1, 50).forEach((idx) -> {

                Post post = Post.builder().title(format("게시글 %s", idx)).content(format("내용 %s", idx)).build();
                post.confirmWriter(Objects.requireNonNull(memberRepository.findById(list.get(idx % 3).getId()).orElse(null)));
                postRepository.save(post);
            });

            IntStream.rangeClosed(1, 150).forEach((idx) -> {

                Comment comment = Comment.builder().content("댓글" + idx).build();
                comment.confirmWriter(Objects.requireNonNull(memberRepository.findById(list.get(idx % 3).getId()).orElse(null)));

                comment.confirmPost(Objects.requireNonNull(postRepository.findById(parseLong(valueOf(idx % 50 + 1))).orElse(null)));
                commentRepository.save(comment);
            });

            commentRepository.findAll().forEach(comment -> {

                IntStream.rangeClosed(1, 50).forEach(i -> {
                    Comment reComment = Comment.builder().content("자식 댓글" + i).build();
                    reComment.confirmWriter(Objects.requireNonNull(memberRepository.findById(list.get(i % 3).getId()).orElse(null)));
                    reComment.confirmPost(comment.getPost());
                    reComment.confirmParent(comment);
                    commentRepository.save(reComment);
                });
            });
        }

    }
}
