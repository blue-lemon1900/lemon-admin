package org.lemon.commons.web.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * 获取请求头国际化信息
 *
 * @author Lion Li
 */
public class I18nLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(@NonNull HttpServletRequest httpServletRequest) {
        String language = httpServletRequest.getHeader("content-language");
        if (language != null && !language.isEmpty()) {
            String[] split = language.split("_");
            return Locale.of(split[0], split[1]);
        }
        return Locale.getDefault();
    }

    @Override
    public void setLocale(@NonNull HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
    }
}
