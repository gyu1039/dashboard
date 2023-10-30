package kig.dashboard.post;

import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.exception.MemberException;
import kig.dashboard.member.exception.MemberExceptionType;
import kig.dashboard.member.login.SecurityUtil;
import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.dto.PostInfoDTO;
import kig.dashboard.post.dto.PostPagingDTO;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.dto.PostUpdateDTO;
import kig.dashboard.post.exception.PostException;
import kig.dashboard.post.exception.PostExceptionType;
import kig.dashboard.post.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Transactional
    public void save(PostSaveDTO postSaveDTO) throws MemberException {


        Post post = postSaveDTO.toEntity();

        post.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));


        postSaveDTO.getUploadFile().ifPresent(
                file -> post.updateFilePath(fileService.save(file))
        );


        postRepository.save(post);
    }

    @Transactional
    public void update(Long id, PostUpdateDTO postUpdateDTO) {

        log.info("{}", postUpdateDTO);
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostException(PostExceptionType.POST_NOT_FOUND)
        );

        checkAuthority(post, PostExceptionType.NOT_AUTHORITY_UPDATE_POST);

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

    @Transactional
    public void delete(Long id) {

        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostException(PostExceptionType.POST_NOT_FOUND)
        );

        checkAuthority(post, PostExceptionType.NOT_AUTHORITY_DELETE_POST);

        if(post.getFilePath() != null) {
            fileService.delete(post.getFilePath());
        }

        postRepository.delete(post);
    }

    public void checkAuthority(Post post, PostExceptionType postExceptionType) throws RuntimeException {

        if(!post.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new PostException(postExceptionType);
        }
    }

    @Transactional(readOnly = true)
    public PostInfoDTO getPostInfo(Long id) {
        return new PostInfoDTO(postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public PostPagingDTO getPostList(PostSearchCondition postSearchCondition, Pageable pageable) {
        return new PostPagingDTO(postRepository.search(postSearchCondition, pageable));
    }
}
