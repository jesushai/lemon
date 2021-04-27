package com.lemon.app.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.sql.Timestamp;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统菜单
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 乐观锁
     */
    @TableField("rev_")
    @Version
    private Long rev;

    /**
     * 所属系统
     */
    @TableField("system_")
    private String system;

    /**
     * 客户端
     */
    @TableField("module_")
    private String module;

    /**
     * 菜单代码
     */
    @TableField("code_")
    private String code;

    /**
     * 菜单显示，支持I18N
     */
    @TableField("display_")
    private String display;

    /**
     * 顺序
     */
    @TableField("rank_")
    private Integer rank;

    /**
     * 图标路径
     */
    @TableField("icon_url_")
    private String iconUrl;

    /**
     * 级次
     */
    @TableField("level_")
    private Integer level;

    @TableField("part_of_")
    private Long partOf;

    /**
     * 是否可用
     */
    @TableField("active_")
    private Boolean active;

    /**
     * 删除标记
     */
    @TableField("deleted_")
    @TableLogic
    private Boolean deleted;

    /**
     * 后分隔行
     */
    @TableField("separation_")
    private Boolean separation;

    /**
     * 描述
     */
    @TableField("description_")
    private String description;

    /**
     * 是否按钮
     */
    @TableField("action_")
    private Boolean action;

    /**
     * 按钮api地址，action_=true有效
     */
    @TableField("action_uri_")
    private String actionUri;

    /**
     * 创建人
     */
    @TableField(value = "create_by_", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建人姓名
     */
    @TableField(value = "create_name_by_", fill = FieldFill.INSERT)
    private String createNameBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time_", fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 最后修改人
     */
    @TableField(value = "modified_by_", fill = FieldFill.INSERT_UPDATE)
    private Long modifiedBy;

    /**
     * 最后修改人名
     */
    @TableField(value = "modified_name_by_", fill = FieldFill.INSERT_UPDATE)
    private String modifiedNameBy;

    /**
     * 最后修改时间
     */
    @TableField(value = "modified_time_", fill = FieldFill.INSERT_UPDATE)
    private Timestamp modifiedTime;


}
