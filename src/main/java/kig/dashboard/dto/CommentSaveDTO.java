package kig.dashboard.dto;

import kig.dashboard.comment.Comment;
import kig.dashboard.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class CommentSaveDTO {

    String content;

    public Comment toEntity() {
        return Comment.builder().content(content).build();
    }
}
