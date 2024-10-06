package algohani.moduleuserapi.domain.problem.repository;

import algohani.common.entity.Parameter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {

    List<Parameter> findByProblemProblemId(long problemId);

}
