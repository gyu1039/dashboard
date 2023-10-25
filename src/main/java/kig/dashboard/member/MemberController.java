package kig.dashboard.member;

import kig.dashboard.member.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public void signUp(@Valid @RequestBody MemberSignUpDTO memberSignUpDTO) throws Exception {

        memberService.signUp(memberSignUpDTO);
    }

    @PutMapping("/member")
    public void updateBasicInfo(@Valid @RequestBody MemberUpdateDTO memberUpdateDTO) throws Exception {
        memberService.update(memberUpdateDTO);
    }

    @PutMapping("/member/password")
    public void updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) throws Exception {
        memberService.updatePassword(updatePasswordDTO.getCheckPassword(), updatePasswordDTO.getToBePassword());
    }

    @DeleteMapping("/member")
    public void withdraw(@Valid @RequestBody MemberWithdrawDTO memberWithdrawDTO) throws Exception {
        memberService.withdraw(memberWithdrawDTO.getCheckPassword());
    }

    @GetMapping("/member")
    public ResponseEntity getMyInfo(HttpServletResponse response) throws Exception {

        MemberInfoDTO info = memberService.getMyInfo();
        return new ResponseEntity(info, HttpStatus.OK);
    }

    @GetMapping("/api/checkuser/{username}")
    public boolean checkDuplicatedId(@PathVariable String username) {

        return memberService.isIdDuplicated(username);
    }
}
