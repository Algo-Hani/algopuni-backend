package algohani.moduleuserapi.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenDto(
    AccessTokenDto accessToken,
    RefreshTokenDto refreshToken
) {

    @Builder
    public record AccessTokenDto(
        String accessToken,
        Long expiresIn
    ) {

    }

    @Builder
    public record RefreshTokenDto(
        String refreshToken,
        Long expiresIn
    ) {

        /**
         * 만료까지 남은 시간을 초 단위로 반환
         */
        public int getExpiresInSecond() {
            return (int) ((expiresIn - System.currentTimeMillis()) / 1000);
        }
    }
}
