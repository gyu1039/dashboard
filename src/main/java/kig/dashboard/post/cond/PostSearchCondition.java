package kig.dashboard.post.cond;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PostSearchCondition {

    private String title;
    private String content;
}
