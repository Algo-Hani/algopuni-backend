package algohani.moduleuserapi.domain.auth.entity;

import algohani.common.enums.YNFlag;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("emailCode")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailCode {

    @Id
    private String email;

    private String code;

    private YNFlag isVerified;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long timeToLive;

    @Builder
    public EmailCode(String email, String code) {
        this.email = email;
        this.code = code;
        this.timeToLive = 3L; // 5분
        this.isVerified = YNFlag.N;
    }

    public void verify() {
        this.isVerified = YNFlag.Y;
    }

    public boolean isNotVerified() {
        return this.isVerified == YNFlag.N;
    }
}
