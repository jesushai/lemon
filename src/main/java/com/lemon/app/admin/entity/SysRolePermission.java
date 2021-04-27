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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色权限表
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 乐观锁
     */
    @TableField("rev_")
    @Version
    @JsonIgnore
    private Long rev;

    /**
     * 所属租户
     */
    @TableField("tenant_")
    @JsonIgnore
    private Long tenant;

    /**
     * 角色ID
     */
    @TableField("role_id_")
    private Long roleId;

    /**
     * 权限
     */
    @TableField("permission_")
    private String permission;

    /**
     * 删除标记
     */
    @TableField("deleted_")
    @TableLogic
    @JsonIgnore
    private Boolean deleted;

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
