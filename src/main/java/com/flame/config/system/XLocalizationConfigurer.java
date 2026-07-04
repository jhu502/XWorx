package com.flame.config.system;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.flame.util.FlameUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ComponentScan({ "com.flame.localize" })
@Configuration
public class XLocalizationConfigurer {
    private static List<Locale> LOCALES = Arrays.asList(Locale.PRC, Locale.US, Locale.UK, Locale.JAPAN);

    @Bean
    public LocaleResolver localeResolver() {
        return new XLocalizationConfigurer.SmartLocaleResolver();
    }

    /**
     * 获取请求头国际化信息
     */
    public class SmartLocaleResolver extends AcceptHeaderLocaleResolver {

        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            String language = request.getHeader("Accept-Language");
            if (FlameUtils.isBlank(language)) {
                return Locale.getDefault();
            } else {
                List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader("Accept-Language"));
                return Locale.lookup(list, LOCALES);
            }
        }

        @Override
        public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
            System.out.println(Thread.currentThread() + "----------------:" + locale);
        }
    }
}
