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

    public static void startJmDNS(ServiceListener listener) {
        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(getIpAddress());

            // Register a service
            //ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "smart_dom", 4567, "path=index.html");
            //jmdns.registerService(serviceInfo);

            // Wait a bit
            //Thread.sleep(25000);

            jmdns.addServiceListener("_http._tcp.local.", listener);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static InetAddress getIpAddress() {
        try {
            InetAddress result = null;
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address
                            && intf.getName().contains("wlan")) {
                        String ipAddress = inetAddress.getHostAddress();
                        System.out.println("IP address: " + ipAddress);
                        return inetAddress;
                        //result = inetAddress;
                    }
                }
            }
            //return result;
        } catch (SocketException ex) {
            System.out.println("Socket exception in GetIP Address of Utilities: " + ex.toString());
        }
        return null;
    }
}
