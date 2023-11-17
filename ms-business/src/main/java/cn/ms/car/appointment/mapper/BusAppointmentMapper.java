package cn.ms.car.appointment.mapper;

import cn.ms.car.appointment.domain.BusAppointment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 养修信息预约Mapper接口
 *
 * @author ms
 * @date 2023-03-27
 */
public interface BusAppointmentMapper {
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
    public int insertBusAppointment(BusAppointment busAppointment);

    /**
     * 修改养修信息预约
     *
     * @param busAppointment 养修信息预约
     * @return 结果
     */
    public int updateBusAppointment(BusAppointment busAppointment);

    /**
     * 删除养修信息预约
     *
     * @param id 养修信息预约主键
     * @return 结果
     */
    public int deleteBusAppointmentById(Long id);

    /**
     * 批量删除养修信息预约
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusAppointmentByIds(Long[] ids);

    /**
     * 更新到店时间与状态
     *
     * @param id 预约信息 id
     * @return 结果
     */
    int updateArrival(Long id);

    /**
     * 专门用于更新状态
     *
     * @param id     预约信息 id
     * @param status 新的状态
     * @return 结果
     */
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
