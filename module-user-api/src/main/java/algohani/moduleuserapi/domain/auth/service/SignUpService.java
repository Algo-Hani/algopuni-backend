package algohani.moduleuserapi.domain.auth.service;

import algohani.common.entity.Member;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.auth.dto.request.EmailCodeReqDto;
import algohani.moduleuserapi.domain.auth.dto.request.SignUpReqDto;
import algohani.moduleuserapi.domain.auth.entity.EmailCode;
import algohani.moduleuserapi.domain.auth.repository.EmailCodeRepository;
import algohani.moduleuserapi.domain.auth.repository.MemberRepository;
import algohani.moduleuserapi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpService {

    private final EmailService emailService;

    private final MemberRepository memberRepository;

    private final EmailCodeRepository emailCodeRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public void sendEmailCode(EmailCodeReqDto.Send send) {
        // 이메일 중복 검사
        memberRepository.findById(send.email())
            .ifPresent(member -> {
                throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
            });

        // 이메일 인증 코드 전송
        String randomCode = sendRandomCode(send.email());

        // 인증 코드 Redis에 저장
        EmailCode emailCode = EmailCode.builder()
            .email(send.email())
            .code(randomCode)
            .build();
        emailCodeRepository.save(emailCode);
    }

    @Transactional(readOnly = true)
    public void verifyEmailCode(EmailCodeReqDto.Verify verify) {
        // 이메일 인증 코드 확인
        EmailCode emailCode = emailCodeRepository.findById(verify.email())
            .orElseThrow(() -> new CustomException(ErrorCode.FAILED_TO_VERIFY_EMAIL));
        if (!emailCode.getCode().equals(verify.code())) {
            throw new CustomException(ErrorCode.FAILED_TO_VERIFY_EMAIL);
        }

        // 인증 코드 확인 처리
        emailCode.verify();
        emailCodeRepository.save(emailCode);
    }

    @Transactional
    public void signup(SignUpReqDto dto) {
        // 이메일 중복 검사
        memberRepository.findById(dto.getEmail())
            .ifPresent(member -> {
                throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
            });

        // 비밀번호 확인
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 이메일 인증 코드 확인
        EmailCode savedEmailCode = getVerifiedEmailCode(dto.getEmail());

        // 비밀번호 암호화
        dto.encodePassword(passwordEncoder);

        // 회원가입
        Member member = SignUpReqDto.toEntity(dto);
        memberRepository.save(member);

        // 이메일 인증 코드 삭제
        emailCodeRepository.delete(savedEmailCode);
    }

    /**
     * 이메일 인증 코드 전송
     */
    private String sendRandomCode(final String email) {
        String randomCode;
        try {
            randomCode = emailService.sendEmail(email);
        } catch (Exception e) {
            log.error("인증번호 전송 실패", e);
            throw new CustomException(ErrorCode.FAILED_TO_SEND_EMAIL);
        }

        return randomCode;
    }

    /**
     * 인증된 이메일 코드 가져오기
     */
    private EmailCode getVerifiedEmailCode(final String email) {
        EmailCode savedEmailCode = emailCodeRepository.findById(email)
            .orElseThrow(() -> new CustomException(ErrorCode.FAILED_TO_VERIFY_EMAIL));
        if (savedEmailCode.isNotVerified()) {
            throw new CustomException(ErrorCode.FAILED_TO_VERIFY_EMAIL);
        }

        return savedEmailCode;
    }
}
