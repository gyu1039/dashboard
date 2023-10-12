package kig.dashboard.member.entity;

public enum MemberRole {

    ADMIN("관리자"), USER("이용자");

    private String value;

    MemberRole(String value) {
        this.value = value;
    }
}
