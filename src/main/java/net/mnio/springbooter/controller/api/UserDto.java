package net.mnio.springbooter.controller.api;

import net.mnio.springbooter.persistence.model.User;

import java.util.Date;

public class UserDto {

    private String id;

    private String name;

    private String email;

    private Date created;

    public static UserDto build(final User user) {
        final UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.email = user.getEmail();
        dto.name = user.getName();
        dto.created = user.getCreated();
        return dto;
    }

    public Date getCreated() {
        return created;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
