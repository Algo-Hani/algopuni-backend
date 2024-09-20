package algohani.moduleuserapi.domain.auth.repository;

import algohani.moduleuserapi.domain.auth.entity.EmailCode;
import org.springframework.data.repository.CrudRepository;

public interface EmailCodeRepository extends CrudRepository<EmailCode, String> {

}
