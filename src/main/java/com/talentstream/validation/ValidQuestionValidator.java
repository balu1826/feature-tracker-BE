package com.talentstream.validation;

import com.talentstream.dto.FeedbackQuestionData;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidQuestionValidator
        implements ConstraintValidator<ValidQuestion, FeedbackQuestionData> {

    @Override
    public boolean isValid(FeedbackQuestionData q, ConstraintValidatorContext context) {

        if (q == null) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        String type = q.getQuestionType();
        List<String> options = q.getOptions();

        if (type == null) {
            return true;
        }

        switch (type) {

            case "RADIO":
            case "CHECKBOX":
                valid &= requireOptions(options, 2,
                        "For type " + type + ", options must contain at least 2 entries",
                        context);
                break;

            case "REVIEW":
                valid &= requireOptions(options, 1,
                        "For type REVIEW, options must contain at least 1 entry",
                        context);
                break;

            default:
                if (options != null && !options.isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                            "Options are only allowed for RADIO, CHECKBOX or REVIEW types")
                           .addPropertyNode("options")
                           .addConstraintViolation();
                    valid = false;
                }
        }

        return valid;
    }

    private boolean requireOptions(
            List<String> options,
            int minSize,
            String message,
            ConstraintValidatorContext context) {

        if (options == null || options.size() < minSize) {
            context.buildConstraintViolationWithTemplate(message)
                   .addPropertyNode("options")
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
}
