package kig.dashboard.member.entity;

public enum MemberRole {

    ADMIN("관리자"), USER("이용자"), TEAM1("1팀"), TEAM2("2팀"), TEAM3("3팀");

    private String value;

    MemberRole(String value) {
        this.value = value;
    }
}
