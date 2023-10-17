package kig.dashboard.member.dto;

import kig.dashboard.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoDTO {

    private String username;
    private String nickname;

    @Builder
    public MemberInfoDTO(Member member) {
        this.username = member.getUsername();
        this.nickname = member.getNickname();
    }

}
