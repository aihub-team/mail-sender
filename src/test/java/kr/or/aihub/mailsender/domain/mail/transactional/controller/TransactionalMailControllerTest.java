package kr.or.aihub.mailsender.domain.mail.transactional.controller;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.application.MandrillService;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import kr.or.aihub.mailsender.global.config.security.WithMockCustomActivateUser;
import kr.or.aihub.mailsender.global.config.security.WithMockCustomDeactivateUser;
import kr.or.aihub.mailsender.global.utils.TestCsvUserListFileFactory;
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

import java.io.IOException;
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
@DisplayName("TransactionalMailController ?????????")
class TransactionalMailControllerTest {
    private static final String PREFIX_URL = "/mail/transactional";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MandrillService mandrillService;

    @Nested
    @DisplayName("GET /mail/transactional/templates/send ?????????")
    class Describe_mailTransactionalTemplatesSend {
        private final MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(PREFIX_URL + "/templates/send");

        @Nested
        @DisplayName("????????? ????????? ??????")
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
            @DisplayName("200??? ????????????")
            void It_response200() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                );

                action
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("???????????? ?????? ????????? ??????")
        @WithMockCustomDeactivateUser
        class Context_deactivateUser {

            @Test
            @DisplayName("403??? ????????????")
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
    @DisplayName("POST /mail/transactional/templates/send ?????????")
    class Describe_postMailTransactionalTemplatesSend {
        private final MockMultipartHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.multipart(PREFIX_URL + "/templates/send");

        @Nested
        @DisplayName("???????????? ????????????")
        @WithMockCustomActivateUser
        class Context_activateUser {

            @Nested
            @DisplayName("null????????? ??? ?????? ????????? ??????")
            class Context_nullOrEmptyPublishName {

                @ParameterizedTest
                @NullAndEmptySource
                @DisplayName("400??? ????????????")
                void It_response400(String nullOrEmptyPublishName) throws Exception {
                    MockMultipartFile file = TestCsvUserListFileFactory.create();

                    ResultActions action = mockMvc.perform(
                            requestBuilder
                                    .file(file)
                                    .param("publishName", nullOrEmptyPublishName)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    );

                    action
                            .andExpect(status().isBadRequest());
                }
            }

            @Nested
            @DisplayName("???????????? ?????? ?????? ????????? ??????")
            class Context_notExistPublishName {
                private String notExistPublishName;

                @BeforeEach
                void setUp() throws MandrillApiError, IOException {
                    given(mandrillService.getTemplates()).willReturn(
                            Collections.emptyList()
                    );

                    this.notExistPublishName = "notExistPublishName";
                }

                @Test
                @DisplayName("400??? ????????????")
                void It_response400() throws Exception {
                    MockMultipartFile file = TestCsvUserListFileFactory.create();

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

            @Nested
            @DisplayName("???????????? ?????? ????????????")
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
                @DisplayName("???????????? ????????? ?????? ????????? ??????")
                class Context_supportedExtensionFileNames {

                    @ParameterizedTest
                    @ValueSource(strings = {
                            "a.csv",
                            " .csv"
                    })
                    @DisplayName("303??? ???????????? ?????? ???????????? ??????????????? ??????")
                    void It_response303AndRedirectGetPage(String supportedExtensionFileName) throws Exception {
                        MockMultipartFile file = TestCsvUserListFileFactory.create(supportedExtensionFileName);

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
                @DisplayName("????????? null??? ?????? ????????? ??????")
                class Context_emptyOrNullFileName {

                    @ParameterizedTest
                    @NullAndEmptySource
                    @DisplayName("400??? ????????????")
                    void It_response400(String emptyOrNullFileName) throws Exception {
                        MockMultipartFile file = TestCsvUserListFileFactory.create(emptyOrNullFileName);

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
                @DisplayName("???????????? ?????? ????????? ?????? ????????? ??????")
                class Context_notSupportedExtensionFileNames {

                    @ParameterizedTest
                    @ValueSource(strings = {
                            "a.xlsx",
                            "b.xls",
                            "c.txt"
                    })
                    @DisplayName("400??? ????????????")
                    void It_response400(String notSupportedExtensionFileName) throws Exception {
                        MockMultipartFile file = TestCsvUserListFileFactory.create(notSupportedExtensionFileName);

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
                @DisplayName("???????????? ?????? ?????? ????????? ??????")
                class Context_noExtensionFilename {

                    @ParameterizedTest
                    @ValueSource(strings = {
                            "e"
                    })
                    @DisplayName("400??? ????????????")
                    void It_response400(String noExtensionFilename) throws Exception {
                        MockMultipartFile file = TestCsvUserListFileFactory.create(noExtensionFilename);

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


            }

        }

        @Nested
        @DisplayName("??????????????? ???????????????")
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

            @Test
            @DisplayName("403??? ????????????")
            void It_response403() throws Exception {
                MockMultipartFile file = TestCsvUserListFileFactory.create();

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
