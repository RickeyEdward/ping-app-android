package com.Utils.vpdntestapp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author panqi
 */
public class IpUtils {

  /**
   * 获取本机ip地址
   *
   * @return 本机ip
   */
  public static String getIp() {
    try {
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        en.hasMoreElements(); ) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
          enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
            return inetAddress.getHostAddress();
          }
        }
      }
    } catch (SocketException ex) {
      ex.printStackTrace();
    }
    return null;
  }


}
