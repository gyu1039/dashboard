package kig.dashboard.post.dto;

import kig.dashboard.comment.Comment;
import kig.dashboard.comment.dto.CommentInfoDTO;
import kig.dashboard.member.dto.MemberInfoDTO;
import kig.dashboard.post.entity.Category;
import kig.dashboard.post.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostInfoDTO {

    private Long postId;
    private Category category;
    private String title;
    private String content;
    private String filePath;

    private MemberInfoDTO writerDTO;

    private List<CommentInfoDTO> commentInfoDTOList;


    public PostInfoDTO(Post post) {

        this.postId = post.getId();
        this.category = post.getCategory();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.filePath = post.getFilePath();


        this.writerDTO = new MemberInfoDTO(post.getWriter());

        Map<Comment, List<Comment>> commentListMap = post.getCommentList().stream()
                .filter(comment -> comment.getParent() != null)
                .collect(Collectors.groupingBy(Comment::getParent));

        commentInfoDTOList = commentListMap.keySet().stream()
                .map(comment -> new CommentInfoDTO(comment, commentListMap.get(comment)))
                .collect(Collectors.toList());
    }



}
