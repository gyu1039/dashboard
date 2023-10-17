package kig.dashboard.post;

import kig.dashboard.global.file.service.FileService;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.login.SecurityUtil;
import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.dto.PostInfoDTO;
import kig.dashboard.post.dto.PostPagingDTO;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.dto.PostUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    public void save(PostSaveDTO postSaveDTO) throws Exception {

        Post post = postSaveDTO.toEntity();

        post.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(() -> new Exception("멤버 정보가 없습니다")));

        postSaveDTO.getUploadFile().ifPresent(
                file -> post.updateFilePath(fileService.save(file))
        );

        postRepository.save(post);
    }

    public void update(Long id, PostUpdateDTO postUpdateDTO) {

        Post post = postRepository.findById(id).orElseThrow();

        checkAuthority(post, "tmp");

        postUpdateDTO.getTitle().ifPresent(post::updateTitle);
        postUpdateDTO.getContent().ifPresent(post::updateContent);

        if(post.getFilePath() != null) {
            fileService.delete(post.getFilePath());
        }

        postUpdateDTO.getUploadFile().ifPresentOrElse(
                multipartFile -> post.updateFilePath(fileService.save(multipartFile)),
                () -> post.updateFilePath(null)
        );

    }

    public void delete(Long id) {

        Post post = postRepository.findById(id).orElseThrow();

        checkAuthority(post, new Exception());

        if(post.getFilePath() != null) {
            fileService.delete(post.getFilePath());
        }

        postRepository.delete(post);
    }

    public void checkAuthority(Post post, Object postExceptionType) throws RuntimeException {

        if(!post.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new RuntimeException();
        }
    }

    public PostInfoDTO getPostInfo(Long id) {
        return null;
    }

    public PostPagingDTO postPagingDTO(Pageable pageable, PostSearchCondition postSearchCondition) {
        return null;
    }
}
