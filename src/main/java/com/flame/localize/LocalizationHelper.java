package com.flame.localize;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化工具类
 **/
@Slf4j
@Component
public class LocalizationHelper {
    private static final Logger logger = LoggerFactory.getLogger(LocalizationHelper.class);
    private static final String COLON = "COLON";
    private static MessageSource messageSource;

    public LocalizationHelper(MessageSource messageSource) {
        LocalizationHelper.messageSource = messageSource;
    }

    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * 获取单个国际化翻译值，支持大小写不敏感查找。
     * 查找顺序：原始 key → 全小写 → 全大写 → 返回 key 本身。
     */
    public static String get(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            // 原始 key 未找到，尝试全小写
            String lowerKey = key.toLowerCase(java.util.Locale.ENGLISH);
            if (!lowerKey.equals(key)) {
                try {
                    return messageSource.getMessage(lowerKey, null, locale);
                } catch (Exception ignored) {
                }
            }
            // 尝试全大写
            String upperKey = key.toUpperCase(java.util.Locale.ENGLISH);
            if (!upperKey.equals(key) && !upperKey.equals(lowerKey)) {
                try {
                    return messageSource.getMessage(upperKey, null, locale);
                } catch (Exception ignored) {
                }
            }
            return key;
        }
    }

    public static String getLabel(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            String text = messageSource.getMessage(key, null, locale);
            if (text == null)
                return text;
            if (text.endsWith(":") || text.endsWith("："))
                return text;
            String colon = messageSource.getMessage(COLON, null, locale);
            colon = colon == null ? ":" : colon;
            return text + colon;
        } catch (Exception e) {
            return "";
        }
    }

    public static String get(String key, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(key, params, locale);
        } catch (Exception e) {
            return "";
        }
    }

    public static String get(String key, Locale locale) {
        try {
            /**
             * 线上直接new Locale(language),会是小写en_us,调用本地方法读取资源的时候会识别不到,
             * 因为配置文件是大写的后缀en_US,所以这里拆分,然后拼装为大写的Locale的en_US
             */
            if (logger.isTraceEnabled()) {
                logger.trace("get,msgKey:{},locale:{}", key, locale);
            }
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            try {
                return messageSource.getMessage(key, null, Locale.US);
            } catch (Exception e0) {
                return "";
            }
        }
    }
}
