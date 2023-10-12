package kig.dashboard.comment;

import kig.dashboard.board.Board;
import kig.dashboard.member.entity.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "comments")
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @ManyToOne
    @JoinColumn
    private Board boardId;

    @Column
    @ManyToOne
    private Member writer;

    @Column
    private String content;

    @Column
    private LocalDateTime createdAt;

    @Builder
    public Comment(Board boardId, Member writer, String content, LocalDateTime createdAt) {
        this.boardId = boardId;
        this.writer = writer;
        this.content = content;
        this.createdAt = createdAt;
    }
}
