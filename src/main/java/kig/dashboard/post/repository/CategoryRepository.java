package kig.dashboard.post.repository;

import kig.dashboard.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Long> {

    Category findByName(String name);
}
