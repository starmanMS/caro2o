package cn.ms.car.flow.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import cn.ms.car.flow.domain.BpmnInfoVo;
import cn.ms.car.flow.domain.BusBpmnInfo;

/**
 * 流程定义Service接口
 * 
 * @author ms
 * @date 2023-04-04
 */
public interface IBusBpmnInfoService 
{
    /**
     * 查询流程定义
     * 
     * @param id 流程定义主键
     * @return 流程定义
     */
    public BusBpmnInfo selectBusBpmnInfoById(Long id);

    /**
     * 查询流程定义列表
     * 
     * @param busBpmnInfo 流程定义
     * @return 流程定义集合
     */
    public List<BusBpmnInfo> selectBusBpmnInfoList(BusBpmnInfo busBpmnInfo);

    /**
     * 新增流程定义
     * 
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    public int insertBusBpmnInfo(BusBpmnInfo busBpmnInfo);

    /**
     * 修改流程定义
     * 
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    public int updateBusBpmnInfo(BusBpmnInfo busBpmnInfo);

    /**
     * 批量删除流程定义
     * 
     * @param ids 需要删除的流程定义主键集合
     * @return 结果
     */
    public int deleteBusBpmnInfoByIds(Long[] ids);

    /**
     * 删除流程定义信息
     * 
     * @param id 流程定义主键
     * @return 结果
     */
    public int deleteBusBpmnInfoById(Long id);

    void deployBpmnInfo(BpmnInfoVo vo);

    InputStream selectBpmnInfoFile(Long id, String type);

    BigDecimal getCarPackageAuditDiscountPrice();

    BusBpmnInfo selectLastestBpmnInfoByType(int type);
}
