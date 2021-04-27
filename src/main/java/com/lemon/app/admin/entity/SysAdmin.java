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
import com.lemon.app.handler.JsonLongArrayTypeHandler;
import com.lemon.framework.auth.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统人员
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_admin", autoResultMap = true)
public class SysAdmin implements User, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户输入的密码明文
     */
    @TableField(exist = false)
    private String rawPassword;

    /**
     * 所属租户
     */
    @TableField("tenant_")
//    @TableField(exist = false)
    @JsonIgnore
    private Long tenant;


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
     * 姓名
     */
    @TableField("display_")
    private String display;

    /**
     * 登录名
     */
    @TableField("username_")
    private String username;

    /**
     * 密码
     */
    @TableField("password_")
    private String password;

    /**
     * 最后登录IP
     */
    @TableField("last_ip_")
    private String lastIp;

    /**
     * 最后登录时间
     */
    @TableField("last_login_")
    private Timestamp lastLogin;

    /**
     * 头像
     */
    @TableField("avatar_")
    private String avatar;

    /**
     * 角色列表json数组
     */
    @TableField(value = "role_ids_", typeHandler = JsonLongArrayTypeHandler.class)
    private Long[] roleIds;

    /**
     * 电话
     */
    @TableField("phone_")
    private String phone;

    /**
     * 身份证
     */
    @TableField("identity_")
    private String identity;

    @TableField("email_")
    private String email;

    /**
     * 微信id
     */
    @TableField("wechat_")
    private String wechat;

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
    @JsonIgnore
    private Boolean deleted;

    /**
     * 描述
     */
    @TableField("description_")
    private String description;

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

    @Override
    @JsonIgnore
    public boolean isValid() {
        return this.active && !this.deleted;
    }

}
