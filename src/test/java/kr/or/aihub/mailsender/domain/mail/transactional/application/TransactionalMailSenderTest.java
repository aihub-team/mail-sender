package kr.or.aihub.mailsender.domain.mail.transactional.application;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendRequest;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotExistPublishNameException;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotSupportedFileExtensionException;
import kr.or.aihub.mailsender.global.utils.application.MailUserCsvConvertor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("TransactionalMailSender 클래스")
class TransactionalMailSenderTest {
    private final MandrillService mandrillService = mock(MandrillService.class);
    private final MailUserCsvConvertor mailUserCsvConvertor = new MailUserCsvConvertor();

    private TransactionalMailSender transactionalMailSender;

    @BeforeEach
    void setUp() {
        this.transactionalMailSender = new TransactionalMailSender(mandrillService, mailUserCsvConvertor);
    }

    @Nested
    @DisplayName("sendTemplates 메서드는")
    class Describe_sendTemplates {

        private MockMultipartFile createFileWithOriginalFilename(String originalFilename) {
            StringBuilder csvBuilder = new StringBuilder();

            csvBuilder.append("data,name,belong,division,email");
            csvBuilder.append("\n");
            csvBuilder.append(",박주영,,,jypark1@wise.co.kr");

            InputStream inputStream = new ByteArrayInputStream(csvBuilder.toString().getBytes());

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
        @DisplayName("존재하지 않는 발행 이름일 경우")
        class Context_notExistPublishName {
            private String notExistPublishName;

            @BeforeEach
            void setUp() {
                this.notExistPublishName = "notExist";
            }

            @Test
            @DisplayName("예외를 던진다")
            void It_throwsNotExistPublishNameException() {
                MockMultipartFile file = createFileWithOriginalFilename("test.csv");

                assertThatThrownBy(() -> {
                    transactionalMailSender.sendTemplate(new TemplateSendRequest(file, notExistPublishName));
                }).isInstanceOf(NotExistPublishNameException.class);
            }
        }

        @Nested
        @DisplayName("존재하는 발행 이름일 경우")
        class Context_existPublishName {
            private String existPublishName;

            @BeforeEach
            void setUp() throws MandrillApiError, IOException {
                String existPublishName = "existPublishName";

                given(mandrillService.getTemplates())
                        .willReturn(Arrays.asList(new TemplatesResponse(existPublishName)));

                this.existPublishName = existPublishName;
            }

            @Nested
            @DisplayName("file이 null인 경우")
            class Context_nullFile {
                private MultipartFile nullFile;

                @BeforeEach
                void setUp() {
                    this.nullFile = null;
                }

                @Test
                @DisplayName("IllegalArgumentException을 던진다")
                void It_throwsIllegalArgumentException() {
                    assertThatThrownBy(() -> {
                        transactionalMailSender.sendTemplate(new TemplateSendRequest(nullFile, existPublishName));
                    }).isInstanceOf(IllegalArgumentException.class);
                }

            }

            @Nested
            @DisplayName("file이 지원하는 확장자가 아닌 경우")
            class Context_notSupportedFileExtensions {
                private List<MockMultipartFile> notSupportedFileExtensions;

                @BeforeEach
                void setUp() {
                    List<String> originalFilenames = Arrays.asList(
                            "a.xlsx",
                            "b.xls",
                            "c.txt"
                    );

                    this.notSupportedFileExtensions = originalFilenames.stream()
                            .map(originalFilename -> createFileWithOriginalFilename(originalFilename))
                            .collect(Collectors.toList());
                }

                @Test
                @DisplayName("NotSupportedFileExtensionException을 던진다")
                void It_throwsNotSupportedFileExtensionException() {
                    for (MockMultipartFile notSupportedFileExtension : this.notSupportedFileExtensions) {
                        assertThatThrownBy(() -> {
                            transactionalMailSender.sendTemplate(new TemplateSendRequest(notSupportedFileExtension, existPublishName));
                        }).isInstanceOf(NotSupportedFileExtensionException.class);
                    }
                }
            }

            @Nested
            @DisplayName("file 확장자가 없는 경우")
            class Context_noExtensionFile {
                private MockMultipartFile noExtensionFile;

                @BeforeEach
                void setUp() {
                    this.noExtensionFile = createFileWithOriginalFilename("a");
                }

                @Test
                @DisplayName("IllegalArgumentException을 던진다")
                void It_throwsIllegalArgumentException() {
                    assertThatThrownBy(() ->
                            transactionalMailSender.sendTemplate(new TemplateSendRequest(noExtensionFile, existPublishName))
                    ).isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Nested
            @DisplayName("file 확장자가 빈 값인 경우")
            class Context_emptyFile {
                private MockMultipartFile emptyFile;

                @BeforeEach
                void setUp() {
                    this.emptyFile = createFileWithOriginalFilename("");
                }

                @Test
                @DisplayName("IllegalArgumentException을 던진다")
                void It_throwsIllegalArgumentException() {
                    assertThatThrownBy(() ->
                            transactionalMailSender.sendTemplate(new TemplateSendRequest(emptyFile, existPublishName))
                    ).isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Nested
            @DisplayName("file이 지원하는 확장자일 경우")
            class Context_supportedExtensionFile {
                private List<MockMultipartFile> supportedExtensionFiles;

                @BeforeEach
                void setUp() throws MandrillApiError, IOException {
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

                    this.supportedExtensionFiles = Arrays.asList(
                            createFileWithOriginalFilename("a.csv")
                    );
                }

                @Test
                @DisplayName("발송 결과를 리턴한다")
                void It_doesNotThrowAnyException() throws MandrillApiError, IOException {
                    for (MockMultipartFile supportedExtensionFile : this.supportedExtensionFiles) {
                        List<TemplateSendResponse> templateSendResponses
                                = transactionalMailSender.sendTemplate(new TemplateSendRequest(supportedExtensionFile, existPublishName));

                        assertThat(templateSendResponses).isNotNull();

                        TemplateSendResponse templateSendResponse = templateSendResponses.get(0);
                        assertThat(templateSendResponse.getEmail()).isEqualTo("jypark1@wise.co.kr");
                        assertThat(templateSendResponse.getStatus()).isEqualTo("sent");
                        assertThat(templateSendResponse.getRejectReason()).isNull();
                        assertThat(templateSendResponse.getId()).isNull();
                    }
                }
            }
        }

    }
}