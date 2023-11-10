package kig.dashboard.member.dto;

import kig.dashboard.member.MemberRole;
import kig.dashboard.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoDTO {

    private Long memberId;

    private String username;
    private String nickname;
    private MemberRole memberRole;

    public MemberInfoDTO(Member member) {
        this.memberId = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.memberRole = member.getRole();
    }

}
