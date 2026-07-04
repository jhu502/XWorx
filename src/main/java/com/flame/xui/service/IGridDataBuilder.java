package com.flame.xui.service;

import com.flame.xui.builder.AbstractComponentBuilder;

import java.io.Serializable;

/**
 * 网格数据构建器标记类 —— 作为可序列化的数据传输对象基类。
 *
 * <p>目前是一个空实现，主要作为类型标记和序列化支持的占位符。
 * 在框架中，网格组件（DataGrid、TreeGrid、PropertyGrid等）的配置与数据构建器
 * 通过继承层次结构（而非此类）来组织。</p>
 *
 * <p>实现了 {@link Serializable} 接口，确保子类实例可以在分布式环境或HTTP会话中安全传输。</p>
 *
 * @author Flame
 * @see AbstractComponentBuilder
 */
public class IGridDataBuilder implements Serializable {
    private static final long serialVersionUID = 1L;
}
