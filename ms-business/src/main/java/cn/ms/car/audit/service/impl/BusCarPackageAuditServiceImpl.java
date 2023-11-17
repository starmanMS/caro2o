package cn.ms.car.audit.service.impl;

import cn.ms.car.audit.domain.BusCarPackageAudit;
import cn.ms.car.audit.enums.BpmnTypeEnum;
import cn.ms.car.audit.enums.CarPackageAuditStatusEnum;
import cn.ms.car.flow.domain.BusBpmnInfo;
import cn.ms.car.flow.service.IBusBpmnInfoService;
import cn.ms.car.flow.service.IProcessService;
import cn.ms.car.appointment.domain.BusServiceItem;
import cn.ms.car.appointment.domain.vo.BusServiceItemVo;
import cn.ms.car.appointment.enums.ServiceItemAuditStatusEnum;
import cn.ms.car.appointment.service.IBusServiceItemService;
import cn.ms.car.audit.domain.vo.HandleAuditParam;
import cn.ms.car.audit.domain.vo.StartAuditParam;
import cn.ms.car.audit.mapper.BusCarPackageAuditMapper;
import cn.ms.car.audit.service.IBusCarPackageAuditService;
import cn.ms.car.common.utils.DateUtils;
import cn.ms.car.common.utils.PageUtils;
import cn.ms.car.common.utils.SecurityUtils;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 审核信息Service业务层处理
 *
 * @author ms
 * @date 2023-04-08
 */
@Service
public class BusCarPackageAuditServiceImpl implements IBusCarPackageAuditService {
    @Autowired
    private BusCarPackageAuditMapper busCarPackageAuditMapper;
    @Autowired
    private IBusServiceItemService serviceItemService;
    @Autowired
    private IBusBpmnInfoService bpmnInfoService;
    @Autowired
    private IProcessService processService;

    /**
     * 查询审核信息
     *
     * @param id 审核信息主键
     * @return 审核信息
     */
    @Override
    public BusCarPackageAudit selectBusCarPackageAuditById(Long id) {
        return busCarPackageAuditMapper.selectBusCarPackageAuditById(id);
    }

    /**
     * 查询审核信息列表
     *
     * @param busCarPackageAudit 审核信息
     * @return 审核信息
     */
    @Override
    public List<BusCarPackageAudit> selectBusCarPackageAuditList(BusCarPackageAudit busCarPackageAudit) {
        return busCarPackageAuditMapper.selectBusCarPackageAuditList(busCarPackageAudit);
    }

    /**
     * 新增审核信息
     *
     * @param busCarPackageAudit 审核信息
     * @return 结果
     */
    @Override
    public int insertBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit) {
        busCarPackageAudit.setCreateTime(DateUtils.getNowDate());
        return busCarPackageAuditMapper.insertBusCarPackageAudit(busCarPackageAudit);
    }

    /**
     * 修改审核信息
     *
     * @param busCarPackageAudit 审核信息
     * @return 结果
     */
    @Override
    public int updateBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit) {
        return busCarPackageAuditMapper.updateBusCarPackageAudit(busCarPackageAudit);
    }

    /**
     * 批量删除审核信息
     *
     * @param ids 需要删除的审核信息主键
     * @return 结果
     */
    @Override
    public int deleteBusCarPackageAuditByIds(Long[] ids) {
        return busCarPackageAuditMapper.deleteBusCarPackageAuditByIds(ids);
    }

    /**
     * 删除审核信息信息
     *
     * @param id 审核信息主键
     * @return 结果
     */
    @Override
    public int deleteBusCarPackageAuditById(Long id) {
        return busCarPackageAuditMapper.deleteBusCarPackageAuditById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void startAudit(StartAuditParam param) {
        // 1. 根据服务项 id 查询服务项对象
        BusServiceItem serviceItem = serviceItemService.selectBusServiceItemById(param.getServiceItemId());
        Assert.notNull(serviceItem, "参数错误，找不到服务项");
        // 2. 状态判断：必须是套餐 && 已下架 && (初始化 || 审核拒绝)
        Integer auditStatus = serviceItem.getAuditStatus();
        Assert.state(
                BusServiceItemVo.PACKAGE_YES.equals(serviceItem.getCarPackage()) &&
                        BusServiceItemVo.SALE_STATUS_NO.equals(serviceItem.getSaleStatus()) &&
                        (ServiceItemAuditStatusEnum.INITIALING.ordinal() == auditStatus || ServiceItemAuditStatusEnum.REJECTED.ordinal() == auditStatus)
                , "非法状态，无法发起审核"
        );
        // 3. 构建套餐审核信息对象（流程实例 id 暂不设置）
        BusCarPackageAudit audit = this.buildAudit(serviceItem, param);
        // 4. 保存审核信息对象（得到自增 id）
        this.insertBusCarPackageAudit(audit);
        // 5. 查询流程信息对象（BpmnInfo），得到流程定义 key、版本号
        BusBpmnInfo bpmnInfo = bpmnInfoService.selectLastestBpmnInfoByType(BpmnTypeEnum.CAR_PACKAGE.ordinal());
        // 6. 构建流程变量（折扣金额、审核人（店长&财务））
        Map<String, Object> variables = new HashMap<>();
        // TODO 存入数字类型的值时，直接使用字符串
        variables.put("discountAmount", serviceItem.getDiscountPrice().longValue() + "");
        variables.put("shopOwnerId", param.getShopOwnerId());
        variables.put("financalId", param.getFinancialId());
        // 7. 开启流程实例（Activiti）
        String instanceId = processService.startProcessInstance(bpmnInfo.getProcessDefinitionKey(), audit.getId() + "", variables);
        // 8. 更新套餐审核信息的流程实例 id
        audit.setInstanceId(instanceId);
        this.updateBusCarPackageAudit(audit);

        // 9. 更新服务项的状态为审核中
        serviceItemService.updateAuditStatus(serviceItem.getId(), ServiceItemAuditStatusEnum.AUDITING.ordinal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int cancelAudit(Long id) {
        // 1. 基于 id 查询套餐信息
        BusCarPackageAudit audit = this.selectBusCarPackageAuditById(id);
        Assert.notNull(audit, "参数错误，套餐审核不存在");
        // 2. 判断状态只有审核中允许撤销
        Assert.state(CarPackageAuditStatusEnum.AUDITING.ordinal() == audit.getStatus(), "只有审核中才允许撤销");
        // 3. 更新套餐审核状态为撤销审核
        busCarPackageAuditMapper.updateStatus(id, CarPackageAuditStatusEnum.CANCELED.ordinal());
        // 4. 删除流程实例
        processService.deleteProcessInstance(audit.getInstanceId(), "[" + SecurityUtils.getUsername() + "]撤销审核");
        // 5. 更新服务项状态为初始化
        serviceItemService.updateAuditStatus(audit.getServiceItemId(), ServiceItemAuditStatusEnum.INITIALING.ordinal());
        return 1;
    }

    @Override
    public List<BusCarPackageAudit> selectAuditTodoList() {
        // 1. 得到流程定义信息对象（流程定义 key，流程定义版本）
        BusBpmnInfo bpmnInfo = bpmnInfoService.selectLastestBpmnInfoByType(BpmnTypeEnum.CAR_PACKAGE.ordinal());
        // 2. 通过 Activiti API 查询业务标识列表
        List<String> businessKeyList = processService.selectBusinessKeyList(bpmnInfo.getProcessDefinitionKey(), SecurityUtils.getUserId());

        if (CollectionUtils.isEmpty(businessKeyList)) {
            // 如果查不到业务标识，返回空数据
            return Collections.emptyList();
        }

        PageUtils.startPage(); // 开始分页
        // 3. 通过业务标识列表查到审核信息列表
        return busCarPackageAuditMapper.selectBusCarPackageAuditByIds(businessKeyList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleAudit(HandleAuditParam param) {
        // 1. 基于套餐 id 查询套餐对象，得到流程实例 id
        BusCarPackageAudit audit = this.selectBusCarPackageAuditById(param.getAuditId());
        Assert.notNull(audit, "参数错误，审核对象不存在");
        Assert.state(CarPackageAuditStatusEnum.AUDITING.ordinal() == audit.getStatus(), "非法状态");

        // 2. Activiti API 处理任务
        boolean ended = processService.handleTaskByInstanceIdAndAssignee(audit.getInstanceId(), SecurityUtils.getUserId(), param);
        // 3. 根据当前审核状态更新套餐信息、服务项的状态
        if (ended) {
            // 套餐信息：基于提交的审核状态，更新为通过或拒绝
            busCarPackageAuditMapper.updateStatus(param.getAuditId(),
                    param.isPassed() ? CarPackageAuditStatusEnum.PASSED.ordinal() : CarPackageAuditStatusEnum.REJECTED.ordinal());
            // 服务项：基于提交的审核状态，更新为通过或拒绝
            serviceItemService.updateAuditStatus(audit.getServiceItemId(),
                    param.isPassed() ? ServiceItemAuditStatusEnum.PASSED.ordinal() : ServiceItemAuditStatusEnum.REJECTED.ordinal());
        }
    }

    @Override
    public List<BusCarPackageAudit> selectAuditDoneList() {
        // 1. 根据类型查询最新的流程信息对象
        BusBpmnInfo bpmnInfo = bpmnInfoService.selectLastestBpmnInfoByType(BpmnTypeEnum.CAR_PACKAGE.ordinal());

        // 2. 通过 Activiti API 查询 businessKey 集合
        Long userId = SecurityUtils.getUserId();
        List<String> businessKeyList = processService.selectBusinessKeyList(bpmnInfo.getProcessDefinitionKey(), userId, BusCarPackageAudit.DONE_LIST);
        if (CollectionUtils.isEmpty(businessKeyList)) {
            return Collections.emptyList();
        }

        PageUtils.startPage(); // 开始分页
        // 3. 根据 businessKey 集合查询套餐信息列表
        return busCarPackageAuditMapper.selectBusCarPackageAuditByIds(businessKeyList);
    }

    @Override
    public List<BusCarPackageAudit> selectAuditListByType(String type) {
        // 1. 根据类型查询最新的流程信息对象
        BusBpmnInfo bpmnInfo = bpmnInfoService.selectLastestBpmnInfoByType(BpmnTypeEnum.CAR_PACKAGE.ordinal());

        // 2. 通过 Activiti API 查询 businessKey 集合
        Long userId = SecurityUtils.getUserId();
        List<String> businessKeyList = processService.selectBusinessKeyList(bpmnInfo.getProcessDefinitionKey(), userId, type);
        if (CollectionUtils.isEmpty(businessKeyList)) {
            return Collections.emptyList();
        }

        PageUtils.startPage(); // 开始分页
        // 3. 根据 businessKey 集合查询套餐信息列表
        return busCarPackageAuditMapper.selectBusCarPackageAuditByIds(businessKeyList);
    }

    private BusCarPackageAudit buildAudit(BusServiceItem serviceItem, StartAuditParam param) {
        BusCarPackageAudit audit = new BusCarPackageAudit();
        // 当前登录用户
        audit.setCreatorId(SecurityUtils.getUserId() + "");
        audit.setInfo(param.getInfo());
        audit.setServiceItemId(serviceItem.getId());
        audit.setServiceItemInfo(serviceItem.getInfo());
        audit.setServiceItemName(serviceItem.getName());
        // 折扣价
        audit.setServiceItemPrice(serviceItem.getDiscountPrice());
        audit.setStatus(CarPackageAuditStatusEnum.AUDITING.ordinal());
        audit.setCreateTime(new Date());
        return audit;
    }
}
