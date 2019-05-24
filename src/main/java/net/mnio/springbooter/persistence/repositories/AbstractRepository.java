package net.mnio.springbooter.persistence.repositories;

import net.mnio.springbooter.persistence.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbstractRepository<T extends AbstractEntity> extends JpaRepository<T, String> {

}
