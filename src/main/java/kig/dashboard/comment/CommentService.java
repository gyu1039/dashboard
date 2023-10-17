package kig.dashboard.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Comment findById(Long id) throws Exception {
        return commentRepository.findById(id).orElseThrow(() -> new Exception("댓글이 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public void remove(Long id) throws Exception {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new Exception("댓글이 없습니다."));
        comment.remove();
        List<Comment> removableList = comment.findRemovableList();
        commentRepository.deleteAll(removableList);
    }
}
