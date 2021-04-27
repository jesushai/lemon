package com.lemon.app.admin.dto;

import com.lemon.schemaql.validator.UpdateValidatedGroup;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/4
 */
@Data
@Accessors(chain = true)
public class MenuDto {

    @NotNull(message = "NNAM-1003", groups = UpdateValidatedGroup.class)
    private Long id;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 菜单代码
     */
    private String code;

    /**
     * 菜单显示，支持I18N
     */
    private String display;

    /**
     * 顺序
     */
    private Integer rank;

    /**
     * 图标路径
     */
    private String iconUrl;

    /**
     * 级次
     */
    private Integer level;

    private Long partOf;

    /**
     * 是否可用
     */
    private Boolean active;

    /**
     * 后分隔行
     */
    private Boolean separation;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否按钮
     */
    private Boolean action;

    /**
     * 按钮api地址，action_=true有效
     */
    private String actionUri;

    private List<MenuDto> children;
}
