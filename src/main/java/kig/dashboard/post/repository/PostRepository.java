package kig.dashboard.post.repository;

import kig.dashboard.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

    Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
