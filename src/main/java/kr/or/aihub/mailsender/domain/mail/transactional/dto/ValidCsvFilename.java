package kr.or.aihub.mailsender.domain.mail.transactional.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCsvFilename implements ConstraintValidator<CsvFilename, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        String originalFilename = multipartFile.getOriginalFilename();

        String[] originalFilenameSplitByDot = originalFilename.split("\\.");
        String extension = getExtension(originalFilenameSplitByDot);

        return "csv".equals(extension);
    }

    private String getExtension(String[] originalFilenameSplitByDot) {
        try {
            int lastIndex = originalFilenameSplitByDot.length - 1;

            return originalFilenameSplitByDot[lastIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }

    }

}
