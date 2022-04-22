package io.terminus.debugger.client.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


// 主要拷贝自 trantor-framework debugKeyProvider
public class MacAddressUtil {


    /**
     * 获取本机的 mac 地址
     */
    public static String getLocalMacAddress() {
        return getMacAddress(getLocalAddress());
    }

    /**
     * 获取MAC地址
     *
     * @param address 本机IP
     * @return MAC地址
     */
    public static String getMacAddress(InetAddress address) {
        try {
            byte[] bytes = NetworkInterface.getByInetAddress(address).getHardwareAddress();
            if (bytes != null && bytes.length == 6) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(Integer.toHexString((b & 240) >> 4));
                    sb.append(Integer.toHexString(b & 15));
                    sb.append("-");
                }
                sb.deleteCharAt(sb.length() - 1);
                return sb.toString().toUpperCase();
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    /**
     * 获取本机IP
     *
     * @return ip
     */
    public static InetAddress getLocalAddress() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces(); faces.hasMoreElements(); ) {
                NetworkInterface face = faces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration<InetAddress> inetAddrList = face.getInetAddresses(); inetAddrList.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrList.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        // 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了,前提可以找到硬件地址
                            if (NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress() != null) {
                                return inetAddr;
                            }
                        } else if (candidateAddress == null) {
                            // 本机地址一定不会是ipv6
                            if (!inetAddr.getHostAddress().contains(":")) {
                                // site-local类型的地址未被发现，先记录候选地址,前提可以找到硬件地址
                                if (NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress() != null) {
                                    candidateAddress = inetAddr;
                                }
                            }
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            return null;
        }
    }
}
