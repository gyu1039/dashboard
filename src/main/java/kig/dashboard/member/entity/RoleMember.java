package kig.dashboard.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Table(name = "role_member")
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    public void setMember(Member member) {
        this.member = member;
        member.getRoleMembers().add(this);
    }

    public void setRole(Role role) {
        this.role = role;
        role.getMemberList().add(this);
    }

    public RoleMember(Member member, Role role) {
        setMember(member);
        setRole(role);
    }
}

