package net.mnio.springbooter.persistence.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @CreatedDate
    @Temporal(TIMESTAMP)
    private Date created;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Date lastModified;

    @Version
    @Column(nullable = false, columnDefinition = "int default 0")
    private int version;

    public String getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModified() {
        return lastModified;
    }

    @PrePersist
    protected void onCreate() {
        created = new Date();
        lastModified = created;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModified = new Date();
    }

    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final AbstractEntity that = (AbstractEntity) obj;

        if (id != null) {
            return id.equals(that.id);
        } else {
            return that.id == null;
        }
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return 0;
        }
    }
}
