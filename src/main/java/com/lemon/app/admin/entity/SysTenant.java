package com.lemon.app.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.lemon.app.handler.JsonStringArrayTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 租户
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_tenant")
public class SysTenant implements Serializable {

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
     * 租户名称
     */
    @TableField("name_")
    private String name;

    /**
     * 描述
     */
    @TableField("description_")
    private String description;

    /**
     * 可用模块，空列表则全部可用
     */
    @TableField(value = "modules_", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] modules;

    /**
     * 是否可用
     */
    @TableField("active_")
    private Boolean active;

}
