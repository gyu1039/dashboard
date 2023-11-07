package kig.dashboard.post.entity;

import kig.dashboard.comment.Comment;
import kig.dashboard.global.domain.BaseTimeEntity;
import kig.dashboard.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Post extends BaseTimeEntity {

    @Id @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 40)
    @NotBlank
    private String title;

    @Lob
    @Column
    @NotBlank
    private String content;

    @Column
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Post(String title, String content, String filePath, Member writer, Category category, List<Comment> commentList) {
        this.title = title;
        this.content = content;
        this.filePath = filePath;
        this.writer = writer;
        this.category = category;
        this.commentList = commentList;
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();


    /**
     * 연관관계 편의 메서드
     */
    public void confirmWriter(Member writer) {
        this.writer = writer;
        writer.addPost(this);
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }

    public void confirmCategory(Category category) {
        this.category = category;
        category.addPost(this);
    }


    /**
     * 내용수정
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }



}
