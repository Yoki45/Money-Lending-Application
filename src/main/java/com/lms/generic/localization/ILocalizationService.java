package com.lms.generic.localization;

public interface ILocalizationService {

    String getMessage(String key, Object[] args);

    String getLocalizedEnum(Enum<?> enumValue);
}
