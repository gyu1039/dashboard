package kig.dashboard.comment.dto;

import kig.dashboard.comment.Comment;
import kig.dashboard.member.dto.MemberInfoDTO;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentInfoDTO {

    private final static String DEFAULT_DELETED_MESSAGE = "삭제된 댓글입니다";

    private Long postId;

    private Long commentId;
    private String content;
    private boolean isRemoved;

    private MemberInfoDTO writerDTO;

    private List<ReCommentInfoDTO> reCommentInfoDTOList;


    public CommentInfoDTO(Comment comment, List<Comment> reCommentList) {

        this.postId = comment.getPost().getId();
        this.commentId = comment.getId();

        this.content = comment.getContent();
        if(comment.isRemoved()) {
            this.content = DEFAULT_DELETED_MESSAGE;
        }
        this.isRemoved = comment.isRemoved();

        this.writerDTO = new MemberInfoDTO(comment.getWriter());
        this.reCommentInfoDTOList = reCommentList.stream().map(ReCommentInfoDTO::new).collect(Collectors.toList());
    }

}
