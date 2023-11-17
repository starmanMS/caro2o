package cn.ms.car.audit.mapper;

import java.util.List;

import cn.ms.car.audit.domain.BusCarPackageAudit;
import org.apache.ibatis.annotations.Param;

/**
 * 审核信息Mapper接口
 * 
 * @author ms
 * @date 2023-04-08
 */
public interface BusCarPackageAuditMapper 
{
    /**
     * 查询审核信息
     * 
     * @param id 审核信息主键
     * @return 审核信息
     */
    public BusCarPackageAudit selectBusCarPackageAuditById(Long id);

    /**
     * 查询审核信息列表
     * 
     * @param busCarPackageAudit 审核信息
     * @return 审核信息集合
     */
    public List<BusCarPackageAudit> selectBusCarPackageAuditList(BusCarPackageAudit busCarPackageAudit);

    /**
     * 新增审核信息
     * 
     * @param busCarPackageAudit 审核信息
     * @return 结果
     */
    public int insertBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit);

    /**
     * 修改审核信息
     * 
     * @param busCarPackageAudit 审核信息
     * @return 结果
     */
    public int updateBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit);

    /**
     * 删除审核信息
     * 
     * @param id 审核信息主键
     * @return 结果
     */
    public int deleteBusCarPackageAuditById(Long id);

    /**
     * 批量删除审核信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusCarPackageAuditByIds(Long[] ids);

    void updateStatus(@Param("id") Long id, @Param("status") int status);

    List<BusCarPackageAudit> selectBusCarPackageAuditByIds(List<String> idList);
}
