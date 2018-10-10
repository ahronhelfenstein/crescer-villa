package br.com.cwi.crescer.tcc.repository;

import br.com.cwi.crescer.tcc.domain.Grupos;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Grupos entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GruposRepository extends JpaRepository<Grupos, Long> {

}
