package algohani.moduleuserapi.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

@Builder
public record TokenDto(
    AccessTokenDto accessToken,
    RefreshTokenDto refreshToken
) {

    @Builder
    public record AccessTokenDto(
        String token,
        Long expiresIn
    ) {

    }

    @Builder
    public record RefreshTokenDto(
        String token,
        Long expiresIn
    ) {

        /**
         * 만료까지 남은 시간을 초 단위로 반환
         */
        @JsonIgnore
        public int getExpiresInSecond() {
            return (int) ((expiresIn - System.currentTimeMillis()) / 1000);
        }
    }
}
