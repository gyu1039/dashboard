package kig.dashboard.post.dto;

import kig.dashboard.post.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostPagingDTO {

    private int totalPageCount;
    private int currentPageNum;
    private long totalElementCount;
    private int currentPageElementCount;

    private List<SimplePostInfo> simpleDTOList = new ArrayList<>();

    public PostPagingDTO(Page<Post> searchResults) {
        this.totalPageCount = searchResults.getTotalPages();
        this.currentPageNum = searchResults.getNumber();
        this.totalElementCount = searchResults.getTotalElements();
        this.currentPageElementCount = searchResults.getNumberOfElements();
        this.simpleDTOList = searchResults.getContent().stream().map(SimplePostInfo::new).collect(Collectors.toList());
        


    }
}
