package algohani.common.dto;

import java.util.List;

/**
 * <h2>PageResponseDto</h2>
 *
 * <p>
 * 페이지 응답 DTO입니다. 페이지 정보와 결과를 담고 있습니다.
 * </p>
 */
public record PageResponseDto<T>(
    long page,
    long size,
    long totalPages,
    long totalElements,
    List<T> result
) {

    public static <T> PageResponseDto<T> of(BasePageRequestDto requestDto, long totalElements, List<T> result) {
        return new PageResponseDto<>(
            requestDto.getPage(),
            requestDto.getSize(),
            (int) Math.ceil((double) totalElements / requestDto.getSize()),
            totalElements,
            result
        );
    }
}
