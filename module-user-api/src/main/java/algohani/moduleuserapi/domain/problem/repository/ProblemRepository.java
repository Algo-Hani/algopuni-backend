package algohani.moduleuserapi.domain.problem.repository;

import algohani.common.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {

}
