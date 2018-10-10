package br.com.cwi.crescer.tcc.web.rest;

import br.com.cwi.crescer.tcc.TccApp;

import br.com.cwi.crescer.tcc.domain.Eventos;
import br.com.cwi.crescer.tcc.repository.EventosRepository;
import br.com.cwi.crescer.tcc.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static br.com.cwi.crescer.tcc.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EventosResource REST controller.
 *
 * @see EventosResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TccApp.class)
public class EventosResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_URL_IMAGEM = "AAAAAAAAAA";
    private static final String UPDATED_URL_IMAGEM = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private EventosRepository eventosRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restEventosMockMvc;

    private Eventos eventos;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EventosResource eventosResource = new EventosResource(eventosRepository);
        this.restEventosMockMvc = MockMvcBuilders.standaloneSetup(eventosResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eventos createEntity(EntityManager em) {
        Eventos eventos = new Eventos()
            .nome(DEFAULT_NOME)
            .urlImagem(DEFAULT_URL_IMAGEM)
            .data(DEFAULT_DATA);
        return eventos;
    }

    @Before
    public void initTest() {
        eventos = createEntity(em);
    }

    @Test
    @Transactional
    public void createEventos() throws Exception {
        int databaseSizeBeforeCreate = eventosRepository.findAll().size();

        // Create the Eventos
        restEventosMockMvc.perform(post("/api/eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventos)))
            .andExpect(status().isCreated());

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll();
        assertThat(eventosList).hasSize(databaseSizeBeforeCreate + 1);
        Eventos testEventos = eventosList.get(eventosList.size() - 1);
        assertThat(testEventos.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testEventos.getUrlImagem()).isEqualTo(DEFAULT_URL_IMAGEM);
        assertThat(testEventos.getData()).isEqualTo(DEFAULT_DATA);
    }

    @Test
    @Transactional
    public void createEventosWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventosRepository.findAll().size();

        // Create the Eventos with an existing ID
        eventos.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventosMockMvc.perform(post("/api/eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventos)))
            .andExpect(status().isBadRequest());

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll();
        assertThat(eventosList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList
        restEventosMockMvc.perform(get("/api/eventos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventos.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].urlImagem").value(hasItem(DEFAULT_URL_IMAGEM.toString())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())));
    }
    
    @Test
    @Transactional
    public void getEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get the eventos
        restEventosMockMvc.perform(get("/api/eventos/{id}", eventos.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventos.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.urlImagem").value(DEFAULT_URL_IMAGEM.toString()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEventos() throws Exception {
        // Get the eventos
        restEventosMockMvc.perform(get("/api/eventos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        int databaseSizeBeforeUpdate = eventosRepository.findAll().size();

        // Update the eventos
        Eventos updatedEventos = eventosRepository.findById(eventos.getId()).get();
        // Disconnect from session so that the updates on updatedEventos are not directly saved in db
        em.detach(updatedEventos);
        updatedEventos
            .nome(UPDATED_NOME)
            .urlImagem(UPDATED_URL_IMAGEM)
            .data(UPDATED_DATA);

        restEventosMockMvc.perform(put("/api/eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEventos)))
            .andExpect(status().isOk());

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
        Eventos testEventos = eventosList.get(eventosList.size() - 1);
        assertThat(testEventos.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testEventos.getUrlImagem()).isEqualTo(UPDATED_URL_IMAGEM);
        assertThat(testEventos.getData()).isEqualTo(UPDATED_DATA);
    }

    @Test
    @Transactional
    public void updateNonExistingEventos() throws Exception {
        int databaseSizeBeforeUpdate = eventosRepository.findAll().size();

        // Create the Eventos

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventosMockMvc.perform(put("/api/eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventos)))
            .andExpect(status().isBadRequest());

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        int databaseSizeBeforeDelete = eventosRepository.findAll().size();

        // Get the eventos
        restEventosMockMvc.perform(delete("/api/eventos/{id}", eventos.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Eventos> eventosList = eventosRepository.findAll();
        assertThat(eventosList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Eventos.class);
        Eventos eventos1 = new Eventos();
        eventos1.setId(1L);
        Eventos eventos2 = new Eventos();
        eventos2.setId(eventos1.getId());
        assertThat(eventos1).isEqualTo(eventos2);
        eventos2.setId(2L);
        assertThat(eventos1).isNotEqualTo(eventos2);
        eventos1.setId(null);
        assertThat(eventos1).isNotEqualTo(eventos2);
    }
}
