package kig.dashboard.member;

import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.MemberRole;
import org.apache.tomcat.util.security.Escape;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    private void after() {
        entityManager.clear();
    }

    @Test
    public void 회원저장_성공() throws Exception {
        Member member = Member.builder()
                .username("test@gmail.com")
                .password("password")
                .nickname("test")
                .build();

        member.addUserAuthority();

        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(() -> new Exception("저장된 회원이 없습니다"));

        assertThat(findMember).isSameAs(savedMember);
        assertThat(findMember).isSameAs(member);
    }

    @Test
    public void 회원저장_실패_ID값이없음() throws Exception {

        Member member = Member.builder()
                .password("test")
                .nickname("test")
                .build();

        member.addUserAuthority();

        assertThrows(Exception.class, () -> {
                memberRepository.save(member);
        });
    }

    @Test
    public void 회원저장_실패_닉네임값이없음() throws Exception {

        Member member = Member.builder()
                .username("test@gmail.com")
                .password("test")
                .build();

        member.addUserAuthority();

        assertThrows(Exception.class, () -> {
            memberRepository.save(member);
        });
    }

    @Test
    public void 회원저장_실패_비밀번호값이없음() throws Exception {

        Member member = Member.builder()
                .username("test@gmail.com")
                .nickname("test")
                .build();

        member.addUserAuthority();

        assertThrows(Exception.class, () -> {
            memberRepository.save(member);
        });
    }

    @Test
    public void 회원가입_실패_중복된아이디() throws Exception {
        Member member1 = Member.builder().username("test").password("12345").nickname("test1").role(MemberRole.USER).build();
        Member member2 = Member.builder().username("test").password("789").nickname("test2").role(MemberRole.ADMIN).build();

        memberRepository.save(member1);

        assertThrows(Exception.class, () -> memberRepository.save(member2));

    }

    @Test
    public void 회원수정_성공() throws Exception {

        Member member1 = Member.builder()
                .username("test").password("1234")
                .nickname("test").role(MemberRole.USER).build();

        memberRepository.save(member1);

        String updatedPassword = "updatedPassword";
        String updatedNickName = "updatedNickName";

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        Member findMember = memberRepository.findById(member1.getId()).orElseThrow(() -> new Exception("저장된 회원이 없습니다."));
        findMember.updatePassword(bCryptPasswordEncoder, updatedPassword);
        findMember.updateNickName(updatedNickName);

        Member findUpdateMember = memberRepository.findById(findMember.getId()).orElseThrow(() -> new Exception("저장된 회원이 없습니다"));

        assertThat(findUpdateMember).isSameAs(findUpdateMember);
        assertThat(bCryptPasswordEncoder.matches(updatedPassword, findUpdateMember.getPassword()));
        assertThat(findUpdateMember.getNickname()).isEqualTo(updatedNickName);

    }

    @Test
    public void 회원삭제_성공() throws Exception {

        Member member = Member.builder().username("test@gmail.com")
                .password("1234")
                .nickname("test")
                .role(MemberRole.USER)
                .build();
        memberRepository.save(member);

        assertThat(memberRepository.existsByUsername(member.getUsername())).isTrue();

        memberRepository.delete(member);
        assertThrows(Exception.class, () -> memberRepository
                .findById(member.getId()).orElseThrow());
    }

    @Test
    public void 회원존재유무_실패() {

        Member member = Member.builder().username("test@gmail.com")
                .password("1234")
                .nickname("test")
                .role(MemberRole.USER)
                .build();
        memberRepository.save(member);

        assertThat(memberRepository.existsByUsername(member.getUsername() + "test")).isFalse();

    }

    @Test
    public void findByUserName확인() {

        String username = "username";
        Member member1 = Member.builder().username(username).password("1234567890").role(MemberRole.USER).nickname("NickName1").build();
        memberRepository.save(member1);


        //when, then
        assertThat(memberRepository.findByUsername(username).get().getUsername()).isEqualTo(member1.getUsername());
        assertThat(memberRepository.findByUsername(username).get().getId()).isEqualTo(member1.getId());
        assertThrows(Exception.class,
                () -> memberRepository.findByUsername(username+"123")
                        .orElseThrow(() -> new Exception()));
    }

    @Test
    public void 회원가입_생성시간등록() {

        Member member = Member.builder().username("test@gamil.com")
                .password("1234").nickname("test")
                .role(MemberRole.USER).build();

        memberRepository.save(member);

        Member findmember = memberRepository.findById(member.getId()).orElseThrow();

        assertThat(findmember.getCreatedDate()).isNotNull();
        assertThat(findmember.getLastModifiedDate()).isNotNull();

        assertThat(member.getCreatedDate()).isNotNull();

    }

}