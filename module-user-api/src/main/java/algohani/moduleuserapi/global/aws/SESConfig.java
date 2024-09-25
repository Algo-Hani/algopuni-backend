package algohani.moduleuserapi.global.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesAsyncClient;

@Configuration
public class SESConfig {

    @Value("${aws.ses.access-key}")
    private String accessKey;

    @Value("${aws.ses.secret-key}")
    private String secretKey;

    /**
     * SES 비동기 클라이언트 빈 등록
     */
    @Bean
    public SesAsyncClient sesAsyncClient() {
        StaticCredentialsProvider staticCredentials = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        );

        return SesAsyncClient.builder()
            .credentialsProvider(staticCredentials)
            .region(Region.AP_NORTHEAST_2)
            .build();
    }
}
