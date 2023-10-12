package kig.dashboard.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kig.dashboard.comment.Comment;
import kig.dashboard.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

@Getter
@Entity @Table(name = "boards")
@NoArgsConstructor
public class Board {

    @Id @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    @NotNull
    private Member writer;

    @Column
    @NotBlank
    private String title;

    @Column
    @NotBlank
    private String content;

    @Column
    @NotNull
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "boardId", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Board(Member writer, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
