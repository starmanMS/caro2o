package cn.ms.car.audit.service;

import cn.ms.car.audit.domain.BusCarPackageAudit;
import cn.ms.car.audit.domain.vo.HandleAuditParam;
import cn.ms.car.audit.domain.vo.StartAuditParam;

import java.util.List;

/**
 * 审核信息Service接口
 *
 * @author ms
 * @date 2023-04-08
 */
public interface IBusCarPackageAuditService {
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
     * 批量删除审核信息
     *
     * @param ids 需要删除的审核信息主键集合
     * @return 结果
     */
    public int deleteBusCarPackageAuditByIds(Long[] ids);

    /**
     * 删除审核信息信息
     *
     * @param id 审核信息主键
     * @return 结果
     */
    public int deleteBusCarPackageAuditById(Long id);

    /**
     * 开始审核功能
     *
     * @param param 参数
     */
    void startAudit(StartAuditParam param);

    /**
     * 撤销审核
     *
     * @param id 套餐审核 id
     * @return 结果
     */
    int cancelAudit(Long id);

    /**
     * 待办审核列表查询
     */
    List<BusCarPackageAudit> selectAuditTodoList();

    void handleAudit(HandleAuditParam param);

    List<BusCarPackageAudit> selectAuditDoneList();

    List<BusCarPackageAudit> selectAuditListByType(String type);
}
