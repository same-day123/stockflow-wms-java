package org.jeecg.modules.wms.shunfeng.other;

/**
 * 快递公司
 *
 * @author lx
 * @date 2020年3月6日15:52:41
 */
public enum ExpressCompanyType {

    paotui("同城快递"),
    shunfeng("顺丰快递");

    ExpressCompanyType(String chName) {
        this.chName = chName;
    }

    /**
     * 中文名称.
     */
    private String chName;

    /**
     * Gets the 中文名称.
     *
     * @return the 中文名称
     */
    public String getChName() {
        return chName;
    }
}
