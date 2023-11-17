package cn.ms.car.appointment.service;

import cn.ms.car.appointment.domain.BusStatement;
import cn.ms.car.appointment.domain.BusStatementItem;
import cn.ms.car.appointment.domain.vo.BusStatementVo;

import java.util.List;

/**
 * 结算单Service接口
 *
 * @author ms
 * @date 2023-04-01
 */
public interface IBusStatementService {
    /**
     * 查询结算单
     *
     * @param id 结算单主键
     * @return 结算单
     */
    public BusStatement selectBusStatementById(Long id);

    /**
     * 查询结算单列表
     *
     * @param busStatement 结算单
     * @return 结算单集合
     */
    public List<BusStatement> selectBusStatementList(BusStatement busStatement);

    /**
     * 新增结算单
     *
     * @param busStatement 结算单
     * @return 结果
     */
    public int insertBusStatement(BusStatementVo busStatement);

    /**
     * 结算单生成创建
     *
     * @param statement     结算单
     * @param appointmentId 预约 id
     * @return 结果
     */
    int insertBusStatement(BusStatement statement, Long appointmentId);

    /**
     * 修改结算单
     *
     * @param busStatement 结算单
     * @return 结果
     */
    public int updateBusStatement(BusStatementVo busStatement);

    /**
     * 批量删除结算单
     *
     * @param ids 需要删除的结算单主键集合
     * @return 结果
     */
    public int deleteBusStatementByIds(Long[] ids);

    /**
     * 删除结算单信息
     *
     * @param id 结算单主键
     * @return 结果
     */
    public int deleteBusStatementById(Long id);

    /**
     * 新增结算单明细
     *
     * @param vo 结算单 vo 对象（discountAmount, items）
     * @return 结果
     */
    int insertItems(BusStatementVo vo);

    List<BusStatementItem> selectItemsById(Long statementId);

    int doPrepay(Long id);

    /**
     * 基于预约 id 查询结算单对象
     *
     * @param appointmentId 预约 id
     * @return 结算单
     */
    BusStatement selectStatementByAppointmentId(Long appointmentId);
}
