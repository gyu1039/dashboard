package kig.dashboard.member;

import kig.dashboard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.OptionalInt;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String email);

    boolean existsByUsername(String email);

    Optional<Member> findByRefreshToken(String refreshToken);
}
