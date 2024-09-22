package algohani.moduleuserapi.domain.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private LogoutService logoutService;

    @Nested
    @DisplayName("로그아웃")
    class 로그아웃 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // when
            logoutService.logout(new MockHttpServletResponse());

            // then
            then(redisTemplate).should().delete(any(String.class));
        }
    }
}