package by.psu.vs.mono.repositories;

import by.psu.vs.mono.model.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface GroupRepository extends CrudRepository<Group, Long> {
}
