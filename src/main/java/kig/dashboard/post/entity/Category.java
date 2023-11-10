package kig.dashboard.post.entity;

import kig.dashboard.global.domain.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = {"name"}, callSuper = false)
//@DynamicInsert @DynamicUpdate
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = {
            CascadeType.PERSIST,
            CascadeType.REMOVE
    })
    private List<Post> postList = new ArrayList<>();

    public void addPost(Post post) {
        postList.add(post);
    }

    public void deletePost(Post post) {
        postList.remove(post);
        post.updateCategory(null);
    }


}
