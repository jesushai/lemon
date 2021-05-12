package com.lemon.app.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统代码库
 * </p>
 *
 * @author hai-zhang
 * @since 2021-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_coding")
public class SysCoding implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 代码描述
     */
    @TableField(exist = false)
    private String display;


}
