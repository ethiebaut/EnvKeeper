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
import cc.envkeeper.app.domain.BuildStepStatistic;
import cc.envkeeper.app.domain.BuildStep;
import cc.envkeeper.app.repository.BuildStepStatisticRepository;
import cc.envkeeper.app.service.BuildStepStatisticService;
import cc.envkeeper.app.service.dto.BuildStepStatisticDTO;
import cc.envkeeper.app.service.mapper.BuildStepStatisticMapper;
import cc.envkeeper.app.service.dto.BuildStepStatisticCriteria;
import cc.envkeeper.app.service.BuildStepStatisticQueryService;

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
 * Integration tests for the {@link BuildStepStatisticResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class BuildStepStatisticResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final Long DEFAULT_VALUE = 1L;
    private static final Long UPDATED_VALUE = 2L;
    private static final Long SMALLER_VALUE = 1L - 1L;

    @Autowired
    private BuildStepStatisticRepository buildStepStatisticRepository;

    @Autowired
    private BuildStepStatisticMapper buildStepStatisticMapper;

    @Autowired
    private BuildStepStatisticService buildStepStatisticService;

    @Autowired
    private BuildStepStatisticQueryService buildStepStatisticQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBuildStepStatisticMockMvc;

    private BuildStepStatistic buildStepStatistic;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BuildStepStatistic createEntity(EntityManager em) {
        BuildStepStatistic buildStepStatistic = new BuildStepStatistic()
            .key(DEFAULT_KEY)
            .value(DEFAULT_VALUE);
        // Add required entity
        BuildStep buildStep;
        if (TestUtil.findAll(em, BuildStep.class).isEmpty()) {
            buildStep = BuildStepResourceIT.createEntity(em);
            em.persist(buildStep);
            em.flush();
        } else {
            buildStep = TestUtil.findAll(em, BuildStep.class).get(0);
        }
        buildStepStatistic.setBuildStep(buildStep);
        return buildStepStatistic;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BuildStepStatistic createUpdatedEntity(EntityManager em) {
        BuildStepStatistic buildStepStatistic = new BuildStepStatistic()
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE);
        // Add required entity
        BuildStep buildStep;
        if (TestUtil.findAll(em, BuildStep.class).isEmpty()) {
            buildStep = BuildStepResourceIT.createUpdatedEntity(em);
            em.persist(buildStep);
            em.flush();
        } else {
            buildStep = TestUtil.findAll(em, BuildStep.class).get(0);
        }
        buildStepStatistic.setBuildStep(buildStep);
        return buildStepStatistic;
    }

    @BeforeEach
    public void initTest() {
        buildStepStatistic = createEntity(em);
    }

    @Test
    @Transactional
    public void createBuildStepStatistic() throws Exception {
        int databaseSizeBeforeCreate = buildStepStatisticRepository.findAll().size();
        // Create the BuildStepStatistic
        BuildStepStatisticDTO buildStepStatisticDTO = buildStepStatisticMapper.toDto(buildStepStatistic);
        restBuildStepStatisticMockMvc.perform(post("/api/build-step-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepStatisticDTO)))
            .andExpect(status().isCreated());

        // Validate the BuildStepStatistic in the database
        List<BuildStepStatistic> buildStepStatisticList = buildStepStatisticRepository.findAll();
        assertThat(buildStepStatisticList).hasSize(databaseSizeBeforeCreate + 1);
        BuildStepStatistic testBuildStepStatistic = buildStepStatisticList.get(buildStepStatisticList.size() - 1);
        assertThat(testBuildStepStatistic.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testBuildStepStatistic.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createBuildStepStatisticWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = buildStepStatisticRepository.findAll().size();

        // Create the BuildStepStatistic with an existing ID
        buildStepStatistic.setId(1L);
        BuildStepStatisticDTO buildStepStatisticDTO = buildStepStatisticMapper.toDto(buildStepStatistic);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBuildStepStatisticMockMvc.perform(post("/api/build-step-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepStatisticDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BuildStepStatistic in the database
        List<BuildStepStatistic> buildStepStatisticList = buildStepStatisticRepository.findAll();
        assertThat(buildStepStatisticList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildStepStatisticRepository.findAll().size();
        // set the field null
        buildStepStatistic.setKey(null);

        // Create the BuildStepStatistic, which fails.
        BuildStepStatisticDTO buildStepStatisticDTO = buildStepStatisticMapper.toDto(buildStepStatistic);


        restBuildStepStatisticMockMvc.perform(post("/api/build-step-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepStatisticDTO)))
            .andExpect(status().isBadRequest());

        List<BuildStepStatistic> buildStepStatisticList = buildStepStatisticRepository.findAll();
        assertThat(buildStepStatisticList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildStepStatisticRepository.findAll().size();
        // set the field null
        buildStepStatistic.setValue(null);

        // Create the BuildStepStatistic, which fails.
        BuildStepStatisticDTO buildStepStatisticDTO = buildStepStatisticMapper.toDto(buildStepStatistic);


        restBuildStepStatisticMockMvc.perform(post("/api/build-step-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepStatisticDTO)))
            .andExpect(status().isBadRequest());

        List<BuildStepStatistic> buildStepStatisticList = buildStepStatisticRepository.findAll();
        assertThat(buildStepStatisticList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatistics() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList
        restBuildStepStatisticMockMvc.perform(get("/api/build-step-statistics?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buildStepStatistic.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }

    @Test
    @Transactional
    public void getBuildStepStatistic() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get the buildStepStatistic
        restBuildStepStatisticMockMvc.perform(get("/api/build-step-statistics/{id}", buildStepStatistic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(buildStepStatistic.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()));
    }


    @Test
    @Transactional
    public void getBuildStepStatisticsByIdFiltering() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        Long id = buildStepStatistic.getId();

        defaultBuildStepStatisticShouldBeFound("id.equals=" + id);
        defaultBuildStepStatisticShouldNotBeFound("id.notEquals=" + id);

        defaultBuildStepStatisticShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBuildStepStatisticShouldNotBeFound("id.greaterThan=" + id);

        defaultBuildStepStatisticShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBuildStepStatisticShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBuildStepStatisticsByKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where key equals to DEFAULT_KEY
        defaultBuildStepStatisticShouldBeFound("key.equals=" + DEFAULT_KEY);

        // Get all the buildStepStatisticList where key equals to UPDATED_KEY
        defaultBuildStepStatisticShouldNotBeFound("key.equals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where key not equals to DEFAULT_KEY
        defaultBuildStepStatisticShouldNotBeFound("key.notEquals=" + DEFAULT_KEY);

        // Get all the buildStepStatisticList where key not equals to UPDATED_KEY
        defaultBuildStepStatisticShouldBeFound("key.notEquals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByKeyIsInShouldWork() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where key in DEFAULT_KEY or UPDATED_KEY
        defaultBuildStepStatisticShouldBeFound("key.in=" + DEFAULT_KEY + "," + UPDATED_KEY);

        // Get all the buildStepStatisticList where key equals to UPDATED_KEY
        defaultBuildStepStatisticShouldNotBeFound("key.in=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where key is not null
        defaultBuildStepStatisticShouldBeFound("key.specified=true");

        // Get all the buildStepStatisticList where key is null
        defaultBuildStepStatisticShouldNotBeFound("key.specified=false");
    }
                @Test
    @Transactional
    public void getAllBuildStepStatisticsByKeyContainsSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where key contains DEFAULT_KEY
        defaultBuildStepStatisticShouldBeFound("key.contains=" + DEFAULT_KEY);

        // Get all the buildStepStatisticList where key contains UPDATED_KEY
        defaultBuildStepStatisticShouldNotBeFound("key.contains=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByKeyNotContainsSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where key does not contain DEFAULT_KEY
        defaultBuildStepStatisticShouldNotBeFound("key.doesNotContain=" + DEFAULT_KEY);

        // Get all the buildStepStatisticList where key does not contain UPDATED_KEY
        defaultBuildStepStatisticShouldBeFound("key.doesNotContain=" + UPDATED_KEY);
    }


    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value equals to DEFAULT_VALUE
        defaultBuildStepStatisticShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the buildStepStatisticList where value equals to UPDATED_VALUE
        defaultBuildStepStatisticShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value not equals to DEFAULT_VALUE
        defaultBuildStepStatisticShouldNotBeFound("value.notEquals=" + DEFAULT_VALUE);

        // Get all the buildStepStatisticList where value not equals to UPDATED_VALUE
        defaultBuildStepStatisticShouldBeFound("value.notEquals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultBuildStepStatisticShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the buildStepStatisticList where value equals to UPDATED_VALUE
        defaultBuildStepStatisticShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value is not null
        defaultBuildStepStatisticShouldBeFound("value.specified=true");

        // Get all the buildStepStatisticList where value is null
        defaultBuildStepStatisticShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value is greater than or equal to DEFAULT_VALUE
        defaultBuildStepStatisticShouldBeFound("value.greaterThanOrEqual=" + DEFAULT_VALUE);

        // Get all the buildStepStatisticList where value is greater than or equal to UPDATED_VALUE
        defaultBuildStepStatisticShouldNotBeFound("value.greaterThanOrEqual=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value is less than or equal to DEFAULT_VALUE
        defaultBuildStepStatisticShouldBeFound("value.lessThanOrEqual=" + DEFAULT_VALUE);

        // Get all the buildStepStatisticList where value is less than or equal to SMALLER_VALUE
        defaultBuildStepStatisticShouldNotBeFound("value.lessThanOrEqual=" + SMALLER_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsLessThanSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value is less than DEFAULT_VALUE
        defaultBuildStepStatisticShouldNotBeFound("value.lessThan=" + DEFAULT_VALUE);

        // Get all the buildStepStatisticList where value is less than UPDATED_VALUE
        defaultBuildStepStatisticShouldBeFound("value.lessThan=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStepStatisticsByValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        // Get all the buildStepStatisticList where value is greater than DEFAULT_VALUE
        defaultBuildStepStatisticShouldNotBeFound("value.greaterThan=" + DEFAULT_VALUE);

        // Get all the buildStepStatisticList where value is greater than SMALLER_VALUE
        defaultBuildStepStatisticShouldBeFound("value.greaterThan=" + SMALLER_VALUE);
    }


    @Test
    @Transactional
    public void getAllBuildStepStatisticsByBuildStepIsEqualToSomething() throws Exception {
        // Get already existing entity
        BuildStep buildStep = buildStepStatistic.getBuildStep();
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);
        Long buildStepId = buildStep.getId();

        // Get all the buildStepStatisticList where buildStep equals to buildStepId
        defaultBuildStepStatisticShouldBeFound("buildStepId.equals=" + buildStepId);

        // Get all the buildStepStatisticList where buildStep equals to buildStepId + 1
        defaultBuildStepStatisticShouldNotBeFound("buildStepId.equals=" + (buildStepId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBuildStepStatisticShouldBeFound(String filter) throws Exception {
        restBuildStepStatisticMockMvc.perform(get("/api/build-step-statistics?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buildStepStatistic.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));

        // Check, that the count call also returns 1
        restBuildStepStatisticMockMvc.perform(get("/api/build-step-statistics/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBuildStepStatisticShouldNotBeFound(String filter) throws Exception {
        restBuildStepStatisticMockMvc.perform(get("/api/build-step-statistics?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBuildStepStatisticMockMvc.perform(get("/api/build-step-statistics/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBuildStepStatistic() throws Exception {
        // Get the buildStepStatistic
        restBuildStepStatisticMockMvc.perform(get("/api/build-step-statistics/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBuildStepStatistic() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        int databaseSizeBeforeUpdate = buildStepStatisticRepository.findAll().size();

        // Update the buildStepStatistic
        BuildStepStatistic updatedBuildStepStatistic = buildStepStatisticRepository.findById(buildStepStatistic.getId()).get();
        // Disconnect from session so that the updates on updatedBuildStepStatistic are not directly saved in db
        em.detach(updatedBuildStepStatistic);
        updatedBuildStepStatistic
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE);
        BuildStepStatisticDTO buildStepStatisticDTO = buildStepStatisticMapper.toDto(updatedBuildStepStatistic);

        restBuildStepStatisticMockMvc.perform(put("/api/build-step-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepStatisticDTO)))
            .andExpect(status().isOk());

        // Validate the BuildStepStatistic in the database
        List<BuildStepStatistic> buildStepStatisticList = buildStepStatisticRepository.findAll();
        assertThat(buildStepStatisticList).hasSize(databaseSizeBeforeUpdate);
        BuildStepStatistic testBuildStepStatistic = buildStepStatisticList.get(buildStepStatisticList.size() - 1);
        assertThat(testBuildStepStatistic.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testBuildStepStatistic.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void updateNonExistingBuildStepStatistic() throws Exception {
        int databaseSizeBeforeUpdate = buildStepStatisticRepository.findAll().size();

        // Create the BuildStepStatistic
        BuildStepStatisticDTO buildStepStatisticDTO = buildStepStatisticMapper.toDto(buildStepStatistic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildStepStatisticMockMvc.perform(put("/api/build-step-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepStatisticDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BuildStepStatistic in the database
        List<BuildStepStatistic> buildStepStatisticList = buildStepStatisticRepository.findAll();
        assertThat(buildStepStatisticList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBuildStepStatistic() throws Exception {
        // Initialize the database
        buildStepStatisticRepository.saveAndFlush(buildStepStatistic);

        int databaseSizeBeforeDelete = buildStepStatisticRepository.findAll().size();

        // Delete the buildStepStatistic
        restBuildStepStatisticMockMvc.perform(delete("/api/build-step-statistics/{id}", buildStepStatistic.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BuildStepStatistic> buildStepStatisticList = buildStepStatisticRepository.findAll();
        assertThat(buildStepStatisticList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
