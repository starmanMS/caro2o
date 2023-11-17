package cn.ms.car.appointment.controller;

import cn.ms.car.appointment.domain.BusStatement;
import cn.ms.car.appointment.domain.vo.BusStatementVo;
import cn.ms.car.appointment.service.IBusStatementService;
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
 * 结算单Controller
 *
 * @author ms
 * @date 2023-04-01
 */
@Validated
@RestController
@RequestMapping("/appointment/statement")
public class BusStatementController extends BaseController {
    @Autowired
    private IBusStatementService busStatementService;

    /**
     * 查询结算单列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusStatement busStatement) {
        startPage();
        List<BusStatement> list = busStatementService.selectBusStatementList(busStatement);
        return getDataTable(list);
    }

    /**
     * 导出结算单列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:export')")
    @Log(title = "结算单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusStatement busStatement) {
        List<BusStatement> list = busStatementService.selectBusStatementList(busStatement);
        ExcelUtil<BusStatement> util = new ExcelUtil<BusStatement>(BusStatement.class);
        util.exportExcel(response, list, "结算单数据");
    }

    /**
     * 获取结算单详细信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(busStatementService.selectBusStatementById(id));
    }

    /**
     * 预支付接口
     */
    @PostMapping("/prepay/{id}")
    public AjaxResult prepay(@PathVariable Long id) {
        logger.info("[结算单] 发起预支付请求，准备支付订单：{}", id);
        return toAjax(busStatementService.doPrepay(id));
    }

    /**
     * 新增结算单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:add')")
    @Log(title = "结算单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusStatementVo busStatement) {
        return toAjax(busStatementService.insertBusStatement(busStatement));
    }

    /**
     * 结算单明细查询
     */
    @GetMapping("/items/{statementId}")
    public AjaxResult getItemsByStatementId(@PathVariable Long statementId) {
        return success(busStatementService.selectItemsById(statementId));
    }

    /**
     * 新增结算单明细
     * params = {discountAmount: 1.0, items: [{}, {}]}
     */
    @Log(title = "结算单明细", businessType = BusinessType.INSERT)
    @PostMapping("/items")
    public AjaxResult addItems(@RequestBody BusStatementVo vo) {
        return toAjax(busStatementService.insertItems(vo));
    }

    /**
     * 修改结算单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:edit')")
    @Log(title = "结算单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusStatementVo busStatement) {
        return toAjax(busStatementService.updateBusStatement(busStatement));
    }

    /**
     * 删除结算单
     * 布隆过滤器
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:remove')")
    @Log(title = "结算单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(busStatementService.deleteBusStatementByIds(ids));
    }
}
