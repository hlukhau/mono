package by.psu.vs.mono.repositories;

import by.psu.vs.mono.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface StudentsRepository extends CrudRepository<Student, Long> {
}
