package cn.ms.car.appointment.mapper;

import cn.ms.car.appointment.domain.BusStatementItem;

import java.util.List;

/**
 * 结算单明细Mapper接口
 *
 * @author ms
 * @date 2023-04-02
 */
public interface BusStatementItemMapper {
    /**
     * 查询结算单明细列表
     *
     * @param statementId 结算单明细
     * @return 结算单明细集合
     */
    List<BusStatementItem> selectBusStatementItemList(Long statementId);

    /**
     * 删除结算单明细
     *
     * @param id 结算单明细主键
     * @return 结果
     */
    int deleteBusStatementItemByStatementId(Long id);

    int batchInsertBusStatementItem(List<BusStatementItem> items);

}
