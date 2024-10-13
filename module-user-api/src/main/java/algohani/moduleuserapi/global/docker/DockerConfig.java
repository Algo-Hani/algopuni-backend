package algohani.moduleuserapi.global.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient dockerClient() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withRegistryEmail("dndry1072@naver.com")
            .withRegistryPassword("jhan00254!")
            .withRegistryUsername("potatowoong")
            .withDockerHost("tcp://43.203.240.232:2375")
            .build();

        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();

        return DockerClientImpl.getInstance(config, dockerHttpClient);
    }
}
