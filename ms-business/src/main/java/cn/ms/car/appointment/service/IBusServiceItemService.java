package cn.ms.car.appointment.service;

import cn.ms.car.appointment.domain.BusServiceItem;
import cn.ms.car.appointment.domain.vo.BusServiceItemVo;
import cn.ms.car.appointment.domain.vo.ServiceItemAuditInfo;

import java.util.List;

/**
 * 服务项Service接口
 *
 * @author ms
 * @date 2023-03-30
 */
public interface IBusServiceItemService {
    /**
     * 查询服务项
     *
     * @param id 服务项主键
     * @return 服务项
     */
    public BusServiceItem selectBusServiceItemById(Long id);

    /**
     * 查询服务项列表
     *
     * @param busServiceItem 服务项
     * @return 服务项集合
     */
    public List<BusServiceItem> selectBusServiceItemList(BusServiceItem busServiceItem);

    /**
     * 新增服务项
     *
     * @param busServiceItem 服务项
     * @return 结果
     */
    public int insertBusServiceItem(BusServiceItemVo busServiceItem);

    /**
     * 修改服务项
     *
     * @param busServiceItem 服务项
     * @return 结果
     */
    public int updateBusServiceItem(BusServiceItemVo busServiceItem);

    /**
     * 批量删除服务项
     *
     * @param ids 需要删除的服务项主键集合
     * @return 结果
     */
    public int deleteBusServiceItemByIds(Long[] ids);

    /**
     * 删除服务项信息
     *
     * @param id 服务项主键
     * @return 结果
     */
    public int deleteBusServiceItemById(Long id);

    /**
     * 更新服务项上下架状态
     *
     * @param id 服务项 id
     * @return 结果
     */
    int updateSaleStatus(Long id);

    /**
     * 根据服务项 id 查询审核信息
     *
     * @param id 服务项 id
     * @return 审核信息
     */
    ServiceItemAuditInfo selectAuditInfoById(Long id);

    void updateAuditStatus(Long id, int auditStatus);
}
