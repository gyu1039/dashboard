package kig.dashboard.member.dto;

import kig.dashboard.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @AllArgsConstructor
public class MemberInfoDTO {

    private final String username;
    private final String nickname;

    public MemberInfoDTO(Member member) {
        this.username = member.getUsername();
        this.nickname = member.getNickname();
    }

}
