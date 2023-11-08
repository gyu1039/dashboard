package kig.dashboard.global.config.login;

import kig.dashboard.member.entity.Member;
import kig.dashboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("{}", "loadUserByUsername 진입");
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("아이디가 조회되지 않습니다"));

        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();
    }

}
