package kig.dashboard.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PostUpdateDTO {

    @Builder.Default
    private Optional<String> title = Optional.empty();

    @Builder.Default
    private Optional<String> content  = Optional.empty();

    @Builder.Default
    private Optional<MultipartFile> uploadFile  = Optional.empty();


}
