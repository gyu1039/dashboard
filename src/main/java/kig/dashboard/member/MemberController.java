package kig.dashboard.member;

import kig.dashboard.member.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:8081/"})
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody MemberSignUpDTO memberSignUpDTO) throws Exception {

        memberService.signUp(memberSignUpDTO);
    }

    @PutMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public void updateBasicInfo(@Valid @RequestBody MemberUpdateDTO memberUpdateDTO) throws Exception {
        memberService.update(memberUpdateDTO);
    }

    @PutMapping("/member/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) throws Exception {
        memberService.updatePassword(updatePasswordDTO.getCheckPassword(), updatePasswordDTO.getToBePassword());
    }

    @DeleteMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@Valid @RequestBody MemberWithdrawDTO memberWithdrawDTO) throws Exception {
        memberService.withdraw(memberWithdrawDTO.getCheckPassword());
    }

    @GetMapping("/member")
    public ResponseEntity getMyInfo(HttpServletResponse response) throws Exception {

        MemberInfoDTO info = memberService.getInfo();
        return new ResponseEntity(info, HttpStatus.OK);
    }
}
