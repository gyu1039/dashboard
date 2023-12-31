package kig.dashboard.member.entity;

import kig.dashboard.comment.Comment;
import kig.dashboard.global.domain.BaseTimeEntity;
import kig.dashboard.member.MemberRole;
import kig.dashboard.post.entity.Post;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Builder @AllArgsConstructor
@Entity @Table(name = "members")
public class Member extends BaseTimeEntity {

    @Id @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String username;

    @Column
    @NotBlank
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 1000)
    private String refreshToken;

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<RoleMember> roleMembers = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private MemberRole role;

//    @JoinColumn
//    @ManyToOne
//    private Group group;


    /*
    public void setGroup(Group group) {
        this.group = group;
        group.getMemberList().add(this);
    }

    public void initGroup(GroupRepository groupRepository) {
        this.group = groupRepository.findByName("기본그룹").orElse(null);

        if(group != null) {
            group.getMemberList().add(this);
        }

    }

    */
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public List<String> getRoles() {
        return roleMembers.stream().map((e) -> e.getRole().getName()).collect(Collectors.toList());
    }

    /**
     * 회원 정보 수정
     */
    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateNickName(String nickname) {
        this.nickname = nickname;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    /**
     * 비밀번호 변경, 회원 탈퇴 시 비밀번호의 일치 여부 확인
     */
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {
        return passwordEncoder.matches(checkPassword, getPassword());
    }


    /**
     * 연관관계 편의
     */


    public void addPost(Post post) {
        postList.add(post);
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }

    public void addRoleMember(RoleMember roleMember) {
        roleMembers.add(roleMember);
    }
}
