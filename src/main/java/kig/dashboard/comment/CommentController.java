package kig.dashboard.comment;

import kig.dashboard.comment.dto.CommentSaveDTO;
import kig.dashboard.comment.dto.CommentUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void commentSave(@PathVariable Long postId, CommentSaveDTO commentSaveDTO) {
        commentService.save(postId, commentSaveDTO);
    }

    @PostMapping("/comment/{postId}/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reCommentSave(@PathVariable Long postId, @PathVariable Long commentId,
                              CommentSaveDTO commentSaveDTO) {

        commentService.saveReComment(postId, commentId, commentSaveDTO);
    }

    @PutMapping("/comment/{commentId}")
    public void update(@PathVariable Long commentId, CommentUpdateDTO commentUpdateDTO) {

        commentService.update(commentId, commentUpdateDTO);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deelte(@PathVariable Long commentId) {
        commentService.remove(commentId);
    }
}
