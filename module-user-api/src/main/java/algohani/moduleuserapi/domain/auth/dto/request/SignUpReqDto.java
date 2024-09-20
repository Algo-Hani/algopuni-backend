package algohani.moduleuserapi.domain.auth.dto.request;

import algohani.common.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
public class SignUpReqDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+]).{8,16}$", message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8~16자로 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+]).{8,16}$", message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8~16자로 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z가-힣\\d]{4,10}$", message = "닉네임은 한글, 영문, 숫자를 포함한 4~10자로 입력해주세요.")
    private String nickname;

    /**
     * 비밀번호 암호화
     */
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    /**
     * SignUpReqDto -> Member
     */
    public static Member toEntity(SignUpReqDto dto) {
        return Member.builder()
            .id(dto.getEmail())
            .password(dto.getPassword())
            .nickname(dto.getNickname())
            .build();
    }
}
