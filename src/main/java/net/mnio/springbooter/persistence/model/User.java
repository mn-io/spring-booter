package net.mnio.springbooter.persistence.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
})
public class User extends AbstractEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = StringUtils.lowerCase(StringUtils.strip(email));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password too short");
        }
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
