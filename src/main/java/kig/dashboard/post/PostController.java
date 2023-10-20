package kig.dashboard.post;

import kig.dashboard.member.exception.MemberException;
import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.dto.PostUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePost(@Valid @ModelAttribute PostSaveDTO postSaveDTO) throws MemberException {
        postService.save(postSaveDTO);
    }

    @PutMapping("/post/{postId}")
    public void updatePost(@PathVariable Long postId, @ModelAttribute PostUpdateDTO postUpdateDTO) {
        postService.update(postId, postUpdateDTO);
    }

    @DeleteMapping("/post/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.delete(postId);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity getInfo(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostInfo(postId));
    }

    @GetMapping("/post")
    public ResponseEntity search(Pageable pageable, PostSearchCondition postSearchCondition) {
        return ResponseEntity.ok(postService.getPostList(pageable, postSearchCondition));
    }
}
