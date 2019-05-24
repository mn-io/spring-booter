package net.mnio.springbooter.persistence.model;

import net.mnio.springbooter.util.security.RandomUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class UserSession extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String token;

    @OneToOne
    private User user;

    public String getToken() {
        return token;
    }

    public void generateToken() {
        this.token = RandomUtil.generateToken();
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
