package kig.dashboard.post.repository;

import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {

    Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable);
}
