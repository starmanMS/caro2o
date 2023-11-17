package cn.ms.car.appointment.mapper;

import java.util.List;

import cn.ms.car.appointment.domain.BusServiceItem;
import org.apache.ibatis.annotations.Param;

/**
 * 服务项Mapper接口
 *
 * @author ms
 * @date 2023-03-30
 */
public interface BusServiceItemMapper {
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
    public int insertBusServiceItem(BusServiceItem busServiceItem);

    /**
     * 修改服务项
     *
     * @param busServiceItem 服务项
     * @return 结果
     */
    public int updateBusServiceItem(BusServiceItem busServiceItem);

    /**
     * 删除服务项
     *
     * @param id 服务项主键
     * @return 结果
     */
    public int deleteBusServiceItemById(Long id);

    /**
     * 批量删除服务项
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusServiceItemByIds(Long[] ids);

    /**
     * 更新服务项状态
     *
     * @param id     服务项 id
     * @param status 需要更新的状态
     * @return 结果
     */
    int updateStatus(@Param("id") Long id, @Param("status") int status);

    void updateAuditStatus(@Param("id") Long id, @Param("auditStatus") int auditStatus);
}
