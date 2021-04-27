package com.lemon.app.admin.dto;

import lombok.Data;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/12
 */
@Data
public class RolePermissionDTO {

    private Long id;

    private Long roleId;

    private String permission;
}
