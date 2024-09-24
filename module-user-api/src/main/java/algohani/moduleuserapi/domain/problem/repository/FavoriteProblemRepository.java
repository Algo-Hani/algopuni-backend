package algohani.moduleuserapi.domain.problem.repository;

import algohani.common.entity.FavoriteProblem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteProblemRepository extends JpaRepository<FavoriteProblem, Long> {

    Optional<FavoriteProblem> findByMemberIdAndProblemProblemId(String memberId, Long problemId);
}
