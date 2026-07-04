package com.flame.config.basic;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * ContextRefreshedEvent、IOCTest_Ext$1[source=我发布的时间]、ContextClosedEvent
 * 1). 容器创建对象: refresh()
 * 2). finishRefresh(); 容器刷新完成会发布ContextRefreshedEvent事件
 * 3). 自己发布事件
 * 4). 如期关闭发布事件ContextClosedEvent;
 */
@Component
public class XFlameMethodListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("----:" + event.getApplicationContext().getApplicationName() + " : " + event);
    }
}
