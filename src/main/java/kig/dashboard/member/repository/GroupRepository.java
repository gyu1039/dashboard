package kig.dashboard.member.repository;

import kig.dashboard.member.entity.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<Group, Long> {


    Optional<Group> findByName(String name);
}
