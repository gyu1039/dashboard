package kig.dashboard.post.dto;

import kig.dashboard.post.entity.Category;
import kig.dashboard.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
@Slf4j
public class PostSaveDTO {


    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

//    @NotBlank(message = "카테고리를 선택해주세요")
//    private Category category;

    public Post toEntity() {
        Post post = Post.builder().title(title).content(content)
                .build();

        return post;
    }
}
