package net.mnio.springbooter.controller.api;

public class UserCreateOrUpdateDto {

    private String email;

    private String password;

    private String name;

    public UserCreateOrUpdateDto(final String email, final String name, final String password) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}