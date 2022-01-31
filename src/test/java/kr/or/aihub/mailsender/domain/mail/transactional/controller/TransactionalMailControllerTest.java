package kr.or.aihub.mailsender.domain.mail.transactional.controller;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.application.MandrillService;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import kr.or.aihub.mailsender.global.config.security.WithMockCustomActivateUser;
import kr.or.aihub.mailsender.global.config.security.WithMockCustomDeactivateUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TransactionalMailController 클래스")
class TransactionalMailControllerTest {
    private static final String PREFIX_URL = "/mail/transactional";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MandrillService mandrillService;

    @Nested
    @DisplayName("GET /mail/transactional/templates/send 요청은")
    class Describe_mailTransactionalTemplatesSend {
        private final MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(PREFIX_URL + "/templates/send");

        @Nested
        @DisplayName("인증된 유저일 경우")
        @WithMockCustomActivateUser
        class Context_activateUser {

            @BeforeEach
            void setUp() throws MandrillApiError, IOException {
                given(mandrillService.getTemplates())
                        .willReturn(Arrays.asList(
                                new TemplatesResponse("publishName")
                        ));
            }

            @Test
            @DisplayName("200을 응답한다")
            void It_response200() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                );

                action
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("인증되지 않은 유저일 경우")
        @WithMockCustomDeactivateUser
        class Context_deactivateUser {

            @Test
            @DisplayName("403을 응답한다")
            void It_response403() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                );

                action
                        .andExpect(status().isForbidden());
            }
        }

    }

    @Nested
    @DisplayName("POST /mail/transactional/templates/send 요청은")
    class Describe_postMailTransactionalTemplatesSend {
        private final MockMultipartHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.multipart(PREFIX_URL + "/templates/send");

        private MockMultipartFile createFileWithOriginalFilename(String originalFilename) {
            StringBuilder csvBuilder = new StringBuilder();

            csvBuilder.append("data,name,belong,division,email");
            csvBuilder.append("\n");
            csvBuilder.append(",박주영,,,jypark1@wise.co.kr");

            InputStream inputStream = new ByteArrayInputStream(csvBuilder.toString().getBytes(StandardCharsets.UTF_8));

            try {
                return new MockMultipartFile(
                        "file",
                        originalFilename,
                        null,
                        inputStream
                );
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }
        }

        @Nested
        @DisplayName("활성화된 유저이고")
        @WithMockCustomActivateUser
        class Context_activateUser {

            @Nested
            @DisplayName("존재하는 발행 이름이고")
            class Context_existPublishName {
                private String existPublishName;

                @BeforeEach
                void setUp() throws MandrillApiError, IOException {
                    String existPublishName = "existPublishName";

                    given(mandrillService.getTemplates()).willReturn(
                            Arrays.asList(
                                    new TemplatesResponse(existPublishName)
                            )
                    );

                    given(mandrillService.sendWithTemplate(eq(existPublishName), anyList()))
                            .will(invocation -> {
                                List<MailUser> mailUsers = invocation.getArgument(1);
                                MailUser mailUser = mailUsers.get(0);

                                return Arrays.asList(
                                        TemplateSendResponse.builder()
                                                .email(mailUser.getEmail())
                                                .status("sent")
                                                .build()
                                );
                            });

                    this.existPublishName = existPublishName;
                }

                @Nested
                @DisplayName("지원하는 확장자 파일 이름일 경우")
                class Context_supportedExtensionFileNames {

                    @ParameterizedTest
                    @ValueSource(strings = {
                            "a.csv",
                            " .csv"
                    })
                    @DisplayName("303을 응답하고 조회 페이지로 리다이렉트 된다")
                    void It_response303AndRedirectGetPage(String supportedExtensionFileName) throws Exception {
                        MockMultipartFile file = createFileWithOriginalFilename(supportedExtensionFileName);

                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .file(file)
                                        .param("publishName", existPublishName)
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        );

                        action
                                .andExpect(status().isSeeOther())
                                .andExpect(redirectedUrl(PREFIX_URL + "/templates/send"));
                    }
                }

                @Nested
                @DisplayName("비거나 null인 파일 이름일 경우")
                class Context_emptyOrNullFileName {

                    @ParameterizedTest
                    @NullAndEmptySource
                    @DisplayName("400을 응답한다")
                    void It_response400(String emptyOrNullFileName) throws Exception {
                        MockMultipartFile file = createFileWithOriginalFilename(emptyOrNullFileName);

                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .file(file)
                                        .param("publishName", existPublishName)
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }

                @Nested
                @DisplayName("지원하지 않는 확장자 파일 이름일 경우")
                class Context_notSupportedExtensionFileNames {

                    @ParameterizedTest
                    @ValueSource(strings = {
                            "a.xlsx",
                            "b.xls",
                            "c.txt"
                    })
                    @DisplayName("400을 응답한다")
                    void It_response400(String notSupportedExtensionFileName) throws Exception {
                        MockMultipartFile file = createFileWithOriginalFilename(notSupportedExtensionFileName);

                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .file(file)
                                        .param("publishName", existPublishName)
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }

                @Nested
                @DisplayName("확장자가 없는 파일 이름일 경우")
                class Context_noExtensionFilename {

                    @ParameterizedTest
                    @ValueSource(strings = {
                            "e"
                    })
                    @DisplayName("400을 응답한다")
                    void It_response400(String noExtensionFilename) throws Exception {
                        MockMultipartFile file = createFileWithOriginalFilename(noExtensionFilename);

                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .file(file)
                                        .param("publishName", existPublishName)
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }


                @Nested
                @DisplayName("존재하지 않는 발행 이름일 경우")
                class Context_notExistPublishName {
                    private String notExistPublishName;

                    @BeforeEach
                    void setUp() throws MandrillApiError, IOException {
                        given(mandrillService.getTemplates()).willReturn(
                                Collections.emptyList()
                        );

                        this.notExistPublishName = "notExistPublishName";
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {
                            "a.csv",
                            "b.xlsx",
                            "c.xls",
                            "d.txt",
                            "e",
                            ".csv",
                    })
                    @NullAndEmptySource
                    @DisplayName("400을 응답한다")
                    void It_response400(String supportedExtensionFileName) throws Exception {
                        MockMultipartFile file = createFileWithOriginalFilename(supportedExtensionFileName);

                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .file(file)
                                        .param("publishName", notExistPublishName)
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }

                }

            }

            @Nested
            @DisplayName("비활성화된 유저일경우")
            @WithMockCustomDeactivateUser
            class Context_deactivateUser {
                private String existPublishName;

                @BeforeEach
                void setUp() throws MandrillApiError, IOException {
                    String existPublishName = "existPublishName";

                    given(mandrillService.getTemplates()).willReturn(
                            Arrays.asList(
                                    new TemplatesResponse(existPublishName)
                            )
                    );

                    this.existPublishName = existPublishName;
                }

                @ParameterizedTest
                @ValueSource(strings = {
                        "a.csv",
                        "b.xlsx",
                        "c.xls",
                        "d.txt",
                        "e",
                        ".csv",
                })
                @NullAndEmptySource
                @DisplayName("403을 응답한다")
                void It_response403(String filename) throws Exception {
                    MockMultipartFile file = createFileWithOriginalFilename(filename);

                    ResultActions action = mockMvc.perform(
                            requestBuilder
                                    .file(file)
                                    .param("publishName", existPublishName)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    );

                    action
                            .andExpect(status().isForbidden());
                }
            }

        }
    }
}
