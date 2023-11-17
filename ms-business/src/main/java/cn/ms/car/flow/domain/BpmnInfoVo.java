package cn.ms.car.flow.domain;

import cn.ms.car.common.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class BpmnInfoVo implements Serializable {

    @NotNull(message = "请选择流程类型")
    private Integer bpmnType;
    @NotNull(message = "请输入部署信息")
    private String info;
    @NotNull(message = "请选择流程文件")
    private MultipartFile bpmnFile;

    public Integer getBpmnType() {
        return bpmnType;
    }

    public void setBpmnType(Integer bpmnType) {
        this.bpmnType = bpmnType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public MultipartFile getBpmnFile() {
        return bpmnFile;
    }

    public void setBpmnFile(MultipartFile bpmnFile) {
        this.bpmnFile = bpmnFile;
    }

    public String getBpmnFileName() {
        return bpmnFile.getOriginalFilename();
    }

    public InputStream getBpmnFileStream() {
        try {
            return bpmnFile.getInputStream();
        } catch (IOException e) {
            throw new ServiceException("获取流程文件输入流失败：" + e.getMessage());
        }
    }
}
