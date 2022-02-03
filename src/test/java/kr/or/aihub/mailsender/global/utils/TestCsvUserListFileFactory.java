package kr.or.aihub.mailsender.global.utils;

import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestCsvUserListFileFactory {
    private static final String DEFAULT_ORIGINAL_FILENAME = "default.csv";

    public static MockMultipartFile create() {
        return create(DEFAULT_ORIGINAL_FILENAME);
    }

    public static MockMultipartFile create(String originalFilename) {
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("data,name,belong,division,email");
        csvBuilder.append("\n");
        csvBuilder.append(",박주영,,,jypark1@wise.co.kr");

        return getMockMultipartFile(csvBuilder, originalFilename);
    }

    public static MockMultipartFile createWithInvalidAttributes() {
        return createWithInvalidAttributes(DEFAULT_ORIGINAL_FILENAME);
    }

    public static MockMultipartFile createWithInvalidAttributes(String originalFilename) {
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("data,name,belong,division");
        csvBuilder.append("\n");
        csvBuilder.append(",박주영,,,");

        return getMockMultipartFile(csvBuilder, originalFilename);
    }

    private static MockMultipartFile getMockMultipartFile(StringBuilder csvBuilder, String originalFilename) {
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
}
