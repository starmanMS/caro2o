package cn.ms.car.audit.domain.vo;

/**
 * 处理任务的参数对象
 */
public class HandleAuditParam {

    private Long auditId;
    private boolean passed;
    private String info;

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
