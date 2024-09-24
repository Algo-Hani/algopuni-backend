package algohani.common.dto;

import lombok.Getter;
import lombok.ToString;

/**
 * <h2>BasePageRequestDto</h2>
 *
 * <p>
 * 페이지 요청 DTO의 기본 클래스입니다. 해당 클래스를 상속 받아 페이지 요청 DTO를 생성합니다.
 * </p>
 */
@Getter
@ToString
public class BasePageRequestDto {

    private final Long page;

    private final Long size;

    public BasePageRequestDto(Long page, Long size) {
        this.page = page == null ? 1 : page;
        this.size = size == null ? 10 : size;
    }
}
