package kig.dashboard.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data @Builder @AllArgsConstructor
public class PostUpdateDTO {

    private Optional<String> title;
    private Optional<String> content;
    private Optional<MultipartFile> uploadFile;


}
