package algohani.moduleuserapi.domain.auth.repository;

import algohani.common.entity.Member;
import algohani.common.enums.SocialType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByIdAndSocialType(String id, SocialType socialType);

}
