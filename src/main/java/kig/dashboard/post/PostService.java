package kig.dashboard.post;

import kig.dashboard.member.MemberRole;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.exception.MemberException;
import kig.dashboard.member.exception.MemberExceptionType;
import kig.dashboard.global.config.login.SecurityUtil;
import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.dto.PostInfoDTO;
import kig.dashboard.post.dto.PostPagingDTO;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.dto.PostUpdateDTO;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.exception.PostException;
import kig.dashboard.post.exception.PostExceptionType;
import kig.dashboard.post.file.service.FileService;
import kig.dashboard.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;


    @Transactional
    public PostPagingDTO getList(Pageable pageable) {
        Page<Post> allByOrderByCreatedDateDesc = postRepository.findAllByOrderByCreatedDateDesc(pageable);
        return new PostPagingDTO(allByOrderByCreatedDateDesc);
    }

    @Transactional
    public void save(PostSaveDTO postSaveDTO, MultipartFile multipartFile) throws MemberException {


        Post post = postSaveDTO.toEntity();

        post.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));


        if(multipartFile != null) {
            post.updateFilePath(fileService.save(multipartFile));
        }

        postRepository.save(post);
    }

    @Transactional
    public void update(Long id, PostUpdateDTO postUpdateDTO, MultipartFile multipartFile) {

        log.info("{}", postUpdateDTO);
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostException(PostExceptionType.POST_NOT_FOUND)
        );

        checkAuthority(post, PostExceptionType.NOT_AUTHORITY_UPDATE_POST);

        if(postUpdateDTO.getTitle() != null) {
            post.updateTitle(postUpdateDTO.getTitle());
        }

        if(postUpdateDTO.getContent() != null) {
            post.updateContent(postUpdateDTO.getContent());
        }

        if(multipartFile != null) {
            post.updateFilePath(fileService.save(multipartFile));
        } else {
            post.updateFilePath(null);
        }

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

        String loginUsername = SecurityUtil.getLoginUsername();
        Member member = memberRepository.findByUsername(loginUsername).get();
        if (member.getRole().name().equals(MemberRole.ADMIN.name())) {
            return;
        }

        if(!post.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new PostException(postExceptionType);
        }
    }

    @Transactional(readOnly = true)
    public PostInfoDTO getPostInfo(Long id) {
        return new PostInfoDTO(postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public PostPagingDTO searchWithConditions(PostSearchCondition postSearchCondition, Pageable pageable) {
        return new PostPagingDTO(postRepository.search(postSearchCondition, pageable));
    }

    @Transactional
    public void save(PostSaveDTO postSaveDTO) {
        save(postSaveDTO, null);
    }
}
