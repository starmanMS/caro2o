package cn.ms.car.appointment.controller;

import cn.ms.car.appointment.domain.BusAppointment;
import cn.ms.car.appointment.domain.vo.BusAppointmentVo;
import cn.ms.car.appointment.service.IBusAppointmentService;
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
 * 养修信息预约Controller
 *
 * @author ms
 * @date 2023-03-27
 */
@Validated
@RestController
@RequestMapping("/appointment/info")
public class BusAppointmentController extends BaseController {

    @Autowired
    private IBusAppointmentService busAppointmentService;

    /**
     * 查询养修信息预约列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusAppointment busAppointment) {
        startPage();
        List<BusAppointment> list = busAppointmentService.selectBusAppointmentList(busAppointment);
        return getDataTable(list);
    }

    /**
     * 导出养修信息预约列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:export')")
    @Log(title = "养修信息预约", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusAppointment busAppointment) {
        List<BusAppointment> list = busAppointmentService.selectBusAppointmentList(busAppointment);
        ExcelUtil<BusAppointment> util = new ExcelUtil<BusAppointment>(BusAppointment.class);
        util.exportExcel(response, list, "养修信息预约数据");
    }

    /**
     * 获取养修信息预约详细信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(busAppointmentService.selectBusAppointmentById(id));
    }

    /**
     * 新增养修信息预约
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:add')")
    @Log(title = "养修信息预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BusAppointmentVo busAppointment) {
        return toAjax(busAppointmentService.insertBusAppointment(busAppointment));
    }

    /**
     * 修改养修信息预约
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:edit')")
    @Log(title = "养修信息预约", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BusAppointmentVo busAppointment) {
        return toAjax(busAppointmentService.updateBusAppointment(busAppointment));
    }

    @Log(title = "生成结算单", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/statements")
    public AjaxResult edit(@PathVariable Long id) {
        return success(busAppointmentService.doCreateStatement(id));
    }

    /**
     * 客户到店
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:arrival')")
    @Log(title = "客户到店", businessType = BusinessType.UPDATE)
    @PutMapping("/arrival/{id}")
    public AjaxResult arrivalInfo(@PathVariable Long id) {
        return toAjax(busAppointmentService.updateArrival(id));
    }

    /**
     * 取消预约
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:cancel')")
    @Log(title = "取消预约", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{id}")
    public AjaxResult cancelInfo(@PathVariable Long id) {
        return toAjax(busAppointmentService.updateCancel(id));
    }

    /**
     * 删除养修信息预约
     */
    @PreAuthorize("@ss.hasPermi('appointment:info:remove')")
    @Log(title = "养修信息预约", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(busAppointmentService.deleteBusAppointmentByIds(ids));
    }
}
