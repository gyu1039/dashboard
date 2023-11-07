package kig.dashboard.member.entity;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = {"name"})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "role")
    private List<RoleMember> memberList = new ArrayList<>();

}
