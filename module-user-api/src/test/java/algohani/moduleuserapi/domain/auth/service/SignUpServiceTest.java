package algohani.moduleuserapi.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import algohani.common.entity.Member;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.auth.dto.request.EmailCodeReqDto;
import algohani.moduleuserapi.domain.auth.dto.request.EmailCodeReqDto.Send;
import algohani.moduleuserapi.domain.auth.dto.request.SignUpReqDto;
import algohani.moduleuserapi.domain.auth.entity.EmailCode;
import algohani.moduleuserapi.domain.auth.repository.EmailCodeRepository;
import algohani.moduleuserapi.domain.auth.repository.MemberRepository;
import algohani.moduleuserapi.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailCodeRepository emailCodeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SignUpService signUpService;

    @Nested
    @DisplayName("이메일 인증 코드 전송")
    class 이메일_인증_코드_전송 {

        private final String email = "algopuni@algopuni.com";

        private final EmailCodeReqDto.Send send = new Send(email);

        @Test
        @DisplayName("성공")
        void 성공() throws MessagingException {
            // given
            given(memberRepository.findById(any())).willReturn(Optional.empty());
            given(emailService.sendEmail(send.email())).willReturn("randomCode");

            // when
            signUpService.sendEmailCode(send);

            // then
            then(memberRepository).should().findById(email);
            then(emailService).should().sendEmail(email);
            then(emailCodeRepository).should().save(any());
        }

        @Test
        @DisplayName("실패 - 이미 가입된 이메일")
        void 실패_이미_가입된_이메일() {
            // given
            given(memberRepository.findById(any())).willReturn(Optional.of(Member.builder().build()));

            // then
            assertThatThrownBy(() -> signUpService.sendEmailCode(send))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_DUPLICATION);

            // then
            then(memberRepository).should().findById(email);
            then(emailService).shouldHaveNoInteractions();
            then(emailCodeRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 이메일 전송 실패")
        void 실패_이메일_전송_실패() throws MessagingException {
            // given
            given(memberRepository.findById(any())).willReturn(Optional.empty());
            given(emailService.sendEmail(send.email())).willThrow(MessagingException.class);

            // then
            assertThatThrownBy(() -> signUpService.sendEmailCode(send))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_TO_SEND_EMAIL);

            // then
            then(memberRepository).should().findById(email);
            then(emailService).should().sendEmail(email);
            then(emailCodeRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 확인")
    class 이메일_인증_코드_확인 {

        private final String email = "algopuni@algopuni.com";

        private final EmailCodeReqDto.Verify verify = new EmailCodeReqDto.Verify(email, "randomCode");

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            EmailCode emailCode = EmailCode.builder()
                .code("randomCode")
                .build();
            given(emailCodeRepository.findById(verify.email())).willReturn(Optional.of(emailCode));

            // when
            signUpService.verifyEmailCode(verify);

            // then
            then(emailCodeRepository).should().findById(email);
            then(emailCodeRepository).should().save(any());
        }

        @Test
        @DisplayName("실패 - 이메일 인증 코드 불일치")
        void 실패_이메일_인증_코드_불일치() {
            // given
            EmailCode emailCode = EmailCode.builder()
                .code("wrongCode")
                .build();
            given(emailCodeRepository.findById(verify.email())).willReturn(Optional.of(emailCode));

            // then
            assertThatThrownBy(() -> signUpService.verifyEmailCode(verify))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_TO_VERIFY_EMAIL);

            // then
            then(emailCodeRepository).should().findById(email);
            then(emailCodeRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("실패 - 이메일 인증 코드 없음")
        void 실패_이메일_인증_코드_없음() {
            // given
            given(emailCodeRepository.findById(verify.email())).willReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> signUpService.verifyEmailCode(verify))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_TO_VERIFY_EMAIL);

            // then
            then(emailCodeRepository).should().findById(email);
            then(emailCodeRepository).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("회원가입")
    class 회원가입 {

        private final SignUpReqDto signUpReqDto = SignUpReqDto.builder()
            .email("algopuni@algopuni.com")
            .password("password")
            .passwordConfirm("password")
            .nickname("algopuni")
            .build();

        private EmailCode getVerifiedEmailCode() {
            EmailCode emailCode = new EmailCode();
            emailCode.verify();

            return emailCode;
        }

        private EmailCode getNoneVerifiedEmailCode() {
            return EmailCode.builder()
                .code("randomCode")
                .build();
        }

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            given(memberRepository.findById(signUpReqDto.getEmail())).willReturn(Optional.empty());
            given(emailCodeRepository.findById(signUpReqDto.getEmail())).willReturn(Optional.of(getVerifiedEmailCode()));

            // when
            signUpService.signup(signUpReqDto);

            // then
            then(memberRepository).should().findById(signUpReqDto.getEmail());
            then(emailCodeRepository).should().findById(signUpReqDto.getEmail());
            then(memberRepository).should().save(any());
            then(emailCodeRepository).should().delete(any());
        }

        @Test
        @DisplayName("실패 - 이미 가입된 이메일")
        void 실패_이미_가입된_이메일() {
            // given
            given(memberRepository.findById(signUpReqDto.getEmail())).willReturn(Optional.of(Member.builder().build()));

            // then
            assertThatThrownBy(() -> signUpService.signup(signUpReqDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_DUPLICATION);

            // then
            then(memberRepository).should().findById(signUpReqDto.getEmail());
            then(emailCodeRepository).shouldHaveNoInteractions();
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(emailCodeRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void 실패_비밀번호_불일치() {
            // given
            SignUpReqDto wrongSignUpReqDto = SignUpReqDto.builder()
                .email("algopuni@algopuni.com")
                .password("password")
                .passwordConfirm("wrongPassword")
                .nickname("algopuni")
                .build();

            given(memberRepository.findById(wrongSignUpReqDto.getEmail())).willReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> signUpService.signup(wrongSignUpReqDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_MISMATCH);

            // then
            then(memberRepository).should().findById(wrongSignUpReqDto.getEmail());
            then(emailCodeRepository).shouldHaveNoInteractions();
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(emailCodeRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("실패 - 저장된 이메일 인증 코드 없음")
        void 실패_저장된_이메일_인증_코드_없음() {
            // given
            given(memberRepository.findById(signUpReqDto.getEmail())).willReturn(Optional.empty());
            given(emailCodeRepository.findById(signUpReqDto.getEmail())).willReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> signUpService.signup(signUpReqDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_TO_VERIFY_EMAIL);

            // then
            then(memberRepository).should().findById(signUpReqDto.getEmail());
            then(emailCodeRepository).should().findById(signUpReqDto.getEmail());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(emailCodeRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("실패 - 이메일 인증 코드 미인증")
        void 실패_이메일_인증_코드_미인증() {
            // given
            given(memberRepository.findById(signUpReqDto.getEmail())).willReturn(Optional.empty());
            given(emailCodeRepository.findById(signUpReqDto.getEmail())).willReturn(Optional.of(getNoneVerifiedEmailCode()));

            // then
            assertThatThrownBy(() -> signUpService.signup(signUpReqDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_TO_VERIFY_EMAIL);

            // then
            then(memberRepository).should().findById(signUpReqDto.getEmail());
            then(emailCodeRepository).should().findById(signUpReqDto.getEmail());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(emailCodeRepository).shouldHaveNoMoreInteractions();
        }
    }
}