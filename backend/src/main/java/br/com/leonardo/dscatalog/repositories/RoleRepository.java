package br.com.leonardo.dscatalog.repositories;

import br.com.leonardo.dscatalog.entities.Product;
import br.com.leonardo.dscatalog.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
