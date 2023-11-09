package kig.dashboard.comment;

import kig.dashboard.comment.exception.CommentException;
import kig.dashboard.comment.exception.CommentExceptionType;
import kig.dashboard.comment.dto.CommentSaveDTO;
import kig.dashboard.comment.dto.CommentUpdateDTO;
import kig.dashboard.global.exception.BaseExceptionType;
import kig.dashboard.member.MemberRole;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.MemberService;
import kig.dashboard.member.dto.MemberSignUpDTO;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.exception.MemberException;
import kig.dashboard.member.exception.MemberExceptionType;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.repository.PostRepository;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.exception.PostException;
import kig.dashboard.post.exception.PostExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    Member member;

    private void clear() {
        em.flush();
        em.clear();
    }

    private Long savePost() {
        String title = "제목";
        String content = "내용";

        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());
        Post save = postRepository.save(postSaveDTO.toEntity());
        clear();

        return save.getId();
    }

    private Long saveComment(Long postId) {

        Optional<Post> byId = postRepository.findById(postId);

        Comment comment = Comment.builder()
                .content("댓글")
                .post(byId.get())
                .writer(member)
                .build();

        Long id = commentRepository.save(comment).getId();
        clear();
        return id;
    }

    private Long saveReComment(Long parentId) {

        Comment parent = commentRepository.findById(parentId).orElse(null);

        Comment comment = Comment.builder()
                .writer(member)
                .content("댓글").parent(parent).build();
        parent.addChild(comment);
        clear();

        return commentRepository.save(comment).getId();
    }

    private static final String USERNAME = "username";
    private static final String PASSWORD = "PASSWORD";


    @BeforeEach
    public void signUpAndSetAuthentication() throws Exception {


        memberService.signUp(new MemberSignUpDTO(USERNAME, PASSWORD, "test", MemberRole.USER.name()));

        member = memberRepository.findByUsername(USERNAME).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .build(), null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private void anotherSignUpAndSetAuthentication() throws Exception {
        memberService.signUp(new MemberSignUpDTO("USERNAME1","PASSWORD123","nickName", MemberRole.USER.name()));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username("USERNAME1")
                                .password("PASSWORD123")
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    @Test
    public void 댓글저장_성공() {

        Long postId = savePost();
        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("댓글");

        commentService.save(postId, commentSaveDTO);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(1);

    }

    @Test
    public void 자식댓글저장_성공() {

        Long postId = savePost();
        Long parentId = saveComment(postId);

        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("자식댓글");


        commentService.saveReComment(postId, parentId, commentSaveDTO);

        List resultList = em.createQuery("select c from Comment c order by c.createdDate").getResultList();
        assertThat(resultList.size()).isEqualTo(2);
    }

    @Test
    public void 댓글저장실패_게시글X() {

        Long postId = savePost();
        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("댓글");

        assertThat(assertThrows(PostException.class, () -> commentService.save(postId + 1, commentSaveDTO)).getExceptionType()).isEqualTo(PostExceptionType.POST_NOT_FOUND);
    }

    @Test
    public void 자식댓글저장실패_게시글이없음() {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("댓글");

        assertThat(
                assertThrows(PostException.class, () -> commentService.saveReComment(postId + 123, parentId, commentSaveDTO)).getExceptionType()
        ).isEqualTo(PostExceptionType.POST_NOT_FOUND);
    }

    @Test
    public void 업데이트성공() {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        Long reCommentId = saveReComment(parentId);


        commentService.update(reCommentId, new CommentUpdateDTO(Optional.ofNullable("업데이트")));

        Comment comment = commentRepository
                .findById(reCommentId).orElse(null);

        assertThat(comment.getContent()).isEqualTo("업데이트");
    }

    @Test
    public void 업데이트실패() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        Long reCommentId = saveReComment(parentId);

        anotherSignUpAndSetAuthentication();

        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.update(reCommentId, new CommentUpdateDTO(Optional.ofNullable("업데이트")))).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);

    }

    @Test
    public void 삭제실패() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        Long reCommentId = saveReComment(parentId);

        anotherSignUpAndSetAuthentication();

        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.remove(reCommentId)).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);

    }


    /**
     * 댓글을 삭제할 때 자식 댓글이 남아 있는경우
     * DB와 화면에서 지우지 않고, "삭제된 댓글입니다" 라고 표시하기
     */
    @Test
    public void 댓글삭제_대댓글존재() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        saveReComment(parentId);
        saveReComment(parentId);
        saveReComment(parentId);


        assertThat(commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size())
                .isEqualTo(3);

        commentService.remove(parentId);

        Comment findComment = commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(3);
    }

    /**
     * 자식댓글이 존재하지 않는 댓글을 삭제할때
     */
    @Test
    public void 댓글삭제_자식댓글없음() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);

        commentService.remove(commentId);

        assertThat(commentRepository.findAll().size()).isSameAs(0);

        assertThat(assertThrows(CommentException.class,
                () -> commentRepository.findById(commentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).getExceptionType())
                .isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);
    }

    /**
     * 대댓글이 모두 삭제된 경우 댓글을 삭제 할때
     *
     */
    @Test
    public void 댓글삭제_자식댓글이모두삭제된경우() throws Exception {

        Long postId = savePost();
        Long id = saveComment(postId);
        Long recommentId1 = saveReComment(id);
        Long recommentId2 = saveReComment(id);
        Long recommentId3 = saveReComment(id);
        Long recommentId4 = saveReComment(id);

        assertThat(commentRepository.findById(id).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size())
                .isSameAs(4);

        commentService.remove(recommentId1);
        assertThat(commentRepository.findById(recommentId1).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved())
                .isTrue();

        commentService.remove(recommentId2);
        assertThat(commentRepository.findById(recommentId2).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved())
                .isTrue();

        commentService.remove(recommentId3);
        assertThat(commentRepository.findById(recommentId3).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved())
                .isTrue();

        commentService.remove(recommentId4);
        assertThat(commentRepository.findById(recommentId4).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved())
                .isTrue();


        commentService.remove(id);
        LongStream.rangeClosed(id, recommentId4).forEach(idd -> {
            assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(idd).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))));
        });
    }

    /**
     * 자식 댓글을 삭제 하는 경우
     * 부모 댓글이 존재할 때
     * 자식 댓글은 내용만 삭제, DB에서는 삭제 X
     */
    @Test
    public void 자식댓글삭제할때_부모댓글이삭제되지않은경우() throws Exception {
        Long postId = savePost();
        Long parentId = saveComment(postId);
        Long childId1 = saveReComment(parentId);

        commentService.remove(childId1);

        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(childId1).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isFalse();
        assertThat(commentRepository.findById(childId1).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
    }

    /**
     * 자식 댓글을 삭제할 때
     * 부모댓글이 삭제되어 있고, 자식댓글들도 모두 삭제된 경우
     * 부모를 포함한 모든 댓글을 DB에서 삭제
     */
    @Test
    public void 자식댓글삭제할때_모든댓글삭제된경우() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        Long childId1 = saveReComment(parentId);
        Long childId2 = saveReComment(parentId);
        Long childId3 = saveReComment(parentId);

        commentService.remove(parentId);
        commentService.remove(childId1);
        commentService.remove(childId3);

        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(3);

        commentService.remove(childId2);

        assertThat(commentRepository.findAll().size()).isEqualTo(0);
        LongStream.rangeClosed(parentId, childId3).forEach(id ->
                assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)))
        );

    }

    /**
     * 자식 댓글을 삭제하는 경우
     * 부모 댓글이 삭제되어 있고, 다른 자식댓글 중 삭제되지 않은 댓글이 남아 있는 경우
     */
    @Test
    public void 자식댓글삭제_다른자식댓글이남아있는경우() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        Long childId1 = saveReComment(parentId);
        Long childId2 = saveReComment(parentId);
        Long childId3 = saveReComment(parentId);

        commentService.remove(childId3);
        commentService.remove(parentId);

        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isSameAs(3);


        commentService.remove(childId2);
        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();



        assertThat(commentRepository.findById(childId2).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(childId2).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(childId1).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isFalse();
        assertThat(commentRepository.findById(childId3).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();
        assertThat(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();

    }


}