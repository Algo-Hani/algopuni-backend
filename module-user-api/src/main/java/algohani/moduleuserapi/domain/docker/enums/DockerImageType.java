package algohani.moduleuserapi.domain.docker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DockerImageType {
    JAVA("openjdk:17"),
    PYTHON("python:3"),
    JAVASCRIPT("node:20");

    private final String imageName;
}
