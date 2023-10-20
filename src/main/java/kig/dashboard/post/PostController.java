package kig.dashboard.post;

import kig.dashboard.member.exception.MemberException;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.dto.PostUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePost(PostSaveDTO postSaveDTO) throws MemberException {
        postService.save(postSaveDTO);
    }

    @PutMapping("/post/{postId}")
    public void updatePost(@PathVariable Long postId, PostUpdateDTO postUpdateDTO) {
        postService.update(postId, postUpdateDTO);
    }

    @DeleteMapping("/post/{postId}")
    public void deeltePost(@PathVariable Long postId) {
        postService.delete(postId);
    }

}
