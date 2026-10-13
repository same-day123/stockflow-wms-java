package org.jeecg.modules.iot.simulate.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author muht
 * @date 2025/4/19 18:33
 */
public class IpUtils {

    /**
     * 获取本地IP地址
     *
     * @return
     */
    public static List<String> getLocalIPs() {
        List<String> ipList = new ArrayList<>();
        try {
            // 遍历所有网络接口
            for (NetworkInterface networkInterface :
                    Collections.list(NetworkInterface.getNetworkInterfaces())) {
                // 过滤未启用或虚拟接口
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                // 遍历接口的所有 IP 地址
                for (InetAddress inetAddress :
                        Collections.list(networkInterface.getInetAddresses())) {
                    // 筛选 IPv4 地址，排除本地链接地址（169.254.x.x）
                    if (inetAddress instanceof Inet4Address &&
                            !inetAddress.isLinkLocalAddress()) {
                        ipList.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipList;
    }
}
