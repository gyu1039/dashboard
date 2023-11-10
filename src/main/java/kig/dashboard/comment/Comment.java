package kig.dashboard.comment;

import kig.dashboard.post.entity.Post;
import kig.dashboard.global.domain.BaseTimeEntity;
import kig.dashboard.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
@Slf4j
public class Comment extends BaseTimeEntity {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Lob
    @Column(nullable = false)
    private String content;

    private boolean isRemoved;

    // 부모 댓글을 삭제해도 자식 댓글은 남아 있음 //
    @OneToMany(mappedBy = "parent")
    private List<Comment> childList = new ArrayList<>();

    /**
     * 연관관계 편의 메서드
     */

    public void confirmWriter(Member writer) {
        this.writer = writer;
        writer.addComment(this);
    }

    public void confirmPost(Post post) {
        this.post = post;
        post.addComment(this);
    }

    public void confirmParent(Comment parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void addChild(Comment child) {
        childList.add(child);
    }


    public void updateContent(String content) {
        this.content = content;
    }

    public void remove() {
        this.isRemoved = true;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isRemoved=" + isRemoved +
                '}';
    }

    @Builder
    public Comment(Member writer, Post post, Comment parent, String content) {
        this.writer = writer;
        this.post = post;
        this.parent = parent;
        this.content = content;
    }

    public List<Comment> findCommentsToErase() {

        List<Comment> result = new ArrayList<>();

        Optional.ofNullable(this.parent).ifPresentOrElse(

                parentComment -> {
                    if(parentComment.isRemoved && parentComment.isAllChildRemoved()) {
                        result.addAll(parentComment.getChildList());
                        result.add(parentComment);
                    }
                },

                () -> {
                    if(isAllChildRemoved()) {

                        result.add(this);
                        result.addAll(this.getChildList());
                    } else {
                        log.info("{}", "여기가 출력되니?");
                    }
                }
        );

        return result;
    }

    private boolean isAllChildRemoved() {

        return getChildList().stream()
                .map(Comment::isRemoved)
                .filter(isRemoved -> !isRemoved)
                .findAny().orElse(true);
    }
}
