package algohani.moduleuserapi.domain.problem.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import algohani.common.dto.ApiResponse.Status;
import algohani.common.dto.PageResponseDto;
import algohani.common.enums.LanguageType;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto.Search;
import algohani.moduleuserapi.domain.problem.service.ProblemService;
import algohani.moduleuserapi.global.dto.ResponseText;
import algohani.moduleuserapi.global.exception.ErrorCode;
import algohani.moduleuserapi.restdocs.ApiDocumentUtils;
import com.epages.restdocs.apispec.ConstrainedFields;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProblemController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureRestDocs
@WithMockUser
class ProblemControllerTest {

    @MockBean
    private ProblemService problemService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("문제 목록 조회 API")
    class 문제_목록_조회_API {

        private final ConstrainedFields fields = new ConstrainedFields(ProblemReqDto.Search.class);

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            ProblemResDto.Search resDto = new Search(1L, "title", 1, 1, 1, true);

            given(problemService.getProblemsWithPaging(any())).willReturn(new PageResponseDto<>(1, 20, 10, 200, Collections.singletonList(resDto)));

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/problems")
                .param("page", "1")
                .param("title", "문제명")
                .param("levels", "0", "1", "2", "3", "4", "5")
                .param("languages", "C", "JAVA", "PYTHON2", "PYTHON3", "JAVASCRIPT")
                .param("statuses", "UNSOLVED", "SOLVING", "SOLVED_SELF", "SOLVED_WITH_HELP")
                .param("order", "RECENT")
                .param("favorite", "Y")
                .contentType(MediaType.APPLICATION_JSON)
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalPages").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(200))
                .andExpect(jsonPath("$.data.result").isArray());

            actions
                .andDo(document("problems-search",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 목록 조회 API")
                            .queryParameters(
                                parameterWithName("page").optional().type(SimpleType.NUMBER).description("페이지"),
                                parameterWithName("title").optional().description("문제명"),
                                parameterWithName("levels").optional().type(SimpleType.NUMBER).description("레벨(0 ~ 6) - 복수 선택 가능"),
                                parameterWithName("languages").optional().description("언어(C, JAVA, PYTHON2, PYTHON3, JAVASCRIPT) - 복수 선택 가능"),
                                parameterWithName("statuses").optional().description("상태(UNSOLVED, SOLVING, SOLVED_SELF, SOLVED_WITH_HELP) - 복수 선택 가능"),
                                parameterWithName("order").optional().description("정렬(RECENT, ACCEPTANCE_DESC, ACCEPTANCE_ASC) - 기본값: RECENT"),
                                parameterWithName("favorite").optional().description("즐겨찾기 여부(Y/N) - Y면 즐겨찾기한 문제만, N이면 즐겨찾기하지 않은 문제만, Null이면 전체 문제 조회")
                            )
                            .responseFields(
                                fields.withPath("status").description("상태"),
                                fields.withPath("statusCode").description("상태 코드"),
                                fields.withPath("timestamp").description("타임스탬프"),
                                fields.withPath("data.page").type(SimpleType.NUMBER).description("페이지"),
                                fields.withPath("data.size").type(SimpleType.NUMBER).description("페이지 크기"),
                                fields.withPath("data.totalPages").type(SimpleType.NUMBER).description("전체 페이지 수"),
                                fields.withPath("data.totalElements").type(SimpleType.NUMBER).description("전체 데이터 수"),
                                fields.withPath("data.result[].problemId").type(SimpleType.NUMBER).description("문제 ID"),
                                fields.withPath("data.result[].title").description("문제명"),
                                fields.withPath("data.result[].level").type(SimpleType.NUMBER).description("레벨"),
                                fields.withPath("data.result[].successCount").type(SimpleType.NUMBER).description("성공 수"),
                                fields.withPath("data.result[].successRate").type(SimpleType.NUMBER).description("성공률"),
                                fields.withPath("data.result[].isFavorite").type(SimpleType.BOOLEAN).description("즐겨찾기 여부")
                            )
                            .requestSchema(Schema.schema("ProblemReqDto.Search"))
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(problemService).should().getProblemsWithPaging(any());
        }
    }

    @Nested
    @DisplayName("문제 즐겨찾기 추가 API")
    class 문제_즐겨찾기_추가_API {

        private final long problemId = 1L;

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            willDoNothing().given(problemService).addFavoriteProblem(problemId);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/problems/{problemId}/favorite", problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value(ResponseText.ADD_FAVORITE_SUCCESS.getMessage()));

            actions
                .andDo(document("문제 즐겨찾기 추가 성공",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 즐겨찾기 추가 API")
                            .pathParameters(
                                parameterWithName("problemId").description("문제 ID")
                            )
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(problemService).should().addFavoriteProblem(problemId);
        }

        @Test
        @DisplayName("실패 - 문제가 없는 경우")
        void ₩실패_문제가_없는_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.PROBLEM_NOT_FOUND)).given(problemService).addFavoriteProblem(problemId);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/problems/{problemId}/favorite", problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROBLEM_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.PROBLEM_NOT_FOUND.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PROBLEM_NOT_FOUND.getCode()));

            actions
                .andDo(document("문제 즐겨찾기 추가 실패 - 문제가 없는 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 즐겨찾기 추가 API")
                            .pathParameters(
                                parameterWithName("problemId").description("문제 ID")
                            )
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("errorName").description("에러 이름"),
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(problemService).should().addFavoriteProblem(problemId);
        }
    }

    @Nested
    @DisplayName("문제 즐겨찾기 해제 API")
    class 문제_즐겨찾기_해제_API {

        private final long problemId = 1L;

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            willDoNothing().given(problemService).removeFavoriteProblem(problemId);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/problems/{problemId}/favorite", problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value(ResponseText.REMOVE_FAVORITE_SUCCESS.getMessage()));

            actions
                .andDo(document("문제 즐겨찾기 해제 성공",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 즐겨찾기 해제 API")
                            .pathParameters(
                                parameterWithName("problemId").description("문제 ID")
                            )
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(problemService).should().removeFavoriteProblem(problemId);
        }

        @Test
        @DisplayName("실패 - 문제가 없는 경우")
        void 실패_문제가_없는_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.PROBLEM_NOT_FOUND)).given(problemService).removeFavoriteProblem(problemId);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/problems/{problemId}/favorite", problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROBLEM_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.PROBLEM_NOT_FOUND.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PROBLEM_NOT_FOUND.getCode()));

            actions
                .andDo(document("문제 즐겨찾기 해제 실패 - 문제가 없는 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 즐겨찾기 해제 API")
                            .pathParameters(
                                parameterWithName("problemId").description("문제 ID")
                            )
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("errorName").description("에러 이름"),
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(problemService).should().removeFavoriteProblem(problemId);
        }
    }

    @Nested
    @DisplayName("문제 상세 조회 API")
    class 문제_상세_조회_API {

        private final long problemId = 1L;

        private final String language = "JAVA";

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            // given
            ProblemResDto.RelatedInfo resDto = new ProblemResDto.RelatedInfo("title", "description", "restriction", "ioExample", "ioDescription", Collections.singletonList(LanguageType.JAVA), true);

            given(problemService.getProblem(anyLong(), any())).willReturn(resDto);

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/problems/{problemId}", problemId)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON)
            );

            actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.name()))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.description").value("description"))
                .andExpect(jsonPath("$.data.restriction").value("restriction"))
                .andExpect(jsonPath("$.data.ioExample").value("ioExample"))
                .andExpect(jsonPath("$.data.ioDescription").value("ioDescription"))
                .andExpect(jsonPath("$.data.languageTypes").isArray())
                .andExpect(jsonPath("$.data.isFavorite").value(true));

            actions
                .andDo(document("문제 상세 조회",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 상세 조회 API")
                            .pathParameters(
                                parameterWithName("problemId").description("문제 ID")
                            )
                            .queryParameters(
                                parameterWithName("language").optional().description("언어")
                            )
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("timestamp").description("타임스탬프"),
                                fieldWithPath("data.title").description("제목"),
                                fieldWithPath("data.description").description("설명"),
                                fieldWithPath("data.restriction").description("제한 사항"),
                                fieldWithPath("data.ioExample").description("입출력 예시"),
                                fieldWithPath("data.ioDescription").description("입출력 설명"),
                                fieldWithPath("data.languageTypes").description("언어 타입"),
                                fieldWithPath("data.isFavorite").description("즐겨찾기 여부")
                            )
                            .responseSchema(Schema.schema("ProblemResDto.RelatedInfo"))
                            .build()
                        )
                    )
                );

            then(problemService).should().getProblem(anyLong(), any());
        }

        @Test
        @DisplayName("실패 - 문제가 없는 경우")
        void 실패_문제가_없는_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.PROBLEM_NOT_FOUND)).given(problemService).getProblem(anyLong(), any());

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/problems/{problemId}", problemId)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON)
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROBLEM_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.PROBLEM_NOT_FOUND.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PROBLEM_NOT_FOUND.getCode()));

            actions
                .andDo(document("문제 상세 조회 실패 - 문제가 없는 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 상세 조회 API")
                            .pathParameters(
                                parameterWithName("problemId").description("문제 ID")
                            )
                            .queryParameters(
                                parameterWithName("language").optional().description("언어")
                            )
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("errorName").description("에러 이름"),
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(problemService).should().getProblem(anyLong(), any());
        }

        @Test
        @DisplayName("실패 - 지원하지 않는 언어인 경우")
        void 실패_지원하지_않는_언어인_경우() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.LANGUAGE_NOT_SUPPORTED)).given(problemService).getProblem(anyLong(), any());

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/problems/{problemId}", problemId)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON)
            );

            actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(Status.ERROR.name()))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(ErrorCode.LANGUAGE_NOT_SUPPORTED.getMessage()))
                .andExpect(jsonPath("$.errorName").value(ErrorCode.LANGUAGE_NOT_SUPPORTED.getName()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.LANGUAGE_NOT_SUPPORTED.getCode()));

            actions
                .andDo(document("문제 상세 조회 실패 - 지원하지 않는 언어인 경우",
                        ApiDocumentUtils.getNoAuthDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        resource(ResourceSnippetParameters.builder()
                            .tag("문제")
                            .description("문제 상세 조회 API")
                            .pathParameters(
                                parameterWithName("problemId").description("문제 ID")
                            )
                            .queryParameters(
                                parameterWithName("language").optional().description("언어")
                            )
                            .responseFields(
                                fieldWithPath("status").description("상태"),
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("errorName").description("에러 이름"),
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("timestamp").description("타임스탬프")
                            )
                            .responseSchema(Schema.schema("ApiResponse"))
                            .build()
                        )
                    )
                );

            then(problemService).should().getProblem(anyLong(), any());
        }
    }
}