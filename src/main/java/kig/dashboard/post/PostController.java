package kig.dashboard.post;

import kig.dashboard.member.exception.MemberException;
import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.dto.PostUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostController {

    private final PostService postService;

    @Value("${spring.data.web.pageable.default-page-size}")
    private int size;

    @Value("${file.dir}")
    private String filePath;

    @GetMapping("/posts")
    public ResponseEntity<?> postList(@RequestParam(defaultValue = "0", name = "page") int page) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return ResponseEntity.ok(postService.getList(pageRequest));
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePost(@Valid @RequestPart(value = "post") PostSaveDTO postSaveDTO,
                         @RequestPart(value="file", required = false) MultipartFile multipartFile) throws MemberException {

        log.info("savePost: {}", postSaveDTO.toString());
        log.info("file: {}", multipartFile);
        postService.save(postSaveDTO, multipartFile);
    }

    @PutMapping("/posts/{postId}")
    public void updatePost(@PathVariable Long postId, @RequestBody PostUpdateDTO postUpdateDTO) {
        postService.update(postId, postUpdateDTO);
    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.delete(postId);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getInfo(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostInfo(postId));
    }

    @GetMapping("/posts/search")
    public ResponseEntity<?> search(Pageable pageable, PostSearchCondition postSearchCondition) {
        return ResponseEntity.ok(postService.searchWithConditions(postSearchCondition, pageable));
    }

    @GetMapping("/images/{path}")
    public Resource showImage(@PathVariable(name = "path") String path) throws MalformedURLException {
        log.info("{}", path);
        return new UrlResource("file:"+ filePath + path);
    }
}
