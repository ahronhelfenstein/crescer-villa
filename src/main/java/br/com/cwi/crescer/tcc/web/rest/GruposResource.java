package br.com.cwi.crescer.tcc.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.cwi.crescer.tcc.domain.Grupos;
import br.com.cwi.crescer.tcc.repository.GruposRepository;
import br.com.cwi.crescer.tcc.web.rest.errors.BadRequestAlertException;
import br.com.cwi.crescer.tcc.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Grupos.
 */
@RestController
@RequestMapping("/api")
public class GruposResource {

    private final Logger log = LoggerFactory.getLogger(GruposResource.class);

    private static final String ENTITY_NAME = "grupos";

    private final GruposRepository gruposRepository;

    public GruposResource(GruposRepository gruposRepository) {
        this.gruposRepository = gruposRepository;
    }

    /**
     * POST  /grupos : Create a new grupos.
     *
     * @param grupos the grupos to create
     * @return the ResponseEntity with status 201 (Created) and with body the new grupos, or with status 400 (Bad Request) if the grupos has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/grupos")
    @Timed
    public ResponseEntity<Grupos> createGrupos(@RequestBody Grupos grupos) throws URISyntaxException {
        log.debug("REST request to save Grupos : {}", grupos);
        if (grupos.getId() != null) {
            throw new BadRequestAlertException("A new grupos cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Grupos result = gruposRepository.save(grupos);
        return ResponseEntity.created(new URI("/api/grupos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /grupos : Updates an existing grupos.
     *
     * @param grupos the grupos to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated grupos,
     * or with status 400 (Bad Request) if the grupos is not valid,
     * or with status 500 (Internal Server Error) if the grupos couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/grupos")
    @Timed
    public ResponseEntity<Grupos> updateGrupos(@RequestBody Grupos grupos) throws URISyntaxException {
        log.debug("REST request to update Grupos : {}", grupos);
        if (grupos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Grupos result = gruposRepository.save(grupos);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, grupos.getId().toString()))
            .body(result);
    }

    /**
     * GET  /grupos : get all the grupos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of grupos in body
     */
    @GetMapping("/grupos")
    @Timed
    public List<Grupos> getAllGrupos() {
        log.debug("REST request to get all Grupos");
        return gruposRepository.findAll();
    }

    /**
     * GET  /grupos/:id : get the "id" grupos.
     *
     * @param id the id of the grupos to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the grupos, or with status 404 (Not Found)
     */
    @GetMapping("/grupos/{id}")
    @Timed
    public ResponseEntity<Grupos> getGrupos(@PathVariable Long id) {
        log.debug("REST request to get Grupos : {}", id);
        Optional<Grupos> grupos = gruposRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(grupos);
    }

    /**
     * DELETE  /grupos/:id : delete the "id" grupos.
     *
     * @param id the id of the grupos to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/grupos/{id}")
    @Timed
    public ResponseEntity<Void> deleteGrupos(@PathVariable Long id) {
        log.debug("REST request to delete Grupos : {}", id);

        gruposRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
