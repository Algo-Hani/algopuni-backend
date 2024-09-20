package algohani.moduleuserapi.domain.auth.repository;

import algohani.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

}
