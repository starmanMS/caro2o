package cn.ms.car.audit.controller;

import cn.ms.car.audit.domain.BusCarPackageAudit;
import cn.ms.car.audit.service.IBusCarPackageAuditService;
import cn.ms.car.common.core.controller.BaseController;
import cn.ms.car.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 已办任务控制器
 *
 * @author ms
 * @date 2023-04-08
 */
@RestController
@RequestMapping("/audit/done")
public class CarPackageAuditDoneController extends BaseController {

    @Autowired
    private IBusCarPackageAuditService busCarPackageAuditService;

    /**
     * 查询审核信息列表
     */
    @PreAuthorize("@ss.hasPermi('audit:done:list')")
    @GetMapping("/list")
    public TableDataInfo list() {
        List<BusCarPackageAudit> list = busCarPackageAuditService.selectAuditListByType(BusCarPackageAudit.DONE_LIST);
        return getDataTable(list);
    }
}
