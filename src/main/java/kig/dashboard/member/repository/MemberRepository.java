package kig.dashboard.member.repository;

import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.RoleMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByUsername(String email);

    boolean existsByUsername(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

}
