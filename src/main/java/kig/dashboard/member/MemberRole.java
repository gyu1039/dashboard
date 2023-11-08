package kig.dashboard.member;


import lombok.Getter;

@Getter
public enum MemberRole {

    ADMIN("관리자"), USER("사용자");

    private String value;

    MemberRole(String value) {
        this.value = value;
    }

}
