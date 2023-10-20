package kig.dashboard.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class CommentUpdateDTO {

    Optional<String> content;
}
