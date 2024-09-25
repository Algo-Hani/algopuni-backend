package algohani.moduleuserapi.domain.auth.service;

import algohani.common.utils.RandomUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    private static final String EMAIL_TEMPLATE_PATH = "static/html/signup_email_template.html"; // 이메일 템플릿 경로

    private static final String LOGO_PATH = "static/image/logo.png"; // 로고 이미지 경로

    private String emailTemplate; // 이메일 템플릿 캐싱

    private final JavaMailSender javaMailSender;

    public String sendEmail(final String email) throws MessagingException, IOException {

        // 랜덤 코드 생성
        final String randomCode = RandomUtils.generateRandomString(8);

        // MimeMessage 생성
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));

        // MimeMessageHelper 생성
        createMimeMessageHelper(message, email, randomCode);

        // 이메일 전송
        javaMailSender.send(message);

        return randomCode;
    }

    /**
     * MimeMessageHelper 생성
     *
     * @param message    MimeMessage
     * @param email      이메일
     * @param randomCode 인증 코드
     * @throws MessagingException 메시지 생성 실패
     * @throws IOException        파일 읽기 실패
     */
    private void createMimeMessageHelper(final MimeMessage message, final String email, final String randomCode) throws MessagingException, IOException {
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        messageHelper.setFrom("Algopuni <" + sender + ">");
        messageHelper.setTo(email);
        messageHelper.setSubject("ALGOPUNI 회원가입 인증 코드");
        messageHelper.setText(getEmailTemplate().replace("{{verification_code}}", randomCode), true);
        messageHelper.addInline("image", new ClassPathResource(LOGO_PATH).getFile());
    }

    /**
     * 이메일 템플릿 반환 (캐싱)
     *
     * @return 이메일 템플릿 문자열
     * @throws IOException 파일 읽기 실패
     */
    private String getEmailTemplate() throws IOException {
        if (emailTemplate == null) {
            emailTemplate = new String(new ClassPathResource(EMAIL_TEMPLATE_PATH).getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
        return emailTemplate;
    }
}
