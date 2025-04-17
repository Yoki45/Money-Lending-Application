package com.lms.generic.localization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalizationServiceImpl implements  ILocalizationService{


    private final MessageSource messageSource;

    @Override
    public String getMessage(String key, Object[] args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error(e.getMessage());
            // If an error occurs, return the default message in English
            return messageSource.getMessage(key, args, Locale.ENGLISH);
        }
    }

    @Override
    public String getLocalizedEnum(Enum<?> enumValue) {
        try {
            String key = "enum." + enumValue.getClass().getSimpleName() + "." + enumValue.name();
            return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error(e.getMessage());
            // If an error occurs, return the default enum name
            return enumValue.name();
        }
    }
}
