package kig.dashboard.member.dto;


import kig.dashboard.member.MemberRole;
import kig.dashboard.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class MemberSignUpDTO {

    @NotBlank(message = "아이디를 입력해주세요") @Size(min=4, max=25)
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요") @Size(min=4)
    private String password;

    @NotBlank(message = "별명을 입력해주세요")
    private String nickname;

    @NotBlank(message = "역할을 선택하세요")
    private String role;

    public MemberSignUpDTO(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public Member toEntity() {
        return Member.builder()
                .username(this.username)
                .password(this.password)
                .role(MemberRole.valueOf(role))
                .nickname(this.nickname).build();
    }

}
