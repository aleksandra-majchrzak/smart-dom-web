package pl.uj.edu.ii.smartdom.web.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Mohru on 03.09.2017.
 */
public class StringUtils {

    public static String getHashString(String basicString) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(basicString.getBytes());
            return new String(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return basicString;
    }
}
