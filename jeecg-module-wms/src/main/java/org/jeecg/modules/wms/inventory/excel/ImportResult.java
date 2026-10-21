package org.jeecg.modules.wms.inventory.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {
    /** 成功条数 */
    private int successCount;
    /** 失败条数 */
    private int failCount;
    /** 跳过条数 */
    private int skipCount;
    /** 失败明细：第X行：原因 */
    private List<String> failMessages = new ArrayList<>();

    /** 添加成功条数 */
    public void addSuccess() {
        this.successCount++;
    }

    /** 添加失败条数及明细 */
    public void addFail(Integer rowNum, String reason) {
        this.failCount++;
        this.failMessages.add(rowNum == null ? reason : "第" + rowNum + "行：" + reason);
    }

    /** 添加跳过条数 */
    public void addSkip() {
        this.skipCount++;
    }

    /** 合并另一批的结果（Listener 分批调用 Service 时用） */
    public void merge(ImportResult other) {
        if (other == null) {
            return;
        }
        this.successCount += other.successCount;
        this.skipCount += other.skipCount;
        this.failCount += other.failCount;
        this.failMessages.addAll(other.failMessages);
    }

    /** 总条数 */
    public int getTotal() {
        return successCount + skipCount + failCount;
    }

    /** 拼给前端看的提示文案 */
    public String toMessage() {
        if (getTotal() == 0) {
            return "文件中没有可导入的数据";
        }
        StringBuilder sb = new StringBuilder("导入完成：成功 ").append(successCount).append(" 条");
        if (skipCount > 0) {
            sb.append("，跳过（已存在）").append(skipCount).append(" 条");
        }
        if (failCount > 0) {
            sb.append("，失败 ").append(failCount).append(" 条");
        }
        return sb.toString();
    }
}