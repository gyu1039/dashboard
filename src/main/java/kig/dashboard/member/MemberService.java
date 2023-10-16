package kig.dashboard.member;

import kig.dashboard.member.dto.MemberInfoDTO;
import kig.dashboard.member.dto.MemberSignUpDTO;
import kig.dashboard.member.dto.MemberUpdateDTO;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.login.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(MemberSignUpDTO memberSignUpDTO) throws Exception {

        Member member = memberSignUpDTO.toEntity();
        member.addUserAuthority();
        member.encodePassword(passwordEncoder);

        if (memberRepository.findByUsername(memberSignUpDTO.getUsername()).isPresent()) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }

        memberRepository.save(member);
    }

    public void update(MemberUpdateDTO memberUpdateDTO) throws Exception {

        Member member = isMemberExists();
        memberUpdateDTO.getNickname().ifPresent(member::updateNickName);
    }



    public void updatePassword(String checkPassword, String toBePassword) throws Exception {

        Member member = isMemberExists();

        if (!member.matchPassword(passwordEncoder, checkPassword)) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(passwordEncoder, toBePassword);
    }

    public void withdraw(String checkPassword) throws Exception {

        Member member = isMemberExists();

        if(!member.matchPassword(passwordEncoder, checkPassword)) {
            throw new Exception("비밀번호가 일치하지 않습니다");
        }

        memberRepository.delete(member);

    }

    private Member isMemberExists() throws Exception {
        return memberRepository
                .findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
    }

    public MemberInfoDTO getInfo() throws Exception {
        Member findMember = isMemberExists();
        return new MemberInfoDTO(findMember);
    }
}
