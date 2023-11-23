package kig.dashboard.member;

import kig.dashboard.member.dto.MemberInfoDTO;
import kig.dashboard.member.dto.MemberSignUpDTO;
import kig.dashboard.member.dto.MemberUpdateDTO;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.exception.MemberException;
import kig.dashboard.member.exception.MemberExceptionType;
import kig.dashboard.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    static final String PASSWORD = "password";
    static final String EMAIL = "test@gmail.com";
    static final String NICKNAME = "nickname";

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;


    private MemberSignUpDTO setMember() throws Exception {

        MemberSignUpDTO memberSignUpDTO = new MemberSignUpDTO(EMAIL, PASSWORD, NICKNAME, MemberRole.USER.name());
        memberService.signUp(memberSignUpDTO);

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                User.builder()
                        .username(memberSignUpDTO.getUsername())
                        .password(memberSignUpDTO.getPassword())
                        .build(), null, null
        ));
        SecurityContextHolder.setContext(emptyContext);

        return memberSignUpDTO;
    }

    @Test
    public void 회원가입_성공() throws Exception {

        MemberSignUpDTO dto = MemberSignUpDTO.builder()
                .username(EMAIL)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .build();

        memberService.signUp(dto);

        Member member = memberRepository.findByUsername(EMAIL).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        assertThat(member).isNotNull();
        assertThat(member.getUsername()).isEqualTo(EMAIL);
    }

    @Test
    public void 회원가입실패_이메일중복() throws Exception {

        MemberSignUpDTO dto = MemberSignUpDTO.builder()
                .username(EMAIL)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .build();

        memberService.signUp(dto);


        MemberSignUpDTO dto2 = MemberSignUpDTO.builder()
                .username(EMAIL)
                .password("another")
                .nickname("another")
                .build();

        assertThat(assertThrows(MemberException.class,
                () -> memberService.signUp(dto2)).getExceptionType())
                .isEqualTo(MemberExceptionType.ALREADY_EXIST_USERNAME);

    }

    @Test
    public void 회원가입실패_입력하지않은필드존재() {

        MemberSignUpDTO emptyEmail = MemberSignUpDTO.builder()
                .username(null)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .build();

        MemberSignUpDTO emptyPassword = MemberSignUpDTO.builder()
                .username(EMAIL)
                .password(null)
                .nickname(NICKNAME)
                .build();

        MemberSignUpDTO emptyNickname = MemberSignUpDTO.builder()
                .username(EMAIL)
                .password(PASSWORD)
                .nickname(null)
                .build();

        assertThrows(Exception.class, () -> {
            memberService.signUp(emptyEmail);
            memberService.signUp(emptyPassword);
            memberService.signUp(emptyNickname);
        });

    }

    @Test
    public void 회원정보수정_비밀번호수정_성공() throws Exception{

        MemberSignUpDTO memberSignUpDTO = setMember();


        String changedPassword = "changedPassword";
        memberService.updatePassword(PASSWORD, changedPassword);

        Optional<Member> byEmail = memberRepository.findByUsername(memberSignUpDTO.getUsername());
        assertThat(byEmail.get()).isNotNull();

        Member member = byEmail.get();
        assertThat(member.matchPassword(passwordEncoder, changedPassword))
                .isTrue();

    }

    @Test
    public void 회원정보수정_닉네임() throws Exception {

        MemberSignUpDTO memberSignUpDTO = setMember();

        String updateNickname = "변경할래용";
        memberService.update(new MemberUpdateDTO(updateNickname));

        memberRepository.findByUsername(memberSignUpDTO.getUsername()).ifPresent((member -> {
            assertThat(member.getNickname()).isEqualTo(updateNickname);
        }));

    }

    @Test
    public void 회원탈퇴_성공() throws Exception {

        MemberSignUpDTO memberSignUpDTO = setMember();

        memberService.withdraw(PASSWORD);

        assertThrows(Exception.class, () -> {
            memberRepository.findByUsername(memberSignUpDTO.getUsername()).orElseThrow();
        });


    }

    @Test
    public void 회원탈퇴_실패_비밀번호일치안함() throws Exception {

        MemberSignUpDTO memberSignUpDTO = setMember();

        assertThat(assertThrows(MemberException.class, () -> memberService.withdraw("123")).getExceptionType())
                .isEqualTo(MemberExceptionType.WRONG_PASSWORD);
    }

    @Test
    public void 회원정보조회() throws Exception {

        MemberSignUpDTO memberSignUpDto = setMember();
        Member member = memberRepository.findByUsername(memberSignUpDto.getUsername()).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));


        MemberInfoDTO info = memberService.getInfo(member.getId());

        assertThat(info.getUsername()).isEqualTo(memberSignUpDto.getUsername());
        assertThat(info.getNickname()).isEqualTo(memberSignUpDto.getNickname());
    }

    @Test
    public void 내정보조회() throws Exception {

        MemberSignUpDTO memberSignUpDTO = setMember();
        MemberInfoDTO info = memberService.getMyInfo();

        assertThat(info.getUsername()).isEqualTo(memberSignUpDTO.getUsername());
    }
}