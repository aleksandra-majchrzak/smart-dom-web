package pl.uj.edu.ii.smartdom.web;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Mohru on 28.08.2017.
 */
public class JmDNSManager {

    private static JmDNS jmdns;
    private static ServiceListener currentListener;

    public static void startJmDNS(ServiceListener listener) {
        try {
            if (jmdns == null) {
                jmdns = JmDNS.create(getIpAddress());
            }
            jmdns.removeServiceListener("_http._tcp.local.", currentListener);
            jmdns.addServiceListener("_http._tcp.local.", listener);
            currentListener = listener;

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static InetAddress getIpAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address
                            && intf.getName().contains("wlan")) {
                        String ipAddress = inetAddress.getHostAddress();
                        System.out.println("IP address: " + ipAddress);
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("Socket exception in GetIP Address of Utilities: " + ex.toString());
        }
        return null;
    }
}
