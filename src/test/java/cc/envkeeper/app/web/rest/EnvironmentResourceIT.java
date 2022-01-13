/*
 * Copyright (c) 2022 Eric Thiebaut-George.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cc.envkeeper.app.web.rest;

import cc.envkeeper.app.EnvKeeperApp;
import cc.envkeeper.app.domain.Environment;
import cc.envkeeper.app.domain.EnvironmentGroup;
import cc.envkeeper.app.repository.EnvironmentRepository;
import cc.envkeeper.app.service.EnvironmentService;
import cc.envkeeper.app.service.dto.EnvironmentDTO;
import cc.envkeeper.app.service.mapper.EnvironmentMapper;
import cc.envkeeper.app.service.dto.EnvironmentCriteria;
import cc.envkeeper.app.service.EnvironmentQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EnvironmentResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class EnvironmentResourceIT {

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;
    private static final Integer SMALLER_SORT_ORDER = 1 - 1;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private EnvironmentMapper environmentMapper;

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private EnvironmentQueryService environmentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvironmentMockMvc;

    private Environment environment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Environment createEntity(EntityManager em) {
        Environment environment = new Environment()
            .shortName(DEFAULT_SHORT_NAME)
            .fullName(DEFAULT_FULL_NAME)
            .description(DEFAULT_DESCRIPTION)
            .sortOrder(DEFAULT_SORT_ORDER);
        // Add required entity
        EnvironmentGroup environmentGroup;
        if (TestUtil.findAll(em, EnvironmentGroup.class).isEmpty()) {
            environmentGroup = EnvironmentGroupResourceIT.createEntity(em);
            em.persist(environmentGroup);
            em.flush();
        } else {
            environmentGroup = TestUtil.findAll(em, EnvironmentGroup.class).get(0);
        }
        environment.setEnvironmentGroup(environmentGroup);
        return environment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Environment createUpdatedEntity(EntityManager em) {
        Environment environment = new Environment()
            .shortName(UPDATED_SHORT_NAME)
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER);
        // Add required entity
        EnvironmentGroup environmentGroup;
        if (TestUtil.findAll(em, EnvironmentGroup.class).isEmpty()) {
            environmentGroup = EnvironmentGroupResourceIT.createUpdatedEntity(em);
            em.persist(environmentGroup);
            em.flush();
        } else {
            environmentGroup = TestUtil.findAll(em, EnvironmentGroup.class).get(0);
        }
        environment.setEnvironmentGroup(environmentGroup);
        return environment;
    }

    @BeforeEach
    public void initTest() {
        environment = createEntity(em);
    }

    @Test
    @Transactional
    public void createEnvironment() throws Exception {
        int databaseSizeBeforeCreate = environmentRepository.findAll().size();
        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);
        restEnvironmentMockMvc.perform(post("/api/environments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isCreated());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeCreate + 1);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
        assertThat(testEnvironment.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testEnvironment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEnvironment.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    public void createEnvironmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = environmentRepository.findAll().size();

        // Create the Environment with an existing ID
        environment.setId(1L);
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvironmentMockMvc.perform(post("/api/environments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkShortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentRepository.findAll().size();
        // set the field null
        environment.setShortName(null);

        // Create the Environment, which fails.
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);


        restEnvironmentMockMvc.perform(post("/api/environments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isBadRequest());

        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentRepository.findAll().size();
        // set the field null
        environment.setFullName(null);

        // Create the Environment, which fails.
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);


        restEnvironmentMockMvc.perform(post("/api/environments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isBadRequest());

        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentRepository.findAll().size();
        // set the field null
        environment.setDescription(null);

        // Create the Environment, which fails.
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);


        restEnvironmentMockMvc.perform(post("/api/environments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isBadRequest());

        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEnvironments() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList
        restEnvironmentMockMvc.perform(get("/api/environments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environment.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));
    }

    @Test
    @Transactional
    public void getEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get the environment
        restEnvironmentMockMvc.perform(get("/api/environments/{id}", environment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(environment.getId().intValue()))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER));
    }


    @Test
    @Transactional
    public void getEnvironmentsByIdFiltering() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        Long id = environment.getId();

        defaultEnvironmentShouldBeFound("id.equals=" + id);
        defaultEnvironmentShouldNotBeFound("id.notEquals=" + id);

        defaultEnvironmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEnvironmentShouldNotBeFound("id.greaterThan=" + id);

        defaultEnvironmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEnvironmentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllEnvironmentsByShortNameIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where shortName equals to DEFAULT_SHORT_NAME
        defaultEnvironmentShouldBeFound("shortName.equals=" + DEFAULT_SHORT_NAME);

        // Get all the environmentList where shortName equals to UPDATED_SHORT_NAME
        defaultEnvironmentShouldNotBeFound("shortName.equals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByShortNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where shortName not equals to DEFAULT_SHORT_NAME
        defaultEnvironmentShouldNotBeFound("shortName.notEquals=" + DEFAULT_SHORT_NAME);

        // Get all the environmentList where shortName not equals to UPDATED_SHORT_NAME
        defaultEnvironmentShouldBeFound("shortName.notEquals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByShortNameIsInShouldWork() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where shortName in DEFAULT_SHORT_NAME or UPDATED_SHORT_NAME
        defaultEnvironmentShouldBeFound("shortName.in=" + DEFAULT_SHORT_NAME + "," + UPDATED_SHORT_NAME);

        // Get all the environmentList where shortName equals to UPDATED_SHORT_NAME
        defaultEnvironmentShouldNotBeFound("shortName.in=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByShortNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where shortName is not null
        defaultEnvironmentShouldBeFound("shortName.specified=true");

        // Get all the environmentList where shortName is null
        defaultEnvironmentShouldNotBeFound("shortName.specified=false");
    }
                @Test
    @Transactional
    public void getAllEnvironmentsByShortNameContainsSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where shortName contains DEFAULT_SHORT_NAME
        defaultEnvironmentShouldBeFound("shortName.contains=" + DEFAULT_SHORT_NAME);

        // Get all the environmentList where shortName contains UPDATED_SHORT_NAME
        defaultEnvironmentShouldNotBeFound("shortName.contains=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByShortNameNotContainsSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where shortName does not contain DEFAULT_SHORT_NAME
        defaultEnvironmentShouldNotBeFound("shortName.doesNotContain=" + DEFAULT_SHORT_NAME);

        // Get all the environmentList where shortName does not contain UPDATED_SHORT_NAME
        defaultEnvironmentShouldBeFound("shortName.doesNotContain=" + UPDATED_SHORT_NAME);
    }


    @Test
    @Transactional
    public void getAllEnvironmentsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where fullName equals to DEFAULT_FULL_NAME
        defaultEnvironmentShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the environmentList where fullName equals to UPDATED_FULL_NAME
        defaultEnvironmentShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByFullNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where fullName not equals to DEFAULT_FULL_NAME
        defaultEnvironmentShouldNotBeFound("fullName.notEquals=" + DEFAULT_FULL_NAME);

        // Get all the environmentList where fullName not equals to UPDATED_FULL_NAME
        defaultEnvironmentShouldBeFound("fullName.notEquals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultEnvironmentShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the environmentList where fullName equals to UPDATED_FULL_NAME
        defaultEnvironmentShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where fullName is not null
        defaultEnvironmentShouldBeFound("fullName.specified=true");

        // Get all the environmentList where fullName is null
        defaultEnvironmentShouldNotBeFound("fullName.specified=false");
    }
                @Test
    @Transactional
    public void getAllEnvironmentsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where fullName contains DEFAULT_FULL_NAME
        defaultEnvironmentShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the environmentList where fullName contains UPDATED_FULL_NAME
        defaultEnvironmentShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where fullName does not contain DEFAULT_FULL_NAME
        defaultEnvironmentShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the environmentList where fullName does not contain UPDATED_FULL_NAME
        defaultEnvironmentShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }


    @Test
    @Transactional
    public void getAllEnvironmentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where description equals to DEFAULT_DESCRIPTION
        defaultEnvironmentShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the environmentList where description equals to UPDATED_DESCRIPTION
        defaultEnvironmentShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where description not equals to DEFAULT_DESCRIPTION
        defaultEnvironmentShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the environmentList where description not equals to UPDATED_DESCRIPTION
        defaultEnvironmentShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultEnvironmentShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the environmentList where description equals to UPDATED_DESCRIPTION
        defaultEnvironmentShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where description is not null
        defaultEnvironmentShouldBeFound("description.specified=true");

        // Get all the environmentList where description is null
        defaultEnvironmentShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllEnvironmentsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where description contains DEFAULT_DESCRIPTION
        defaultEnvironmentShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the environmentList where description contains UPDATED_DESCRIPTION
        defaultEnvironmentShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where description does not contain DEFAULT_DESCRIPTION
        defaultEnvironmentShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the environmentList where description does not contain UPDATED_DESCRIPTION
        defaultEnvironmentShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder equals to DEFAULT_SORT_ORDER
        defaultEnvironmentShouldBeFound("sortOrder.equals=" + DEFAULT_SORT_ORDER);

        // Get all the environmentList where sortOrder equals to UPDATED_SORT_ORDER
        defaultEnvironmentShouldNotBeFound("sortOrder.equals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder not equals to DEFAULT_SORT_ORDER
        defaultEnvironmentShouldNotBeFound("sortOrder.notEquals=" + DEFAULT_SORT_ORDER);

        // Get all the environmentList where sortOrder not equals to UPDATED_SORT_ORDER
        defaultEnvironmentShouldBeFound("sortOrder.notEquals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsInShouldWork() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder in DEFAULT_SORT_ORDER or UPDATED_SORT_ORDER
        defaultEnvironmentShouldBeFound("sortOrder.in=" + DEFAULT_SORT_ORDER + "," + UPDATED_SORT_ORDER);

        // Get all the environmentList where sortOrder equals to UPDATED_SORT_ORDER
        defaultEnvironmentShouldNotBeFound("sortOrder.in=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder is not null
        defaultEnvironmentShouldBeFound("sortOrder.specified=true");

        // Get all the environmentList where sortOrder is null
        defaultEnvironmentShouldNotBeFound("sortOrder.specified=false");
    }

    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder is greater than or equal to DEFAULT_SORT_ORDER
        defaultEnvironmentShouldBeFound("sortOrder.greaterThanOrEqual=" + DEFAULT_SORT_ORDER);

        // Get all the environmentList where sortOrder is greater than or equal to UPDATED_SORT_ORDER
        defaultEnvironmentShouldNotBeFound("sortOrder.greaterThanOrEqual=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder is less than or equal to DEFAULT_SORT_ORDER
        defaultEnvironmentShouldBeFound("sortOrder.lessThanOrEqual=" + DEFAULT_SORT_ORDER);

        // Get all the environmentList where sortOrder is less than or equal to SMALLER_SORT_ORDER
        defaultEnvironmentShouldNotBeFound("sortOrder.lessThanOrEqual=" + SMALLER_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder is less than DEFAULT_SORT_ORDER
        defaultEnvironmentShouldNotBeFound("sortOrder.lessThan=" + DEFAULT_SORT_ORDER);

        // Get all the environmentList where sortOrder is less than UPDATED_SORT_ORDER
        defaultEnvironmentShouldBeFound("sortOrder.lessThan=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentsBySortOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList where sortOrder is greater than DEFAULT_SORT_ORDER
        defaultEnvironmentShouldNotBeFound("sortOrder.greaterThan=" + DEFAULT_SORT_ORDER);

        // Get all the environmentList where sortOrder is greater than SMALLER_SORT_ORDER
        defaultEnvironmentShouldBeFound("sortOrder.greaterThan=" + SMALLER_SORT_ORDER);
    }


    @Test
    @Transactional
    public void getAllEnvironmentsByEnvironmentGroupIsEqualToSomething() throws Exception {
        // Get already existing entity
        EnvironmentGroup environmentGroup = environment.getEnvironmentGroup();
        environmentRepository.saveAndFlush(environment);
        Long environmentGroupId = environmentGroup.getId();

        // Get all the environmentList where environmentGroup equals to environmentGroupId
        defaultEnvironmentShouldBeFound("environmentGroupId.equals=" + environmentGroupId);

        // Get all the environmentList where environmentGroup equals to environmentGroupId + 1
        defaultEnvironmentShouldNotBeFound("environmentGroupId.equals=" + (environmentGroupId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnvironmentShouldBeFound(String filter) throws Exception {
        restEnvironmentMockMvc.perform(get("/api/environments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environment.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));

        // Check, that the count call also returns 1
        restEnvironmentMockMvc.perform(get("/api/environments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnvironmentShouldNotBeFound(String filter) throws Exception {
        restEnvironmentMockMvc.perform(get("/api/environments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnvironmentMockMvc.perform(get("/api/environments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingEnvironment() throws Exception {
        // Get the environment
        restEnvironmentMockMvc.perform(get("/api/environments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Update the environment
        Environment updatedEnvironment = environmentRepository.findById(environment.getId()).get();
        // Disconnect from session so that the updates on updatedEnvironment are not directly saved in db
        em.detach(updatedEnvironment);
        updatedEnvironment
            .shortName(UPDATED_SHORT_NAME)
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER);
        EnvironmentDTO environmentDTO = environmentMapper.toDto(updatedEnvironment);

        restEnvironmentMockMvc.perform(put("/api/environments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
        assertThat(testEnvironment.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testEnvironment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEnvironment.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void updateNonExistingEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc.perform(put("/api/environments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeDelete = environmentRepository.findAll().size();

        // Delete the environment
        restEnvironmentMockMvc.perform(delete("/api/environments/{id}", environment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
