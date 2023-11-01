package kig.dashboard.member.entity;

import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.repository.RoleMemberRepository;
import kig.dashboard.member.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RoleMemberTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleMemberRepository roleMemberRepository;

    @Autowired
    RoleRepository roleRepository;


    @Test
    public void 멤버에_역할_저장해보기() {

        Role 테스트_역할1 = Role.builder()
                .name("테스트 역할1")
                .build();

        Role 테스트_역할2 = Role.builder()
                .name("테스트 역할2")
                .build();

        Role 테스트_역할3 = Role.builder()
                .name("테스트 역할3")
                .build();

        roleRepository.save(테스트_역할1);
        roleRepository.save(테스트_역할2);
        roleRepository.save(테스트_역할3);

        Member test1 = Member.builder()
                .username("test1")
                .password("1234")
                .nickname("test")
                .build();

        Member test2 = Member.builder()
                .username("test2")
                .password("1234")
                .nickname("test")
                .build();

        Member test3 = Member.builder()
                .username("test3")
                .password("1234")
                .nickname("test")
                .build();

        memberRepository.save(test1);
        memberRepository.save(test2);
        memberRepository.save(test3);

        roleMemberRepository.save(new RoleMember(test1, 테스트_역할3));
        roleMemberRepository.save(new RoleMember(test1, 테스트_역할1));

        roleMemberRepository.save(new RoleMember(test2, 테스트_역할2));
        roleMemberRepository.save(new RoleMember(test2, 테스트_역할3));

        roleMemberRepository.save(new RoleMember(test3, 테스트_역할3));



    }
}