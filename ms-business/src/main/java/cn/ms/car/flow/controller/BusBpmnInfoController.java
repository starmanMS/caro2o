package cn.ms.car.flow.controller;

import cn.ms.car.flow.domain.BpmnInfoVo;
import cn.ms.car.flow.domain.BusBpmnInfo;
import cn.ms.car.common.annotation.Log;
import cn.ms.car.common.core.controller.BaseController;
import cn.ms.car.common.core.domain.AjaxResult;
import cn.ms.car.common.core.page.TableDataInfo;
import cn.ms.car.common.enums.BusinessType;
import cn.ms.car.common.utils.poi.ExcelUtil;
import cn.ms.car.flow.service.IBusBpmnInfoService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 流程定义Controller
 *
 * @author ms
 * @date 2023-04-04
 */
@Validated
@RestController
@RequestMapping("/flow/bpmnInfo")
public class BusBpmnInfoController extends BaseController {
    @Autowired
    private IBusBpmnInfoService busBpmnInfoService;

    /**
     * 查询流程定义列表
     */
    @PreAuthorize("@ss.hasPermi('flow:bpmnInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusBpmnInfo busBpmnInfo) {
        startPage();
        List<BusBpmnInfo> list = busBpmnInfoService.selectBusBpmnInfoList(busBpmnInfo);
        return getDataTable(list);
    }

    /**
     * 导出流程定义列表
     */
    @PreAuthorize("@ss.hasPermi('flow:bpmnInfo:export')")
    @Log(title = "流程定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusBpmnInfo busBpmnInfo) {
        List<BusBpmnInfo> list = busBpmnInfoService.selectBusBpmnInfoList(busBpmnInfo);
        ExcelUtil<BusBpmnInfo> util = new ExcelUtil<BusBpmnInfo>(BusBpmnInfo.class);
        util.exportExcel(response, list, "流程定义数据");
    }

    /**
     * 获取流程定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('flow:bpmnInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(busBpmnInfoService.selectBusBpmnInfoById(id));
    }

    /**
     * 获取流程定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('flow:bpmnInfo:query')")
    @GetMapping(value = "/{id}/{type}")
    public void getBpmnFile(@PathVariable("id") Long id, @PathVariable("type") String type, HttpServletResponse resp) throws IOException {
        InputStream bpmnFile = busBpmnInfoService.selectBpmnInfoFile(id, type);

        if (bpmnFile == null) {
            return;
        }

        try {
            // 写出文件内容
            IOUtils.copy(bpmnFile, resp.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bpmnFile.close();
        }
    }

    /**
     * 新增流程定义
     */
    @PreAuthorize("@ss.hasPermi('flow:bpmnInfo:deploy')")
    @Log(title = "流程定义部署", businessType = BusinessType.INSERT)
    @PostMapping("/deploy")
    public AjaxResult deploy(BpmnInfoVo vo) {
        busBpmnInfoService.deployBpmnInfo(vo);
        return success();
    }

    /**
     * 修改流程定义
     */
    @PreAuthorize("@ss.hasPermi('flow:bpmnInfo:edit')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusBpmnInfo busBpmnInfo) {
        return toAjax(busBpmnInfoService.updateBusBpmnInfo(busBpmnInfo));
    }

    /**
     * 删除流程定义
     */
    @PreAuthorize("@ss.hasPermi('flow:bpmnInfo:remove')")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(busBpmnInfoService.deleteBusBpmnInfoByIds(ids));
    }
}
