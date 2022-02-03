package kr.or.aihub.mailsender.domain.mail.transactional.application;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendRequest;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotExistPublishNameException;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotSupportedFileExtensionException;
import kr.or.aihub.mailsender.global.utils.TestCsvUserListFileFactory;
import kr.or.aihub.mailsender.global.utils.application.CsvMailUserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("TransactionalMailSender 클래스")
class TransactionalMailSenderTest {
    private final MandrillService mandrillService = mock(MandrillService.class);

    private TransactionalMailSender transactionalMailSender;

    @BeforeEach
    void setUp() {
        CsvMailUserConverter csvMailUserConverter = new CsvMailUserConverter();

        this.transactionalMailSender = new TransactionalMailSender(mandrillService, csvMailUserConverter);
    }

    @Nested
    @DisplayName("sendTemplates 메서드는")
    class Describe_sendTemplates {

        @Nested
        @DisplayName("존재하지 않는 발행 이름일 경우")
        class Context_notExistPublishName {
            private String notExistPublishName;

            @BeforeEach
            void setUp() throws MandrillApiError, IOException {
                String notExistPublishName = "notExistPublishName";

                given(mandrillService.getTemplates())
                        .willReturn(Collections.emptyList());

                this.notExistPublishName = notExistPublishName;
            }

            @Test
            @DisplayName("NotExistPublishNameException 예외를 던진다")
            void It_throwsNotExistPublishNameException() {
                MockMultipartFile file = TestCsvUserListFileFactory.create();

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
            @DisplayName("file 이름이 지원하는 확장자가 아닌 경우")
            class Context_notSupportedExtensionFilenames {

                @ParameterizedTest
                @ValueSource(strings = {
                        "a.xlsx",
                        "b.xls",
                        "c.txt"
                })
                @DisplayName("NotSupportedFileExtensionException을 던진다")
                void It_throwsNotSupportedFileExtensionException(String notSupportedExtensionFilename) {
                    MultipartFile notSupportedExtensionFile = TestCsvUserListFileFactory.create(notSupportedExtensionFilename);

                    assertThatThrownBy(() -> {
                        transactionalMailSender.sendTemplate(new TemplateSendRequest(notSupportedExtensionFile, existPublishName));
                    }).isInstanceOf(NotSupportedFileExtensionException.class);
                }
            }

            @Nested
            @DisplayName("file 이름 확장자가 없는 경우")
            class Context_noExtensionFilename {

                @ParameterizedTest
                @ValueSource(strings = {
                        "a"
                })
                @DisplayName("IllegalArgumentException을 던진다")
                void It_throwsIllegalArgumentException(String noExtensionFilename) {
                    MockMultipartFile noExtensionFile = TestCsvUserListFileFactory.create(noExtensionFilename);

                    assertThatThrownBy(() ->
                            transactionalMailSender.sendTemplate(new TemplateSendRequest(noExtensionFile, existPublishName))
                    ).isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Nested
            @DisplayName("file 이름 확장자가 비거나 널 값인 경우")
            class Context_emptyOrNullFilename {

                @ParameterizedTest
                @NullAndEmptySource
                @DisplayName("IllegalArgumentException을 던진다")
                void It_throwsIllegalArgumentException(String emptyOrNullFilename) {
                    MockMultipartFile emptyFile = TestCsvUserListFileFactory.create(emptyOrNullFilename);

                    assertThatThrownBy(() ->
                            transactionalMailSender.sendTemplate(new TemplateSendRequest(emptyFile, existPublishName))
                    ).isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Nested
            @DisplayName("file 이름이 지원하는 확장자일 경우")
            class Context_supportedExtensionFilename {

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
                }

                @ParameterizedTest
                @ValueSource(strings = {
                        "a.csv"
                })
                @DisplayName("발송 결과를 리턴한다")
                void It_doesNotThrowAnyException(String supportedExtensionFilename) throws MandrillApiError, IOException {
                    MockMultipartFile supportedExtensionFile = TestCsvUserListFileFactory.create(supportedExtensionFilename);

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
