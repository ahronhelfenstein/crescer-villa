package br.com.cwi.crescer.tcc.web.rest;

import br.com.cwi.crescer.tcc.TccApp;

import br.com.cwi.crescer.tcc.domain.Grupos;
import br.com.cwi.crescer.tcc.repository.GruposRepository;
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
import java.util.List;


import static br.com.cwi.crescer.tcc.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the GruposResource REST controller.
 *
 * @see GruposResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TccApp.class)
public class GruposResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_URL_IMAGEM = "AAAAAAAAAA";
    private static final String UPDATED_URL_IMAGEM = "BBBBBBBBBB";

    @Autowired
    private GruposRepository gruposRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restGruposMockMvc;

    private Grupos grupos;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GruposResource gruposResource = new GruposResource(gruposRepository);
        this.restGruposMockMvc = MockMvcBuilders.standaloneSetup(gruposResource)
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
    public static Grupos createEntity(EntityManager em) {
        Grupos grupos = new Grupos()
            .nome(DEFAULT_NOME)
            .urlImagem(DEFAULT_URL_IMAGEM);
        return grupos;
    }

    @Before
    public void initTest() {
        grupos = createEntity(em);
    }

    @Test
    @Transactional
    public void createGrupos() throws Exception {
        int databaseSizeBeforeCreate = gruposRepository.findAll().size();

        // Create the Grupos
        restGruposMockMvc.perform(post("/api/grupos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(grupos)))
            .andExpect(status().isCreated());

        // Validate the Grupos in the database
        List<Grupos> gruposList = gruposRepository.findAll();
        assertThat(gruposList).hasSize(databaseSizeBeforeCreate + 1);
        Grupos testGrupos = gruposList.get(gruposList.size() - 1);
        assertThat(testGrupos.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testGrupos.getUrlImagem()).isEqualTo(DEFAULT_URL_IMAGEM);
    }

    @Test
    @Transactional
    public void createGruposWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = gruposRepository.findAll().size();

        // Create the Grupos with an existing ID
        grupos.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGruposMockMvc.perform(post("/api/grupos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(grupos)))
            .andExpect(status().isBadRequest());

        // Validate the Grupos in the database
        List<Grupos> gruposList = gruposRepository.findAll();
        assertThat(gruposList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllGrupos() throws Exception {
        // Initialize the database
        gruposRepository.saveAndFlush(grupos);

        // Get all the gruposList
        restGruposMockMvc.perform(get("/api/grupos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grupos.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].urlImagem").value(hasItem(DEFAULT_URL_IMAGEM.toString())));
    }
    
    @Test
    @Transactional
    public void getGrupos() throws Exception {
        // Initialize the database
        gruposRepository.saveAndFlush(grupos);

        // Get the grupos
        restGruposMockMvc.perform(get("/api/grupos/{id}", grupos.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(grupos.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.urlImagem").value(DEFAULT_URL_IMAGEM.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGrupos() throws Exception {
        // Get the grupos
        restGruposMockMvc.perform(get("/api/grupos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGrupos() throws Exception {
        // Initialize the database
        gruposRepository.saveAndFlush(grupos);

        int databaseSizeBeforeUpdate = gruposRepository.findAll().size();

        // Update the grupos
        Grupos updatedGrupos = gruposRepository.findById(grupos.getId()).get();
        // Disconnect from session so that the updates on updatedGrupos are not directly saved in db
        em.detach(updatedGrupos);
        updatedGrupos
            .nome(UPDATED_NOME)
            .urlImagem(UPDATED_URL_IMAGEM);

        restGruposMockMvc.perform(put("/api/grupos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGrupos)))
            .andExpect(status().isOk());

        // Validate the Grupos in the database
        List<Grupos> gruposList = gruposRepository.findAll();
        assertThat(gruposList).hasSize(databaseSizeBeforeUpdate);
        Grupos testGrupos = gruposList.get(gruposList.size() - 1);
        assertThat(testGrupos.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testGrupos.getUrlImagem()).isEqualTo(UPDATED_URL_IMAGEM);
    }

    @Test
    @Transactional
    public void updateNonExistingGrupos() throws Exception {
        int databaseSizeBeforeUpdate = gruposRepository.findAll().size();

        // Create the Grupos

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGruposMockMvc.perform(put("/api/grupos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(grupos)))
            .andExpect(status().isBadRequest());

        // Validate the Grupos in the database
        List<Grupos> gruposList = gruposRepository.findAll();
        assertThat(gruposList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGrupos() throws Exception {
        // Initialize the database
        gruposRepository.saveAndFlush(grupos);

        int databaseSizeBeforeDelete = gruposRepository.findAll().size();

        // Get the grupos
        restGruposMockMvc.perform(delete("/api/grupos/{id}", grupos.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Grupos> gruposList = gruposRepository.findAll();
        assertThat(gruposList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Grupos.class);
        Grupos grupos1 = new Grupos();
        grupos1.setId(1L);
        Grupos grupos2 = new Grupos();
        grupos2.setId(grupos1.getId());
        assertThat(grupos1).isEqualTo(grupos2);
        grupos2.setId(2L);
        assertThat(grupos1).isNotEqualTo(grupos2);
        grupos1.setId(null);
        assertThat(grupos1).isNotEqualTo(grupos2);
    }
}
