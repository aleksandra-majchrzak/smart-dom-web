package serverEntities;

/**
 * Created by Mohru on 15.07.2017.
 */
public class LoginResponse {
    public String userLogin;
    public String userToken;

    public LoginResponse(String userLogin, String userToken) {
        this.userLogin = userLogin;
        this.userToken = userToken;
    }
}
