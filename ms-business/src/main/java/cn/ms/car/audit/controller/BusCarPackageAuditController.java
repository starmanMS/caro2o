package cn.ms.car.audit.controller;

import cn.ms.car.audit.domain.BusCarPackageAudit;
import cn.ms.car.audit.domain.vo.StartAuditParam;
import cn.ms.car.audit.service.IBusCarPackageAuditService;
import cn.ms.car.flow.service.IProcessService;
import cn.ms.car.common.annotation.Log;
import cn.ms.car.common.core.controller.BaseController;
import cn.ms.car.common.core.domain.AjaxResult;
import cn.ms.car.common.core.page.TableDataInfo;
import cn.ms.car.common.enums.BusinessType;
import cn.ms.car.common.utils.poi.ExcelUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 审核信息Controller
 *
 * @author ms
 * @date 2023-04-08
 */
@RestController
@RequestMapping("/audit/info")
public class BusCarPackageAuditController extends BaseController {
    @Autowired
    private IBusCarPackageAuditService busCarPackageAuditService;
    @Autowired
    private IProcessService processService;

    /**
     * 查询审核信息列表
     */
    @PreAuthorize("@ss.hasPermi('audit:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusCarPackageAudit busCarPackageAudit) {
        startPage();
        List<BusCarPackageAudit> list = busCarPackageAuditService.selectBusCarPackageAuditList(busCarPackageAudit);
        return getDataTable(list);
    }

    /**
     * 导出审核信息列表
     */
    @PreAuthorize("@ss.hasPermi('audit:info:export')")
    @Log(title = "审核信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusCarPackageAudit busCarPackageAudit) {
        List<BusCarPackageAudit> list = busCarPackageAuditService.selectBusCarPackageAuditList(busCarPackageAudit);
        ExcelUtil<BusCarPackageAudit> util = new ExcelUtil<BusCarPackageAudit>(BusCarPackageAudit.class);
        util.exportExcel(response, list, "审核信息数据");
    }

    /**
     * 获取审核信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('audit:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(busCarPackageAuditService.selectBusCarPackageAuditById(id));
    }

    @PreAuthorize("@ss.hasPermi('audit:info:history')")
    @GetMapping(value = "/{id}/histories")
    public AjaxResult historyList(@PathVariable("id") String instanceId) {
        return success(processService.selectHistoryTaskListByInstanceId(instanceId));
    }

    @PreAuthorize("@ss.hasPermi('audit:info:processing')")
    @GetMapping(value = "/{id}/processing")
    public void processing(@PathVariable("id") String instanceId, HttpServletResponse resp) {
        // JDK7 自动关闭资源
        try (InputStream stream = processService.getProcessingImageByInstanceId(instanceId)) {
            // 通过响应流将数据响应到浏览器
            IOUtils.copy(stream, resp.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增审核信息
     */
    @PreAuthorize("@ss.hasPermi('audit:info:start')")
    @Log(title = "审核信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult startAudit(@RequestBody StartAuditParam param) {
        busCarPackageAuditService.startAudit(param);
        return success();
    }

    /**
     * 修改审核信息
     */
    @PreAuthorize("@ss.hasPermi('audit:info:edit')")
    @Log(title = "审核信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusCarPackageAudit busCarPackageAudit) {
        return toAjax(busCarPackageAuditService.updateBusCarPackageAudit(busCarPackageAudit));
    }

    /**
     * 删除审核信息
     */
    @PreAuthorize("@ss.hasPermi('audit:info:remove')")
    @Log(title = "审核信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(busCarPackageAuditService.cancelAudit(id));
    }
}
