package pl.uj.edu.ii.smartdom.web.serverEntities;

/**
 * Created by Mohru on 15.07.2017.
 */
public class UserEntity {
    String login;
    String password;

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}