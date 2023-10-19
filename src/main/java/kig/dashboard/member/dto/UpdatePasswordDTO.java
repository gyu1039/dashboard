package kig.dashboard.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class UpdatePasswordDTO {

    @NotBlank(message = "기존 비밀번호를 입력해주세요") @Size(min=8)
    private String checkPassword;

    @NotBlank(message = "변경할 비밀번호를 입력해주세요") @Size(min=8)
    private String toBePassword;
}
