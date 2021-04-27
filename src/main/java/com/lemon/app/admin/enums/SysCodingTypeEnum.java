package com.lemon.app.admin.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.lemon.framework.db.enums.DbFieldIntegerEnum;

/**
 * <b>名称：系统代码类型枚举</b><br/>
 * <b>描述：</b><br/>
 * 类型: 0-系统内置; 101-业务定义
 * TODO: 返回整个对象前台并不好处理
 *
 * @author hai-zhang
 * @since 2020-6-29
 */
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SysCodingTypeEnum implements DbFieldIntegerEnum {

    SYSTEM(0, "系统内置"),
    BUSINESS(101, "业务定义");

    @EnumValue //写入到数据库里的枚举值
    @JsonValue //枚举类的json返回值
    private final int value;

    private final String display;

    SysCodingTypeEnum(int value, String display) {
        this.value = value;
        this.display = display;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDisplay() {
        return display;
    }

//    /**
//     * Json反序列化为枚举的时候需要依据Json中code的值来决定反序列化为哪个枚举
//     */
//    @JsonCreator
//    public static SysCodingTypeEnum forValues(@JsonProperty("value") int value) {
//        return DbFieldEnum.fromValue(SysCodingTypeEnum.class, value);
//    }
}
