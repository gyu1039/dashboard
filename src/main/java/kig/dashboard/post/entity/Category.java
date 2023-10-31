package kig.dashboard.post.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private String value;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Post> postList = new ArrayList<>();

    public void addPost(Post post) {
        this.postList.add(post);
    }


}
