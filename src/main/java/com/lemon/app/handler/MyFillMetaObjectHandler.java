package com.lemon.app.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.util.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Timestamp;

import static com.lemon.app.constants.EntityConstants.*;

/**
 * 名称：自动填充<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/23
 */
@Slf4j
@RequiredArgsConstructor
public class MyFillMetaObjectHandler implements MetaObjectHandler {

    private final AuthenticationService authenticationService;

    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp now = TimestampUtils.now();
        User user = authenticationService.getPrincipal();

        this.strictInsertFill(metaObject, ENTITY_CREATE_DATE_FIELD, Timestamp.class, now);
        this.strictInsertFill(metaObject, ENTITY_MODIFIED_DATE_FIELD, Timestamp.class, now);

        this.strictInsertFill(metaObject, ENTITY_CREATE_BY_FIELD, Long.class,
                null == user ? 0L : user.getId());
        this.strictInsertFill(metaObject, ENTITY_MODIFIED_BY_FIELD, Long.class,
                null == user ? 0L : user.getId());

        this.strictInsertFill(metaObject, ENTITY_CREATE_NAME_BY_FIELD, String.class,
                null == user ? StringUtils.EMPTY : user.getUsername());
        this.strictInsertFill(metaObject, ENTITY_MODIFIED_NAME_BY_FIELD, String.class,
                null == user ? StringUtils.EMPTY : user.getUsername());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Timestamp now = TimestampUtils.now();
        User user = authenticationService.getPrincipal();
        // setFieldValByName不管实体是否持有这些属性，都强制更新
        // strictUpdateFill不会强制更新，只有属性为null时才会更新
        this.setFieldValByName(ENTITY_MODIFIED_DATE_FIELD, now, metaObject);
        this.setFieldValByName(ENTITY_MODIFIED_BY_FIELD, null == user ? 0L : user.getId(), metaObject);
        this.setFieldValByName(ENTITY_MODIFIED_NAME_BY_FIELD, null == user ? StringUtils.EMPTY : user.getUsername(), metaObject);
    }
}