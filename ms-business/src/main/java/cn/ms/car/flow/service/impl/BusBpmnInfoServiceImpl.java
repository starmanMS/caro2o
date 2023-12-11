package cn.ms.car.flow.service.impl;

import cn.ms.car.flow.domain.BpmnInfoVo;
import cn.ms.car.flow.domain.BusBpmnInfo;
import cn.ms.car.flow.mapper.BusBpmnInfoMapper;
import cn.ms.car.flow.service.IProcessService;
import cn.ms.car.audit.enums.BpmnTypeEnum;
import cn.ms.car.audit.utils.ActivitiUtils;
import cn.ms.car.flow.service.IBusBpmnInfoService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * 流程定义Service业务层处理
 *
 * @author ms
 * @date 2023-04-04
 */
@Service
public class BusBpmnInfoServiceImpl implements IBusBpmnInfoService {
    @Autowired
    private BusBpmnInfoMapper busBpmnInfoMapper;
    @Autowired
    private IProcessService processService;

    /**
     * 查询流程定义
     *
     * @param id 流程定义主键
     * @return 流程定义
     */
    @Override
    public BusBpmnInfo selectBusBpmnInfoById(Long id) {
        return busBpmnInfoMapper.selectBusBpmnInfoById(id);
    }

    /**
     * 查询流程定义列表
     *
     * @param busBpmnInfo 流程定义
     * @return 流程定义
     */
    @Override
    public List<BusBpmnInfo> selectBusBpmnInfoList(BusBpmnInfo busBpmnInfo) {
        return busBpmnInfoMapper.selectBusBpmnInfoList(busBpmnInfo);
    }

    /**
     * 新增流程定义
     *
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    @Override
    public int insertBusBpmnInfo(BusBpmnInfo busBpmnInfo) {
        return busBpmnInfoMapper.insertBusBpmnInfo(busBpmnInfo);
    }

    /**
     * 修改流程定义
     *
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    @Override
    public int updateBusBpmnInfo(BusBpmnInfo busBpmnInfo) {
        return busBpmnInfoMapper.updateBusBpmnInfo(busBpmnInfo);
    }

    /**
     * 批量删除流程定义
     *
     * @param ids 需要删除的流程定义主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteBusBpmnInfoByIds(Long[] ids) {
        Assert.state(ids != null && ids.length == 1, "id 参数错误");
        // 查询流程信息对象
        BusBpmnInfo bpmnInfo = this.selectBusBpmnInfoById(ids[0]);
        // 1. 删除 Activiti 中的流程数据（不能级联删除）
        processService.deleteDefinition(bpmnInfo.getProcessDefinitionKey(), bpmnInfo.getVersion());

        // 2. 删除自己的流程信息数据
        return busBpmnInfoMapper.deleteBusBpmnInfoByIds(ids);
    }

    /**
     * 删除流程定义信息
     *
     * @param id 流程定义主键
     * @return 结果
     */
    @Override
    public int deleteBusBpmnInfoById(Long id) {
        return busBpmnInfoMapper.deleteBusBpmnInfoById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deployBpmnInfo(BpmnInfoVo vo) {
        // 利用 activiti 部署流程文件（流程定义Service）
        BusBpmnInfo info = processService.deploy(vo.getBpmnFileName(), vo.getBpmnFileStream());

        // 利用部署后的流程对象，封装 BusBpmnInfo 对象数据
        info.setBpmnType(vo.getBpmnType());
        info.setInfo(vo.getInfo());

        // 保存 bus_bpmn_info 信息
        busBpmnInfoMapper.insertBusBpmnInfo(info);
    }

    @Override
    public InputStream selectBpmnInfoFile(Long id, String type) {
        // 1. 查询 bpmnInfo 对象
        BusBpmnInfo bpmnInfo = this.selectBusBpmnInfoById(id);
        // 2. 由 ProcessService 根据 key、version 查询流程定义资源
        // 3. 最终返回回来
        return processService.getResourceAsStream(bpmnInfo.getProcessDefinitionKey(), bpmnInfo.getVersion(), type);
    }

    @Override
    public BigDecimal getCarPackageAuditDiscountPrice() {
        BusBpmnInfo info = busBpmnInfoMapper.selectLastestBpmnInfoByType(BpmnTypeEnum.CAR_PACKAGE.ordinal());

        String definitionKey = info.getProcessDefinitionKey();
        return ActivitiUtils.getDiscountAmount(processService.getBpmnModel(definitionKey, info.getVersion().intValue()), definitionKey);
    }

    @Override
    public BusBpmnInfo selectLastestBpmnInfoByType(int type) {
        return busBpmnInfoMapper.selectLastestBpmnInfoByType(type);
    }
}
