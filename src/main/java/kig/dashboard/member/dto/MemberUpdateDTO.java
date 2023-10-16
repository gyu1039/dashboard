package kig.dashboard.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class MemberUpdateDTO {

    private Optional<String> nickname;

}
