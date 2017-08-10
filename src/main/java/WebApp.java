import controllers.ModulesController;
import controllers.RoomsController;
import controllers.UserController;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by Mohru on 15.07.2017.
 */
public class WebApp {

    public static void initialize() {

        secure(Constants.KEYSTORE_PATH, Constants.KEYSTORE_PASS, null, null);

        // default port 4567
        staticFileLocation("/public");

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "/public/index.vm");
        }, new VelocityTemplateEngine());


        //***************  USER  ***************
        get("/register", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "/public/register.vm");
        }, new VelocityTemplateEngine());

        post("/register", UserController::register,
                new VelocityTemplateEngine());

        post("/signIn", UserController::signIn,
                new VelocityTemplateEngine());

        get("/logOut", UserController::logOut);


        //***************  ROOMS  ***************
        get("/rooms", RoomsController::getRooms,
                new VelocityTemplateEngine());

        post("/rooms", RoomsController::saveRoom,
                new VelocityTemplateEngine());

        get("/rooms/new", RoomsController::newRoom,
                new VelocityTemplateEngine());

        get("/rooms/:id", RoomsController::getRoom,
                new VelocityTemplateEngine());

        get("/rooms/:id/edit", RoomsController::editRoom,
                new VelocityTemplateEngine());

        post("/rooms/:id/edit/users", RoomsController::addUser,
                new VelocityTemplateEngine());

        post("/rooms/:id/edit/users/delete", RoomsController::removeUser,
                new VelocityTemplateEngine());


        //***************  MODULES  ***************
        get("/modules", ModulesController::getModules,
                new VelocityTemplateEngine());

        post("/modules", ModulesController::saveModule,
                new VelocityTemplateEngine());

        get("/modules/new", ModulesController::newModule,
                new VelocityTemplateEngine());

        get("/modules/:id", ModulesController::getModule,
                new VelocityTemplateEngine());
    }

    private TrustManager initTrustManager() {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");   // jedyny dosteny typ into: https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html
            InputStream certInput = new BufferedInputStream(new FileInputStream(Constants.CERTIFICATE_PATH));
            Certificate cert = factory.generateCertificate(certInput);

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", cert);

            String trustManagerAlg = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(trustManagerAlg);
            trustFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustFactory.getTrustManagers(), null);

            TrustManager[] trustManagers = trustFactory.getTrustManagers();
            if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager))
                return trustManagers[0];

        } catch (CertificateException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
