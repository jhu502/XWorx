package com.xworx.config;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * SpringBoot的自动配置进行排除配置时，只能够一个一个的去添加排除类，但是有些功能模块带有大量的自动配置类(例如：**Flowable**)，
 * 相关的自动配置类差不多有20几个，如果逐个添加特别麻烦，为了使EnableAutoConfiguration能够支持package排除，主要在@SpringBootApplication定义excludeName
 * e.g.: @SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }, excludeName = { "org.flowable.*" })
 * @author hujin
 */
public class XWorxAutoImportSelector extends AutoConfigurationImportSelector {
	@Override
	protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		List<String> configurations = getCandidateConfigurations(metadata, attributes);
		configurations = removeDuplicates(configurations);

		Set<String> excluded = new LinkedHashSet<>();
		excluded.addAll(asList(attributes, "exclude"));
		for (String exclusion : Arrays.asList(attributes.getStringArray("excludeName"))) {
			if (exclusion.endsWith("*")) {
				exclusion = exclusion.substring(0, exclusion.length() - 2);
				for (String config : configurations.toArray(new String[0])) {
					if (config != null && config.startsWith(exclusion)) {
						excluded.add(config);
					}
				}
			} else {
				excluded.add(exclusion);
			}
		}
		excluded.addAll(getExcludeAutoConfigurationsProperty());
		return excluded;
	}
}
