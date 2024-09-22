package algohani.moduleuserapi.domain.auth.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import algohani.common.dto.ApiResponse.Status;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.auth.dto.request.EmailCodeReqDto;
import algohani.moduleuserapi.domain.auth.dto.request.LoginReqDto;
import algohani.moduleuserapi.domain.auth.dto.request.SignUpReqDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.AccessTokenDto;
import algohani.moduleuserapi.domain.auth.service.LoginService;
import algohani.moduleuserapi.domain.auth.service.SignUpService;
import algohani.moduleuserapi.domain.auth.service.TokenRefreshService;
import algohani.moduleuserapi.global.dto.ResponseText;
import algohani.moduleuserapi.global.exception.ErrorCode;
import algohani.moduleuserapi.restdocs.ApiDocumentUtils;
import com.epages.restdocs.apispec.ConstrainedFields;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureRestDocs
@WithMockUser
class AuthControllerTest {

    @MockBean
    private SignUpService signUpService;

    @MockBean
    private LoginService loginService;

    @MockBean
    private TokenRefreshService tokenRefreshService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("이메일 인증 코드 전송 API")
    class 이메일_인증_코드_전송_API {

        private final EmailCodeReqDto.Send send = new EmailCodeReqDto.Send("algopuni@algopuni.com");

        private final ConstrainedFields fields = new ConstrainedFields(EmailCodeReqDto.Send.class);

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            willDoNothing().given(signUpService).sendEmailCode(send);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(send))
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value(ResponseText.VERIFICATION_CODE_SENT.getMessage()));

            actions
                .andDo(document("email-verification-send-code",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("이메일 인증 코드 전송 API")
                            .requestFields(
                                fields.withPath("email").description("이메일")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("EmailCodeReqDto.Send"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().sendEmailCode(send);
        }

        @Test
        @DisplayName("실패 - 이미 가입된 이메일")
        void 실패_이미_가입된_이메일() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.EMAIL_DUPLICATION)).given(signUpService).sendEmailCode(send);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(send))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_DUPLICATION.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.EMAIL_DUPLICATION.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.EMAIL_DUPLICATION.getCode()));

            actions
                .andDo(document("email-verification-send-code-fail-email-duplication",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("이메일 인증 코드 전송 API")
                            .requestFields(
                                fields.withPath("email").description("이메일")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("EmailCodeReqDto.Send"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().sendEmailCode(send);
        }

        @Test
        @DisplayName("실패 - 이메일 전송 실패")
        void 실패_이메일_전송_실패() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.FAILED_TO_SEND_EMAIL)).given(signUpService).sendEmailCode(send);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(send))
            );

            actions
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.message").value(ErrorCode.FAILED_TO_SEND_EMAIL.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.FAILED_TO_SEND_EMAIL.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.FAILED_TO_SEND_EMAIL.getCode()));

            actions
                .andDo(document("email-verification-send-code-fail-failed-to-send-email",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("이메일 인증 코드 전송 API")
                            .requestFields(
                                fields.withPath("email").description("이메일")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("EmailCodeReqDto.Send"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().sendEmailCode(send);
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 확인 API")
    class 이메일_인증_코드_확인_API {

        private final EmailCodeReqDto.Verify verify = new EmailCodeReqDto.Verify("algopuni@algopuni.com", "randomCode");

        private final ConstrainedFields fields = new ConstrainedFields(EmailCodeReqDto.Verify.class);

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            willDoNothing().given(signUpService).verifyEmailCode(verify);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification/verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(verify))
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value(ResponseText.VERIFICATION_CODE_VERIFIED.getMessage()));

            actions
                .andDo(document("email-verification-verify-code",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("이메일 인증 코드 확인 API")
                            .requestFields(
                                fields.withPath("email").description("이메일"),
                                fields.withPath("code").description("인증 코드")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("EmailCodeReqDto.Verify"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().verifyEmailCode(verify);
        }

        @Test
        @DisplayName("실패 - 인증 코드 불일치")
        void 실패_인증_코드_불일치() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.FAILED_TO_VERIFY_EMAIL)).given(signUpService).verifyEmailCode(verify);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification/verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(verify))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getCode()));

            actions
                .andDo(document("email-verification-verify-code-fail-mismatch",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("이메일 인증 코드 확인 API")
                            .requestFields(
                                fields.withPath("email").description("이메일"),
                                fields.withPath("code").description("인증 코드")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("EmailCodeReqDto.Verify"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().verifyEmailCode(verify);
        }

        @Test
        @DisplayName("실패 - 인증 코드가 없는 경우")
        void 실패_인증_코드가_없는_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.FAILED_TO_VERIFY_EMAIL)).given(signUpService).verifyEmailCode(verify);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification/verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(verify))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getCode()));

            actions
                .andDo(document("email-verification-verify-code-fail-no-code",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("이메일 인증 코드 확인 API")
                            .requestFields(
                                fields.withPath("email").description("이메일"),
                                fields.withPath("code").description("인증 코드")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("EmailCodeReqDto.Verify"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().verifyEmailCode(verify);
        }
    }

    @Nested
    @DisplayName("회원가입 API")
    class 회원가입_API {

        private final SignUpReqDto dto = SignUpReqDto.builder()
            .email("algopuni@algopuni.com")
            .password("Password1!")
            .passwordConfirm("Password1!")
            .nickname("algopuni")
            .build();

        private final SignUpReqDto wrongDto = SignUpReqDto.builder()
            .email("algopuni@algopuni.com")
            .password("Password1!")
            .passwordConfirm("Password1!!")
            .nickname("algopuni")
            .build();

        private final ConstrainedFields fields = new ConstrainedFields(SignUpReqDto.class);

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            willDoNothing().given(signUpService).signup(dto);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(dto))
            );

            actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value(ResponseText.SIGN_UP_SUCCESS.getMessage()));

            actions
                .andDo(document("signup",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("회원가입 API")
                            .requestFields(
                                fields.withPath("email").description("이메일"),
                                fields.withPath("password").description("비밀번호"),
                                fields.withPath("passwordConfirm").description("비밀번호 확인"),
                                fields.withPath("nickname").description("닉네임")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("SignUpReqDto"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().signup(any());
        }

        @Test
        @DisplayName("실패 - 이미 가입된 이메일")
        void 실패_이미_가입된_이메일() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.EMAIL_DUPLICATION)).given(signUpService).signup(any());

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(dto))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_DUPLICATION.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.EMAIL_DUPLICATION.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.EMAIL_DUPLICATION.getCode()));

            actions
                .andDo(document("signup-fail-email-duplication",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("회원가입 API")
                            .requestFields(
                                fields.withPath("email").description("이메일"),
                                fields.withPath("password").description("비밀번호"),
                                fields.withPath("passwordConfirm").description("비밀번호 확인"),
                                fields.withPath("nickname").description("닉네임")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("SignUpReqDto"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().signup(any());
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void 실패_비밀번호_불일치() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.PASSWORD_MISMATCH)).given(signUpService).signup(any());

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(wrongDto))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.PASSWORD_MISMATCH.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.PASSWORD_MISMATCH.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PASSWORD_MISMATCH.getCode()));

            actions
                .andDo(document("signup-fail-password-mismatch",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("회원가입 API")
                            .requestFields(
                                fields.withPath("email").description("이메일"),
                                fields.withPath("password").description("비밀번호"),
                                fields.withPath("passwordConfirm").description("비밀번호 확인"),
                                fields.withPath("nickname").description("닉네임")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("SignUpReqDto"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().signup(any());
        }

        @Test
        @DisplayName("실패 - 이메일 인증 코드 확인 실패")
        void 실패_이메일_인증_코드_확인_실패() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.FAILED_TO_VERIFY_EMAIL)).given(signUpService).signup(any());

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(dto))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.FAILED_TO_VERIFY_EMAIL.getCode()));

            actions
                .andDo(document("signup-fail-failed-to-verify-email",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("회원가입")
                            .summary("회원가입 API")
                            .requestFields(
                                fields.withPath("email").description("이메일"),
                                fields.withPath("password").description("비밀번호"),
                                fields.withPath("passwordConfirm").description("비밀번호 확인"),
                                fields.withPath("nickname").description("닉네임")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("SignUpReqDto"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(signUpService).should().signup(any());
        }
    }

    @Nested
    @DisplayName("로그인 API")
    class 로그인_API {

        private final LoginReqDto loginReqDto = LoginReqDto.builder()
            .id("id")
            .password("password")
            .build();

        private final AccessTokenDto accessTokenDto = AccessTokenDto.builder()
            .accessToken("accessToken")
            .expiresIn(new Date().getTime() + 1000 * 60 * 60)
            .build();

        private final ConstrainedFields fields = new ConstrainedFields(LoginReqDto.class);

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            given(loginService.login(any(LoginReqDto.class), any(HttpServletResponse.class))).willReturn(accessTokenDto);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(loginReqDto))
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value(ResponseText.LOGIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.accessToken").value(accessTokenDto.accessToken()))
                .andExpect(jsonPath("$.data.expiresIn").value(accessTokenDto.expiresIn()));

            actions
                .andDo(document("성공",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("로그인")
                            .summary("로그인 API")
                            .requestFields(
                                fields.withPath("id").description("이메일"),
                                fields.withPath("password").description("비밀번호")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("timestamp").description("타임스탬프"),
                                fields.withPath("data.accessToken").description("액세스 토큰"),
                                fields.withPath("data.expiresIn").description("만료 시간")
                            )
                            .requestSchema(Schema.schema("LoginReqDto"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(loginService).should().login(any(LoginReqDto.class), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("실패 - 아이디가 일치하지 않는 경우")
        void 실패_아이디가_일치하지_않는_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.LOGIN_FAILED)).given(loginService).login(any(LoginReqDto.class), any(HttpServletResponse.class));

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(loginReqDto))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.LOGIN_FAILED.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.LOGIN_FAILED.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.LOGIN_FAILED.getCode()));

            actions
                .andDo(document("실패 - 아이디가 일치하지 않는 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("로그인")
                            .summary("로그인 API")
                            .requestFields(
                                fields.withPath("id").description("이메일"),
                                fields.withPath("password").description("비밀번호")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("LoginReqDto"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(loginService).should().login(any(LoginReqDto.class), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("실패 - 비밀번호가 일치하지 않는 경우")
        void 실패_비밀번호가_일치하지_않는_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.LOGIN_FAILED)).given(loginService).login(any(LoginReqDto.class), any(HttpServletResponse.class));

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .content(objectMapper.writeValueAsString(loginReqDto))
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.LOGIN_FAILED.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.LOGIN_FAILED.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.LOGIN_FAILED.getCode()));

            actions
                .andDo(document("실패 - 비밀번호가 일치하지 않는 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("로그인")
                            .summary("로그인 API")
                            .requestFields(
                                fields.withPath("id").description("이메일"),
                                fields.withPath("password").description("비밀번호")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("message").description("메시지"),
                                fields.withPath("errorName").description("에러 이름"),
                                fields.withPath("errorCode").description("에러 코드"),
                                fields.withPath("timestamp").description("타임스탬프")
                            )
                            .requestSchema(Schema.schema("LoginReqDto"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(loginService).should().login(any(LoginReqDto.class), any(HttpServletResponse.class));
        }

        // TODO : 소셜 로그인 사용자인 경우
    }

    @Nested
    @DisplayName("Access Token 갱신 API")
    class Access_Token_갱신_API {

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            AccessTokenDto accessTokenDto = AccessTokenDto.builder()
                .accessToken("accessToken")
                .expiresIn(new Date().getTime() + 1000 * 60 * 60)
                .build();

            given(tokenRefreshService.refresh(any())).willReturn(accessTokenDto);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value(ResponseText.ACCESS_TOKEN_REFRESHED.getMessage()))
                .andExpect(jsonPath("$.data.accessToken").value(accessTokenDto.accessToken()))
                .andExpect(jsonPath("$.data.expiresIn").value(accessTokenDto.expiresIn()));

            actions
                .andDo(document("Access Token 갱신 성공",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("Access Token 갱신")
                            .summary("Access Token 갱신 API")
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("timestamp").description("타임스탬프"),
                                fieldWithPath("data.accessToken").description("액세스 토큰"),
                                fieldWithPath("data.expiresIn").description("만료 시간")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(tokenRefreshService).should().refresh(any());
        }

        @Test
        @DisplayName("실패 - 쿠키에 Refresh Token이 존재하지 않는 경우")
        void 실패_쿠키에_Refresh_Token이_존재하지_않는_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.INVALID_REFRESH_TOKEN)).given(tokenRefreshService).refresh(any());

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
            );

            actions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_REFRESH_TOKEN.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.INVALID_REFRESH_TOKEN.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REFRESH_TOKEN.getCode()));

            actions
                .andDo(document("Access Token 갱신 실패 - 쿠키에 Refresh Token이 존재하지 않는 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("Access Token 갱신")
                            .summary("Access Token 갱신 API")
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("errorName").description("에러 이름"),
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(tokenRefreshService).should().refresh(any());
        }

        @Test
        @DisplayName("실패 - Refresh Token이 만료된 경우")
        void 실패_Refresh_Token이_만료된_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.INVALID_ACCESS_TOKEN)).given(tokenRefreshService).refresh(any());

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
            );

            actions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_ACCESS_TOKEN.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.INVALID_ACCESS_TOKEN.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_ACCESS_TOKEN.getCode()));

            actions
                .andDo(document("Access Token 갱신 실패 - Refresh Token이 만료된 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("Access Token 갱신")
                            .summary("Access Token 갱신 API")
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("errorName").description("에러 이름"),
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(tokenRefreshService).should().refresh(any());
        }
    }
}