package kig.dashboard.post;

import kig.dashboard.comment.Comment;
import kig.dashboard.global.config.BaseTimeEntity;
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
public class Post extends BaseTimeEntity {

    @Id
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

    @ManyToOne
    @JoinColumn
    private Member writer;

    @Builder
    public Post(String title, String content, Member writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
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

}
