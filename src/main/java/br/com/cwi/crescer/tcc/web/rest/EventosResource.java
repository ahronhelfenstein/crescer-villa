package br.com.cwi.crescer.tcc.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.cwi.crescer.tcc.domain.Eventos;
import br.com.cwi.crescer.tcc.repository.EventosRepository;
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
 * REST controller for managing Eventos.
 */
@RestController
@RequestMapping("/api")
public class EventosResource {

    private final Logger log = LoggerFactory.getLogger(EventosResource.class);

    private static final String ENTITY_NAME = "eventos";

    private final EventosRepository eventosRepository;

    public EventosResource(EventosRepository eventosRepository) {
        this.eventosRepository = eventosRepository;
    }

    /**
     * POST  /eventos : Create a new eventos.
     *
     * @param eventos the eventos to create
     * @return the ResponseEntity with status 201 (Created) and with body the new eventos, or with status 400 (Bad Request) if the eventos has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/eventos")
    @Timed
    public ResponseEntity<Eventos> createEventos(@RequestBody Eventos eventos) throws URISyntaxException {
        log.debug("REST request to save Eventos : {}", eventos);
        if (eventos.getId() != null) {
            throw new BadRequestAlertException("A new eventos cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Eventos result = eventosRepository.save(eventos);
        return ResponseEntity.created(new URI("/api/eventos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /eventos : Updates an existing eventos.
     *
     * @param eventos the eventos to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated eventos,
     * or with status 400 (Bad Request) if the eventos is not valid,
     * or with status 500 (Internal Server Error) if the eventos couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/eventos")
    @Timed
    public ResponseEntity<Eventos> updateEventos(@RequestBody Eventos eventos) throws URISyntaxException {
        log.debug("REST request to update Eventos : {}", eventos);
        if (eventos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Eventos result = eventosRepository.save(eventos);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, eventos.getId().toString()))
            .body(result);
    }

    /**
     * GET  /eventos : get all the eventos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of eventos in body
     */
    @GetMapping("/eventos")
    @Timed
    public List<Eventos> getAllEventos() {
        log.debug("REST request to get all Eventos");
        return eventosRepository.findAll();
    }

    /**
     * GET  /eventos/:id : get the "id" eventos.
     *
     * @param id the id of the eventos to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the eventos, or with status 404 (Not Found)
     */
    @GetMapping("/eventos/{id}")
    @Timed
    public ResponseEntity<Eventos> getEventos(@PathVariable Long id) {
        log.debug("REST request to get Eventos : {}", id);
        Optional<Eventos> eventos = eventosRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(eventos);
    }

    /**
     * DELETE  /eventos/:id : delete the "id" eventos.
     *
     * @param id the id of the eventos to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/eventos/{id}")
    @Timed
    public ResponseEntity<Void> deleteEventos(@PathVariable Long id) {
        log.debug("REST request to delete Eventos : {}", id);

        eventosRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
