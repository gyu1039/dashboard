package kig.dashboard.member;

import kig.dashboard.member.dto.MemberInfoDTO;
import kig.dashboard.member.dto.MemberSignUpDTO;
import kig.dashboard.member.dto.MemberUpdateDTO;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.exception.MemberException;
import kig.dashboard.member.exception.MemberExceptionType;
import kig.dashboard.member.login.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(MemberSignUpDTO memberSignUpDTO) throws Exception {

        Member member = memberSignUpDTO.toEntity();
        member.addUserAuthority();
        member.encodePassword(passwordEncoder);

        if (memberRepository.findByUsername(memberSignUpDTO.getUsername()).isPresent()) {
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_USERNAME);
        }

        memberRepository.save(member);
    }

    public void update(MemberUpdateDTO memberUpdateDTO) throws Exception {

        Member member = isMemberExists();
        member.updateNickName(memberUpdateDTO.getNickname());
    }



    public void updatePassword(String checkPassword, String toBePassword) throws Exception {

        Member member = isMemberExists();

        if (!member.matchPassword(passwordEncoder, checkPassword)) {
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        member.updatePassword(passwordEncoder, toBePassword);
    }

    public void withdraw(String checkPassword) throws Exception {

        Member member = isMemberExists();

        if(!member.matchPassword(passwordEncoder, checkPassword)) {
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        memberRepository.delete(member);

    }

    private Member isMemberExists() {
        return memberRepository
                .findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    public MemberInfoDTO getInfo(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return new MemberInfoDTO(member);
    }

    public MemberInfoDTO getMyInfo() throws Exception {
        Member findMember = isMemberExists();
        return new MemberInfoDTO(findMember);
    }

    public boolean isIdDuplicated(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }
}
