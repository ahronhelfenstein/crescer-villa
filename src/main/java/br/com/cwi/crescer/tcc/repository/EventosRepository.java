package br.com.cwi.crescer.tcc.repository;

import br.com.cwi.crescer.tcc.domain.Eventos;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Eventos entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventosRepository extends JpaRepository<Eventos, Long> {

}
