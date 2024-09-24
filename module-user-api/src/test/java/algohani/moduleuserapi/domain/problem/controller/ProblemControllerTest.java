package algohani.moduleuserapi.domain.problem.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import algohani.common.dto.ApiResponse.Status;
import algohani.common.dto.PageResponseDto;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto.Search;
import algohani.moduleuserapi.domain.problem.service.ProblemService;
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
            ProblemResDto.Search resDto = new Search(1L, "title", 1, 1, 1);

            given(problemService.getProblemsWithPaging(any())).willReturn(new PageResponseDto<>(1, 20, 10, 200, Collections.singletonList(resDto)));

            // when & then
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/problems")
                .param("page", "1")
                .param("title", "문제명")
                .param("levels", "0", "1", "2", "3", "4", "5")
                .param("languages", "C", "JAVA", "PYTHON2", "PYTHON3", "JAVASCRIPT")
                .param("statuses", "UNSOLVED", "SOLVING", "SOLVED_SELF", "SOLVED_WITH_HELP")
                .param("order", "RECENT")
                .param("bookmarked", "Y")
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
                                parameterWithName("bookmarked").optional().description("북마크 여부(Y/N) - 기본값: N")
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
                                fields.withPath("data.result[].successRate").type(SimpleType.NUMBER).description("성공률")
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
}