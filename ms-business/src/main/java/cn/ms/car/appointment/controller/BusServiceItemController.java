package cn.ms.car.appointment.controller;

import cn.ms.car.appointment.domain.BusServiceItem;
import cn.ms.car.appointment.domain.vo.BusServiceItemVo;
import cn.ms.car.appointment.domain.vo.ServiceItemAuditInfo;
import cn.ms.car.appointment.service.IBusServiceItemService;
import cn.ms.car.common.annotation.Log;
import cn.ms.car.common.core.controller.BaseController;
import cn.ms.car.common.core.domain.AjaxResult;
import cn.ms.car.common.core.page.TableDataInfo;
import cn.ms.car.common.enums.BusinessType;
import cn.ms.car.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务项Controller
 *
 * @author ms
 * @date 2023-03-30
 */
@Validated
@RestController
@RequestMapping("/appointment/serviceItem")
public class BusServiceItemController extends BaseController {
    @Autowired
    private IBusServiceItemService busServiceItemService;

    /**
     * 查询服务项列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:serviceItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusServiceItem busServiceItem) {
        startPage();
        List<BusServiceItem> list = busServiceItemService.selectBusServiceItemList(busServiceItem);
        return getDataTable(list);
    }

    /**
     * 导出服务项列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:serviceItem:export')")
    @Log(title = "服务项", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusServiceItem busServiceItem) {
        List<BusServiceItem> list = busServiceItemService.selectBusServiceItemList(busServiceItem);
        ExcelUtil<BusServiceItem> util = new ExcelUtil<BusServiceItem>(BusServiceItem.class);
        util.exportExcel(response, list, "服务项数据");
    }

    /**
     * 获取服务项详细信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:serviceItem:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(busServiceItemService.selectBusServiceItemById(id));
    }

    /**
     * 获取服务项审核信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:serviceItem:auditInfo')")
    @GetMapping(value = "/auditInfo/{id}")
    public AjaxResult getAuditInfo(@PathVariable("id") Long id) {
        ServiceItemAuditInfo info = busServiceItemService.selectAuditInfoById(id);
        return success(info);
    }

    /**
     * 新增服务项
     */
    @PreAuthorize("@ss.hasPermi('appointment:serviceItem:add')")
    @Log(title = "服务项", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusServiceItemVo busServiceItem) {
        return toAjax(busServiceItemService.insertBusServiceItem(busServiceItem));
    }

    /**
     * 修改服务项
     */
    @PreAuthorize("@ss.hasPermi('appointment:serviceItem:edit')")
    @Log(title = "服务项", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusServiceItemVo busServiceItem) {
        return toAjax(busServiceItemService.updateBusServiceItem(busServiceItem));
    }

    /**
     * 修改上架状态
     */
    @Log(title = "修改上架状态", businessType = BusinessType.UPDATE)
    @PutMapping("/saleStatus/{id}")
    public AjaxResult updateSaleStatus(@PathVariable Long id) {
        return toAjax(busServiceItemService.updateSaleStatus(id));
    }

    /**
     * 删除服务项
     */
    @PreAuthorize("@ss.hasPermi('appointment:serviceItem:remove')")
    @Log(title = "服务项", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(busServiceItemService.deleteBusServiceItemByIds(ids));
    }
}
