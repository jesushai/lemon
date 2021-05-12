package ${packageName};

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.lemon.framework.db.enums.DbFieldIntegerEnum;

/**
 * <p>
 * ${enum.comment!}
 * <p>
<#list enum.elements as element>
 * ${element.value}-${element.comment}<p>
</#list>
 *
 * @author ${author}
 * @since ${date}
 */
public enum ${enum.enumName} implements DbFieldIntegerEnum {

<#list enum.elements as element>
 <#if element_index == enum.elements?size - 1>
    ${element.name}(${element.value}, "${element.comment}");
 <#else>
    ${element.name}(${element.value}, "${element.comment}"),
 </#if>
</#list>

    @EnumValue
    @JsonValue
    private final int value;

    private final String display;

    ${enum.enumName}(int value, String display) {
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

}
