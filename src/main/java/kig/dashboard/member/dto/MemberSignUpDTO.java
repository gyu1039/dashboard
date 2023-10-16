package kig.dashboard.member.dto;


import kig.dashboard.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class MemberSignUpDTO {

    private String username;
    private String password;
    private String nickname;

    public Member toEntity() {
        return Member.builder()
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname).build();
    }

}
