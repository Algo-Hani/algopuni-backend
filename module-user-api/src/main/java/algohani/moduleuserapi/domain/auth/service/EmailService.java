package algohani.moduleuserapi.domain.auth.service;

import algohani.common.utils.RandomUtils;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;

    public String sendEmail(final String email) throws MessagingException {
        final String randomCode = RandomUtils.generateRandomString(8);
        MimeMessage mimeMessage = createEmailForm(email, randomCode);
        javaMailSender.send(mimeMessage);

        return randomCode;
    }

    private MimeMessage createEmailForm(final String email, final String randomCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("ALGOPUNI 회원가입 인증 코드");
        message.setFrom(fromEmail);
        message.setText(randomCode);

        return message;
    }
}
