package kig.dashboard.post.dto;

import kig.dashboard.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class PostSaveDTO {


    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @Builder.Default
    private Optional<MultipartFile> uploadFile = Optional.empty();

    public Post toEntity() {
        return Post.builder().title(title).content(content).build();
    }
}
