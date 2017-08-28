import controllers.ModulesController;
import controllers.RoomsController;
import controllers.SmartDomController;
import controllers.UserController;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static spark.Spark.*;

/**
 * Created by Mohru on 15.07.2017.
 */
public class WebApp {

    public static void initialize() {

        secure(Constants.KEYSTORE_PATH, Constants.KEYSTORE_PASS, null, null);

        // default port 4567
        staticFileLocation("/public");

        //***************  MAIN  ***************
        get("/", (request, response) ->
                render(SmartDomController.getMain(request, response)));

        get("/menu", (request, response) ->
                render(SmartDomController.getMenu(request, response)));

        get("/register", (request, response) ->
                render(SmartDomController.getRegister(request, response)));


        //***************  USER  ***************
        post("/register", (request, response) ->
                render(UserController.register(request, response)));

        post("/signIn", (request, response) ->
                render(UserController.signIn(request, response)));

        get("/logOut", (request, response) ->
                render(UserController.logOut(request, response)));


        //***************  ROOMS  ***************
        get("/rooms", (request, response) ->
                render(RoomsController.getRooms(request, response)));

        post("/rooms", (request, response) ->
                render(RoomsController.saveRoom(request, response)));

        get("/rooms/new", (request, response) ->
                render(RoomsController.newRoom(request, response)));

        get("/rooms/:id", (request, response) ->
                render(RoomsController.getRoom(request, response)));

        get("/rooms/:id/edit", (request, response) ->
                render(RoomsController.editRoom(request, response)));

        post("/rooms/:id/edit/users", (request, response) ->
                render(RoomsController.addUser(request, response)));

        post("/rooms/:id/edit/users/delete", (request, response) ->
                render(RoomsController.removeUser(request, response)));


        //***************  MODULES  ***************
        get("/modules", (request, response) ->
                render(ModulesController.getModules(request, response)));

        post("/modules", (request, response) ->
                render(ModulesController.saveModule(request, response)));

        get("/modules/new", (request, response) ->
                render(ModulesController.newModule(request, response)));

        get("/modules/:id", (request, response) ->
                render(ModulesController.getModule(request, response)));

        startJmDNS();
    }

    private static String render(ModelAndView modelAndView) {
        return new VelocityTemplateEngine().render(modelAndView);
    }

    private static void startJmDNS() {
        try {
            // Create a JmDNS instance
            //JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
            JmDNS jmdns = JmDNS.create(getIpAddress());

            // Register a service
            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "smart_dom", 4567, "path=index.html");
            jmdns.registerService(serviceInfo);

            // Wait a bit
            //Thread.sleep(25000);

            jmdns.addServiceListener("_http._tcp.local.", new SampleListener());

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
                        //return inetAddress;
                        result = inetAddress;
                    }
                }
            }
            return result;
        } catch (SocketException ex) {
            System.out.println("Socket exception in GetIP Address of Utilities: " + ex.toString());
        }
        return null;
    }


    private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    }

}
