package kig.dashboard.comment.dto;

import kig.dashboard.comment.Comment;
import kig.dashboard.member.dto.MemberInfoDTO;
import lombok.Data;

@Data
public class ReCommentInfoDTO {

    private final static String DEFAULT_DELETED_MESSAGE = "삭제된 댓글입니다";

    private Long postId;
    private Long parentId;

    private Long reCommentId;
    private String content;
    private boolean isRemoved;

    private MemberInfoDTO writerDTO;

    public ReCommentInfoDTO(Comment reComment) {

        this.postId = reComment.getPost().getId();
        this.parentId = reComment.getParent().getId();
        this.reCommentId = reComment.getId();
        this.content = reComment.getContent();

        if (reComment.isRemoved()) {
            this.content = DEFAULT_DELETED_MESSAGE;
        }

        this.isRemoved = reComment.isRemoved();
        this.writerDTO = new MemberInfoDTO(reComment.getWriter());
    }
}
