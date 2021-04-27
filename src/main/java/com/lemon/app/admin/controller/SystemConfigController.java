package com.lemon.app.admin.controller;

import com.lemon.app.constants.AppConstants;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.app.core.ModuleBase;
import com.lemon.framework.core.annotation.ApiDescription;
import com.lemon.framework.log.LogTypeEnum;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 名称：api访问限制 前端控制器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/24
 */
@RestController
@RequestMapping(AppConstants.ADMIN_CONTEXT_ROOT_PATH + "/sys")
@ApiDescription(bizName = "biz!admin|sys", type = LogTypeEnum.SYSTEM)
public class SystemConfigController {

//    private final IApiLimitService apiLimitService;
//
//    public SystemConfigController(IApiLimitService apiLimitService) {
//        this.apiLimitService = apiLimitService;
//    }

    @RequiresAuthentication
    @GetMapping("/modules")
    public Object[] getModules() {
        return AppComponentHolder.getModules().stream()
                .map(ModuleBase::getModule)
                .toArray();
    }

//    /**
//     * 获取指定模块的api限流数据
//     */
//    @RequiresPermissions("admin:sys:api-limit-search")
//    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-sys"}, action = "action!admin|api-limit-search")
//    @GetMapping("/api-limit/search")
//    @ApiDescription(description = "biz-desc!admin|api-limit-search", args = {"#moduleName"}, resourceType = "ApiLimit", type = LogTypeEnum.SYSTEM)
//    public List<ApiLimit> selectAllApiLimit(@RequestParam("module") String moduleName) {
//        return apiLimitService.selectAllApiLimit(moduleName);
//    }

//    /**
//     * 修改Api限流配置
//     */
//    @RequiresPermissions("admin:sys:api-limit-update")
//    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-sys"}, action = "action!admin|api-limit-update")
//    @PostMapping("/api-limit/update")
//    @ApiDescription(description = "biz-desc!admin|api-limit-update", args = {"#apiLimit.key", "#apiLimit.period", "#apiLimit.count"}, resourceType = "ApiLimit", type = LogTypeEnum.SYSTEM)
//    public ApiLimit updateApiLimit(@RequestParam("module") String moduleName,
//                                   @Validated(UpdateValidatedGroup.class) @RequestBody ApiLimit apiLimit) {
//        return apiLimitService.updateApiLimit(moduleName, apiLimit);
//    }
}
