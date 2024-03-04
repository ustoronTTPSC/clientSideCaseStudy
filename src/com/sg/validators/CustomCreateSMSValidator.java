package com.sg.validators;

import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.core.ui.validation.*;
import wt.part.WTPart;
import wt.util.WTException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomCreateSMSValidator extends DefaultUIComponentValidator {

    @Override
    public UIValidationResult validateFormSubmission(UIValidationKey uiValidationKey, UIValidationCriteria uiValidationCriteria, Locale locale) throws WTException {
        UIValidationResult uiValidationResult = super.validateFormSubmission(uiValidationKey, uiValidationCriteria, locale);

        UIValidationFeedbackMsg feedbackMsg = null;

        if (uiValidationCriteria.getContextObject().getObject() instanceof WTPart) {
            Object clickedButton = uiValidationCriteria.getRequestDataParameterMap().get("clickedButton");
            if (clickedButton != null) {
                if (((String[]) clickedButton)[0].equals("ext-gen35")) {
                    if (!validMessageText(getValMessageText(uiValidationCriteria)) || !validTelephoneNumber(getValTelephoneNumber(uiValidationCriteria))) {
                        feedbackMsg = UIValidationFeedbackMsg.newInstance("Message text or telephone number invalid", FeedbackType.ERROR);
                        uiValidationResult.setStatus(UIValidationStatus.DENIED);
                        uiValidationResult.addFeedbackMsg(feedbackMsg);
                    }
                }
            }
        } else {
            if (!isEndItem(uiValidationCriteria)) {
                feedbackMsg = UIValidationFeedbackMsg.newInstance("Make SMS Gateway end item", FeedbackType.ERROR);
                uiValidationResult.setStatus(UIValidationStatus.DENIED);
                uiValidationResult.addFeedbackMsg(feedbackMsg);
            }
        }

        return uiValidationResult;
    }

    private boolean validTelephoneNumber(String telephoneNumber) {
        return Pattern.compile("^[1-9][1-9][1-9][1-9][1-9][1-9][1-9][1-9][1-9]$").matcher(telephoneNumber).matches();
    }

    private boolean validMessageText(String messageText) {
        if (!messageText.isEmpty() && messageText.length() < 120) {
            return true;
        }
        return false;
    }

    private String getValMessageText(UIValidationCriteria uiValidationCriteria) {
        Map<String, String> textArea = uiValidationCriteria.getTextArea();
        for (Map.Entry<String, String> entry : textArea.entrySet()) {
            if (entry.getKey().contains("MessageText")) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String getValTelephoneNumber(UIValidationCriteria uiValidationCriteria) {
        Map<String, String> textArea = uiValidationCriteria.getTextArea();
        for (Map.Entry<String, String> entry : textArea.entrySet()) {
            if (entry.getKey().contains("TelephoneNumber")) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static boolean isEndItem(UIValidationCriteria uiValidationCriteria) {
        Map<String, List<String>> comboBoxes = uiValidationCriteria.getComboBox();

        for (Map.Entry<String, List<String>> entry : comboBoxes.entrySet()) {
            if (entry.getKey().contains("endItem") && Boolean.parseBoolean(entry.getValue().get(0))) {
                if (Boolean.parseBoolean(entry.getValue().get(0))) {
                    return true;
                }
            }
        }
        return false;
    }
}
