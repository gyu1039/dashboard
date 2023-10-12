package kig.dashboard.member.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberDTO {

    private long id;
    private String email;
    private String password;
    private String name;
    private Boolean del;
    private LocalDate regDate;
}
