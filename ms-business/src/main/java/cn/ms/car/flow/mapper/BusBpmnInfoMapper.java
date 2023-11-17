package cn.ms.car.flow.mapper;

import java.util.List;
import cn.ms.car.flow.domain.BusBpmnInfo;

/**
 * 流程定义Mapper接口
 * 
 * @author ms
 * @date 2023-04-04
 */
public interface BusBpmnInfoMapper 
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
     * 删除流程定义
     * 
     * @param id 流程定义主键
     * @return 结果
     */
    public int deleteBusBpmnInfoById(Long id);

    /**
     * 批量删除流程定义
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusBpmnInfoByIds(Long[] ids);

    BusBpmnInfo selectLastestBpmnInfoByType(int type);
}
