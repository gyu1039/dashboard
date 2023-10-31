package kig.dashboard.post.dto;

import kig.dashboard.post.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimplePostInfo {

    private Long postId;

    private String title;
    private String content;
    private String writerName;
    private String createdDate;

    public SimplePostInfo(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.writerName = post.getWriter().getUsername();
        this.createdDate = post.getCreatedDate().toString();
    }
}
