package me.weekbelt.apiserver.directory.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import me.weekbelt.apiserver.department.dto.DepartmentResponse;
import me.weekbelt.apiserver.department.dto.DepartmentSynonymResponse;
import me.weekbelt.apiserver.directory.dto.DirectoryResponse;
import me.weekbelt.apiserver.directory.service.DirectoryQueryService;
import me.weekbelt.persistence.PhoneType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest(DirectoryController.class)
@ExtendWith({RestDocumentationExtension.class})
class DirectoryControllerTest {

    private static final String DIRECTORY_BASE_URL = "/admin/v1/directories";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DirectoryQueryService directoryQueryService;

    @BeforeEach
    public void before(WebApplicationContext ctx, RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
            .apply(documentationConfiguration(restDocumentationContextProvider))
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .alwaysDo(print())
            .build();
    }

    @Test
    @DisplayName("????????? ????????? ??????????????? ????????? ??????")
    public void get_depth1_directory() throws Exception {
        // given
        DirectoryResponse directoryResponse = createDirectoryResponse();
        given(directoryQueryService.getDirectory(anyString())).willReturn(directoryResponse);

        // when
        ResultActions resultActions = mockMvc.perform(get(DIRECTORY_BASE_URL + "/{departmentId}", "departmentId"));

        // then
        resultActions
            .andDo(getDirectoryApiDocument())
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.departmentResponses.[0].name").value("???????????????"))
            .andExpect(jsonPath("$.departmentResponses.[1].name").value("???????????????"))
        ;
    }

    private RestDocumentationResultHandler getDirectoryApiDocument() {
        return document("department",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("departmentResponses.[0].id").description("?????? ?????? ID"),
                fieldWithPath("departmentResponses.[0].name").description("?????????"),
                fieldWithPath("departmentResponses.[0].number").description("?????? ??????"),
                fieldWithPath("departmentResponses.[0].phoneType").description("?????? ?????? ??????(??????, ??????, ??????)"),
                fieldWithPath("departmentResponses.[0].branchId").description("????????? ID"),
                fieldWithPath("departmentResponses.[0].synonyms.[0].id").description("?????? ????????? ?????? ID"),
                fieldWithPath("departmentResponses.[0].synonyms.[0].synonym").description("?????? ?????????"))
        );
    }

    private DirectoryResponse createDirectoryResponse() {
        DepartmentSynonymResponse ???????????? = DepartmentSynonymResponse.builder()
            .id(UUID.randomUUID().toString())
            .synonym("????????????")
            .build();
        DepartmentResponse ??????????????? = DepartmentResponse.builder()
            .id(UUID.randomUUID().toString())
            .name("???????????????")
            .number("1234")
            .phoneType(PhoneType.INWARD_DIALING)
            .branchId("test")
            .synonyms(List.of(????????????))
            .build();

        DepartmentSynonymResponse ???????????? = DepartmentSynonymResponse.builder()
            .id(UUID.randomUUID().toString())
            .synonym("????????????")
            .build();
        DepartmentResponse ??????????????? = DepartmentResponse.builder()
            .id(UUID.randomUUID().toString())
            .name("???????????????")
            .number("4321")
            .phoneType(PhoneType.GROUP_DIALING)
            .branchId("test")
            .synonyms(List.of(????????????))
            .build();

        return DirectoryResponse.builder()
            .departmentResponses(List.of(???????????????, ???????????????))
            .build();
    }
}