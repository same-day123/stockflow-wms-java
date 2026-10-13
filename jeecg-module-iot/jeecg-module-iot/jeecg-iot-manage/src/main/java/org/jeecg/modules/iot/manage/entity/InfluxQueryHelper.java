package org.jeecg.modules.iot.manage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jeecg.modules.iot.manage.constant.CommonConst;
import org.jeecg.modules.iot.manage.constant.SeriesDataStartEnum;

import javax.annotation.Nullable;

/**
 * @author muht
 * @date 2025/5/1 22:24
 */
@Data
@AllArgsConstructor
public class InfluxQueryHelper {
    /**
     * 数据桶
     */
    private String bucket ;
    /**
     * 开始时间
     */
    private String start = SeriesDataStartEnum.ONE_DAY.getVal();
    /**
     * 结束时间
     */
    private String stop = "now()";
    /**
     * 测量
     */
    private String measurement;
    /**
     * 标签，即设备code
     */
    private String tag;
    /**
     * 指标，即上报数据
     */
    private @Nullable String field;

    /**
     * 快捷构造器
     */
    public InfluxQueryHelper(String bucket,String productCode, String deviceCode, String modelCode) {
        this.bucket = bucket;
        this.measurement = productCode;
        this.tag = deviceCode;
        this.field = modelCode;
    }

    /**
     * 拼接 flux 查询语句
     *
     * @return
     */
    public String getFlux() {
        if (this.tag == null) {
            throw new IllegalArgumentException("InfluxQueryHelper tag is null");
        }

        String flux = """
                from(bucket: "%s")
                    |> range(start: %s, stop: %s)
                    |> filter(fn: (r) => r["_measurement"] == "%s")
                    |> filter(fn: (r) => r["deviceCode"] == "%s")
                """;

        if (field == null) {
            return String.format(flux, bucket, start, stop, measurement, tag);
        } else {
            flux = flux.concat("|> filter(fn: (r) => r[\"_field\"] == \"%s\")");
            return String.format(flux, bucket, start, stop, measurement, tag, field);
        }
    }

    /**
     * 获取分页flux
     *
     * @param pageSize
     * @param pageNo
     * @return
     */
    public String getPageFlux(Integer pageNo, Integer pageSize) {
        String flux = getFlux();
        flux.concat("|> limit(n: " + pageSize + ", offset: " + pageSize * (pageNo - 1) + ")");
        return flux;
    }
}
