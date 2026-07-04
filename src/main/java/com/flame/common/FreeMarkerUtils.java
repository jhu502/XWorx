package com.flame.common;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.Version;

/**
 * FreeMarker直接去调用java类时需要使用这个工具方法
 * 
 * @author hujin
 *
 */
public class FreeMarkerUtils {
	public static void initStatics(final Model model) {
        BeansWrapper wrapper = new BeansWrapper(new Version(2, 3, 27));
        TemplateModel statics = wrapper.getStaticModels();
        model.addAttribute("statics", statics);
    }
	

	public static void initStatics(final ModelAndView model) {
        BeansWrapper wrapper = new BeansWrapper(new Version(2, 3, 27));
        TemplateModel statics = wrapper.getStaticModels();
        model.addObject("statics", statics);
    }
	
}
