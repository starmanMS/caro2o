package cn.ms.car.appointment.service;

import java.util.List;

import cn.ms.car.appointment.domain.BusAppointment;
import cn.ms.car.appointment.domain.BusStatement;
import cn.ms.car.appointment.domain.vo.BusAppointmentVo;

/**
 * 养修信息预约Service接口
 *
 * @author ms
 * @date 2023-03-27
 */
public interface IBusAppointmentService {
    /**
     * 查询养修信息预约
     *
     * @param id 养修信息预约主键
     * @return 养修信息预约
     */
    public BusAppointment selectBusAppointmentById(Long id);

    /**
     * 查询养修信息预约列表
     *
     * @param busAppointment 养修信息预约
     * @return 养修信息预约集合
     */
    public List<BusAppointment> selectBusAppointmentList(BusAppointment busAppointment);

    /**
     * 新增养修信息预约
     *
     * @param busAppointment 养修信息预约
     * @return 结果
     */
    public int insertBusAppointment(BusAppointmentVo busAppointment);

    /**
     * 修改养修信息预约
     *
     * @param busAppointment 养修信息预约
     * @return 结果
     */
    public int updateBusAppointment(BusAppointmentVo busAppointment);

    /**
     * 批量删除养修信息预约
     *
     * @param ids 需要删除的养修信息预约主键集合
     * @return 结果
     */
    public int deleteBusAppointmentByIds(Long[] ids);

    /**
     * 删除养修信息预约信息
     *
     * @param id 养修信息预约主键
     * @return 结果
     */
    public int deleteBusAppointmentById(Long id);

    /**
     * 客户到店
     *
     * @param id 预约 id
     * @return 是否更新成功
     */
    int updateArrival(Long id);

    /**
     * 取消预约
     *
     * @param id 预约 id
     * @return 结果
     */
    int updateCancel(Long id);

    /**
     * 创建结算单
     *
     * @param id 预约信息 id
     * @return 结算单
     */
    BusStatement doCreateStatement(Long id);

    /**
     * 更新支付成功状态
     *
     * @param appointmentId 预约 id
     * @return 受影响的行数
     */
    int updatePaySuccess(Long appointmentId);
}
