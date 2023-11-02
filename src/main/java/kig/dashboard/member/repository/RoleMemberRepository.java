package kig.dashboard.member.repository;

import kig.dashboard.member.entity.RoleMember;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleMemberRepository extends CrudRepository<RoleMember, Long> {

}

