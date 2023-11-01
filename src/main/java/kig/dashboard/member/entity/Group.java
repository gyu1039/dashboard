package kig.dashboard.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "groups")
@Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "group")
    private List<Member> memberList = new ArrayList<>();
}
