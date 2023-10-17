package kig.dashboard.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


import java.util.Objects;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    private Long saveComment() {
        Comment comment = Comment.builder()
                .content("댓글")
                .build();
        return commentRepository.save(comment).getId();
    }

    private Long saveReComment(Long parentId) {
        Comment parent = commentRepository.findById(parentId).orElse(null);
        Comment comment = Comment.builder()
                .content("댓글").parent(parent).build();
        parent.addChild(comment);

        return commentRepository.save(comment).getId();
    }


    /**
     * 댓글을 삭제할 때 자식 댓글이 남아 있는경우
     * DB와 화면에서 지우지 않고, "삭제된 댓글입니다" 라고 표시하기
     */
    @Test
    public void 댓글삭제_대댓글존재() throws Exception {

        Long parentId = saveComment();
        saveReComment(parentId);
        saveReComment(parentId);
        saveReComment(parentId);

        assertThat(commentService.findById(parentId).getChildList().size()).isEqualTo(3);

        commentService.remove(parentId);

        Comment findComment = commentService.findById(parentId);
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(3);
    }

    /**
     * 자식댓글이 존재하지 않는 댓글을 삭제할때
     */
    @Test
    public void 댓글삭제_자식댓글없음() throws Exception {

        Long comment = saveComment();

        commentService.remove(comment);

        assertThat(commentService.findAll().size()).isSameAs(0);
        assertThat(assertThrows(Exception.class, () -> commentService.findById(comment)).getMessage()).isEqualTo("댓글이 없습니다.");
    }

    /**
     * 대댓글이 모두 삭제된 경우 댓글을 삭제 할때
     *
     */
    @Test
    public void 댓글삭제_자식댓글이모두삭제된경우() throws Exception {
        Long id = saveComment();
        Long recommentId1 = saveReComment(id);
        Long recommentId2 = saveReComment(id);
        Long recommentId3 = saveReComment(id);
        Long recommentId4 = saveReComment(id);

        assertThat(commentService.findById(id).getChildList().size()).isSameAs(4);

        commentService.remove(recommentId1);
        assertThat(commentService.findById(recommentId1).isRemoved());

        commentService.remove(recommentId2);
        assertThat(commentService.findById(recommentId2).isRemoved());

        commentService.remove(recommentId3);
        assertThat(commentService.findById(recommentId3).isRemoved());

        commentService.remove(recommentId4);
        assertThat(commentService.findById(recommentId4).isRemoved());


        commentService.remove(id);
        assertThrows(Exception.class, () -> commentService.findById(id));
    }

    /**
     * 자식 댓글을 삭제 하는 경우
     * 부모 댓글이 존재할 때
     * 자식 댓글은 내용만 삭제, DB에서는 삭제 X
     */
    @Test
    public void 자식댓글삭제할때_부모댓글이삭제되지않은경우() throws Exception {
        Long parentId = saveComment();
        Long childId1 = saveReComment(parentId);

        commentService.remove(childId1);

        assertThat(commentService.findById(parentId)).isNotNull();
        assertThat(commentService.findById(childId1)).isNotNull();
        assertThat(commentService.findById(parentId).isRemoved()).isFalse();
        assertThat(commentService.findById(childId1).isRemoved()).isTrue();
    }

    /**
     * 자식 댓글을 삭제할 때
     * 부모댓글이 삭제되어 있고, 자식댓글들도 모두 삭제된 경우
     * 부모를 포함한 모든 댓글을 DB에서 삭제
     */
    @Test
    public void 자식댓글삭제할때_모든댓글삭제된경우() throws Exception {

        Long parentId = saveComment();
        Long childId1 = saveReComment(parentId);
        Long childId2 = saveReComment(parentId);
        Long childId3 = saveReComment(parentId);

        commentService.remove(parentId);
        commentService.remove(childId1);
        commentService.remove(childId3);

        assertThat(commentService.findById(parentId)).isNotNull();
        assertThat(commentService.findById(parentId).getChildList().size()).isEqualTo(3);

        commentService.remove(childId2);

        LongStream.rangeClosed(parentId, childId3).forEach(id ->
                assertThrows(Exception.class, () -> commentService.findById(id))
        );

    }

    /**
     * 자식 댓글을 삭제하는 경우
     * 부모 댓글이 삭제되어 있고, 다른 자식댓글 중 삭제되지 않은 댓글이 남아 있는 경우
     */
    @Test
    public void 자식댓글삭제_다른자식댓글이남아있는경우() throws Exception {
        Long parentId = saveComment();
        Long childId1 = saveReComment(parentId);
        Long childId2 = saveReComment(parentId);
        Long childId3 = saveReComment(parentId);

        commentService.remove(childId3);
        commentService.remove(parentId);

        assertThat(commentService.findById(parentId)).isNotNull();
        assertThat(commentService.findById(parentId).getChildList().size()).isSameAs(3);


        commentService.remove(childId2);
        assertThat(commentService.findById(parentId)).isNotNull();


        assertThat(commentService.findById(childId2)).isNotNull();
        assertThat(commentService.findById(childId2).isRemoved()).isTrue();
        assertThat(commentService.findById(childId1).isRemoved()).isFalse();
        assertThat(commentService.findById(childId3).getId()).isNotNull();
        assertThat(commentService.findById(parentId).getId()).isNotNull();

    }


}