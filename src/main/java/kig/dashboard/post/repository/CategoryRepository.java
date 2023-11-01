package kig.dashboard.post.repository;

import kig.dashboard.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  {

    Category findByName(String name);
}
