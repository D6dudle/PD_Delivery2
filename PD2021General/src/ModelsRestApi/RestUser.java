package ModelsRestApi;

public class RestUser {
    private String username;
    private String password;
    private String userIp;
    private String token;

    public RestUser(String username, String password, String userIp) {
        this.username = username;
        this.password = password;
        this.userIp = userIp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
