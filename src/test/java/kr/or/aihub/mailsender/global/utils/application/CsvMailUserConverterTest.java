package kr.or.aihub.mailsender.global.utils.application;

import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotSupportedFileExtensionException;
import kr.or.aihub.mailsender.global.utils.TestCsvUserListFileFactory;
import kr.or.aihub.mailsender.global.utils.errors.FileParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CsvMailUserConverterTest {
    private CsvMailUserConverter csvMailUserConverter;

    @BeforeEach
    void setUp() {
        this.csvMailUserConverter = new CsvMailUserConverter();
    }

    @Nested
    @DisplayName("convert 메서드는")
    class Describe_convert {

        @Nested
        @DisplayName("유저 리스트가 들어있는 파일이 주어지면")
        class Context_userListFile {
            private MultipartFile userListFile;

            @BeforeEach
            void setUp() {
                this.userListFile = TestCsvUserListFileFactory.create();
            }

            @Test
            @DisplayName("변환된 MailUser 리스트를 리턴한다")
            void It_returnsConvertedToMailUser() throws FileParsingException {
                List<MailUser> mailUserList = csvMailUserConverter.convert(userListFile);

                MailUser mailUser = mailUserList.get(0);
                assertThat(mailUser.getEmail()).isEqualTo("jypark1@wise.co.kr");
                assertThat(mailUser.getName()).isEqualTo("박주영");
            }

        }

        @Nested
        @DisplayName("메일 유저 리스트 파일 항목이 일치하지 않은 경우")
        class Context_inValidMailUserListAttributes {
            private MockMultipartFile inValidMailUserAttributes;

            @BeforeEach
            void setUp() {
                this.inValidMailUserAttributes = TestCsvUserListFileFactory.createWithInvalidAttributes();
            }

            @Test
            @DisplayName("FileParsingException을 던진다")
            void It_throwsFileParsingException() {
                assertThatThrownBy(() -> csvMailUserConverter.convert(inValidMailUserAttributes))
                        .isInstanceOf(FileParsingException.class);
            }
        }

        @Nested
        @DisplayName("null 파일이 주어지는 경우")
        class Context_nullFile {

            @ParameterizedTest
            @NullSource
            @DisplayName("IllegalArgumentExcpetion을 던진다")
            void It_throwsIllegalArgumentException(MultipartFile nullFile) {
                assertThatThrownBy(() -> csvMailUserConverter.convert(nullFile))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        @DisplayName("csv가 아닌 확장자가 주어지는 경우")
        class Context_notCsvExtensionFile {

            @ParameterizedTest
            @ValueSource(strings = {
                    "a.xlsx",
                    "b.xls",
                    "c.txt"
            })
            @DisplayName("예외를 던진다")
            void It_throwsException(String notCsvExtensionFilename) {
                MultipartFile notCsvExtensionUserListFile = TestCsvUserListFileFactory.create(notCsvExtensionFilename);

                assertThatThrownBy(() -> csvMailUserConverter.convert(notCsvExtensionUserListFile))
                        .isInstanceOf(NotSupportedFileExtensionException.class);
            }
        }
    }
}
