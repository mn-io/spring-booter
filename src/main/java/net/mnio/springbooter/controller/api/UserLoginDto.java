package net.mnio.springbooter.controller.api;

public class UserLoginDto {

    private String email;

    private String password;

    public UserLoginDto(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

