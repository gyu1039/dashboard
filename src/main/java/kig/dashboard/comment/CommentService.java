package kig.dashboard.comment;

import kig.dashboard.comment.exception.CommentException;
import kig.dashboard.comment.exception.CommentExceptionType;
import kig.dashboard.comment.dto.CommentSaveDTO;
import kig.dashboard.comment.dto.CommentUpdateDTO;
import kig.dashboard.global.exception.BaseException;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.exception.MemberException;
import kig.dashboard.member.exception.MemberExceptionType;
import kig.dashboard.member.login.SecurityUtil;
import kig.dashboard.post.PostRepository;
import kig.dashboard.post.exception.PostException;
import kig.dashboard.post.exception.PostExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public void save(Long postId, CommentSaveDTO commentSaveDTO) {
        Comment comment = commentSaveDTO.toEntity();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));
        comment.confirmPost(postRepository.findById(postId).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND)));

        commentRepository.save(comment);
    }

    public void saveReComment(Long postId, Long parentId, CommentSaveDTO commentSaveDTO) {

        Comment comment = commentSaveDTO.toEntity();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));
        comment.confirmPost(postRepository.findById(postId).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND)));
        comment.confirmParent(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)));

        commentRepository.save(comment);
    }


    public void update(Long id, CommentUpdateDTO commentUpdateDTO) {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));
        if (!comment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new CommentException(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);
        }

        commentUpdateDTO.getContent().ifPresent(comment::updateContent);
    }

    public void remove(Long id) throws CommentException {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));
        if (!comment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new CommentException(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);
        }
        comment.remove();
        List<Comment> removableList = comment.findRemovableList();
        commentRepository.deleteAll(removableList);
    }
}
