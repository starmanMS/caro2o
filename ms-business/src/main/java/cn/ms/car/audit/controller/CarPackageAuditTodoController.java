package cn.ms.car.audit.controller;

import cn.ms.car.audit.domain.BusCarPackageAudit;
import cn.ms.car.audit.domain.vo.HandleAuditParam;
import cn.ms.car.audit.service.IBusCarPackageAuditService;
import cn.ms.car.common.core.controller.BaseController;
import cn.ms.car.common.core.domain.AjaxResult;
import cn.ms.car.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 待办任务控制器
 *
 * @author ms
 * @date 2023-04-08
 */
@RestController
@RequestMapping("/audit/todo")
public class CarPackageAuditTodoController extends BaseController {

    @Autowired
    private IBusCarPackageAuditService busCarPackageAuditService;

    /**
     * 查询审核信息列表
     */
    @PreAuthorize("@ss.hasPermi('audit:todo:list')")
    @GetMapping("/list")
    public TableDataInfo list() {
        // 原理：利用当前线程进行绑定一个分页参数
        //      对第一条执行的 SQL 语句进行拦截，在 SQL 前先执行 COUNT 语句，再对后续执行的 SQL 语句追加 LIMIT 语句
        // startPage();
        List<BusCarPackageAudit> list = busCarPackageAuditService.selectAuditListByType(BusCarPackageAudit.TODO_LIST);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('audit:todo:handle')")
    @PutMapping("/handle")
    public AjaxResult handleAudit(@RequestBody HandleAuditParam param) {
        busCarPackageAuditService.handleAudit(param);
        return success();
    }
}
