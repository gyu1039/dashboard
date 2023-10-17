package kig.dashboard.post.dto;

import kig.dashboard.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class PostSaveDTO {

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    Optional<MultipartFile> uploadFile;

    public Post toEntity() {
        return Post.builder().title(title).content(content).build();
    }
}
