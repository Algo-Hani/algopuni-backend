package algohani.moduleuserapi.domain.docker.dto;

import org.apache.commons.lang3.StringUtils;

public record DockerResultDto(
    String standardOutputLogs,
    String standardErrorLogs
) {

    public boolean isFailed() {
        return StringUtils.isNotBlank(this.standardErrorLogs);
    }
}
