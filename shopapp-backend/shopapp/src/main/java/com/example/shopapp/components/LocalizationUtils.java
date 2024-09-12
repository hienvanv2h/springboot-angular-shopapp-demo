package com.example.shopapp.components;

import com.example.shopapp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocalizationUtils {

    private final MessageSource messageSource;

    public String getLocalizedMessage(String messageKey, Object... params) {
        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale = request.getLocale() != null ? request.getLocale() : Locale.getDefault();
        return messageSource.getMessage(messageKey, params, locale);
    }

}
