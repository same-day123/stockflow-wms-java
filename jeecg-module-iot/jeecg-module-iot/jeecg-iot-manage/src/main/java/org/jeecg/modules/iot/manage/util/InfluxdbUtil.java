package org.jeecg.modules.iot.manage.util;

/**
 * @author muht
 * @date 2025/5/1 21:27
 */
public class InfluxdbUtil {

    /**
     * 构造flux 查询语句
     *
     * @param bucket
     * @param start
     * @param measurement
     * @param tag
     * @param field
     * @return
     */
    public static String getFlux(String bucket,
                                 String start,
                                 String measurement,
                                 String tag,
                                 String field) {
        String flux = """
                from(bucket: %s)
                    |> range(start: %s)
                    |> filter(fn: (r) => r["_measurement"] == %s)
                    |> filter(fn: (r) => r["deviceCode"] == %s)
                    |> filter(fn: (r) => r["_field"] == %s)
                """;
        String formatFlux = String.format(flux, bucket, start, measurement, tag, field);
        return formatFlux;
    }
}
