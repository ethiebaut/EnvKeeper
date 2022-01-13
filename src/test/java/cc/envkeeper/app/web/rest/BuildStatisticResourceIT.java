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
import cc.envkeeper.app.domain.BuildStatistic;
import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.repository.BuildStatisticRepository;
import cc.envkeeper.app.service.BuildStatisticService;
import cc.envkeeper.app.service.dto.BuildStatisticDTO;
import cc.envkeeper.app.service.mapper.BuildStatisticMapper;
import cc.envkeeper.app.service.dto.BuildStatisticCriteria;
import cc.envkeeper.app.service.BuildStatisticQueryService;

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
 * Integration tests for the {@link BuildStatisticResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class BuildStatisticResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final Long DEFAULT_VALUE = 1L;
    private static final Long UPDATED_VALUE = 2L;
    private static final Long SMALLER_VALUE = 1L - 1L;

    @Autowired
    private BuildStatisticRepository buildStatisticRepository;

    @Autowired
    private BuildStatisticMapper buildStatisticMapper;

    @Autowired
    private BuildStatisticService buildStatisticService;

    @Autowired
    private BuildStatisticQueryService buildStatisticQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBuildStatisticMockMvc;

    private BuildStatistic buildStatistic;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BuildStatistic createEntity(EntityManager em) {
        BuildStatistic buildStatistic = new BuildStatistic()
            .key(DEFAULT_KEY)
            .value(DEFAULT_VALUE);
        // Add required entity
        Build build;
        if (TestUtil.findAll(em, Build.class).isEmpty()) {
            build = BuildResourceIT.createEntity(em);
            em.persist(build);
            em.flush();
        } else {
            build = TestUtil.findAll(em, Build.class).get(0);
        }
        buildStatistic.setBuild(build);
        return buildStatistic;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BuildStatistic createUpdatedEntity(EntityManager em) {
        BuildStatistic buildStatistic = new BuildStatistic()
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE);
        // Add required entity
        Build build;
        if (TestUtil.findAll(em, Build.class).isEmpty()) {
            build = BuildResourceIT.createUpdatedEntity(em);
            em.persist(build);
            em.flush();
        } else {
            build = TestUtil.findAll(em, Build.class).get(0);
        }
        buildStatistic.setBuild(build);
        return buildStatistic;
    }

    @BeforeEach
    public void initTest() {
        buildStatistic = createEntity(em);
    }

    @Test
    @Transactional
    public void createBuildStatistic() throws Exception {
        int databaseSizeBeforeCreate = buildStatisticRepository.findAll().size();
        // Create the BuildStatistic
        BuildStatisticDTO buildStatisticDTO = buildStatisticMapper.toDto(buildStatistic);
        restBuildStatisticMockMvc.perform(post("/api/build-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStatisticDTO)))
            .andExpect(status().isCreated());

        // Validate the BuildStatistic in the database
        List<BuildStatistic> buildStatisticList = buildStatisticRepository.findAll();
        assertThat(buildStatisticList).hasSize(databaseSizeBeforeCreate + 1);
        BuildStatistic testBuildStatistic = buildStatisticList.get(buildStatisticList.size() - 1);
        assertThat(testBuildStatistic.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testBuildStatistic.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createBuildStatisticWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = buildStatisticRepository.findAll().size();

        // Create the BuildStatistic with an existing ID
        buildStatistic.setId(1L);
        BuildStatisticDTO buildStatisticDTO = buildStatisticMapper.toDto(buildStatistic);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBuildStatisticMockMvc.perform(post("/api/build-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStatisticDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BuildStatistic in the database
        List<BuildStatistic> buildStatisticList = buildStatisticRepository.findAll();
        assertThat(buildStatisticList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildStatisticRepository.findAll().size();
        // set the field null
        buildStatistic.setKey(null);

        // Create the BuildStatistic, which fails.
        BuildStatisticDTO buildStatisticDTO = buildStatisticMapper.toDto(buildStatistic);


        restBuildStatisticMockMvc.perform(post("/api/build-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStatisticDTO)))
            .andExpect(status().isBadRequest());

        List<BuildStatistic> buildStatisticList = buildStatisticRepository.findAll();
        assertThat(buildStatisticList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildStatisticRepository.findAll().size();
        // set the field null
        buildStatistic.setValue(null);

        // Create the BuildStatistic, which fails.
        BuildStatisticDTO buildStatisticDTO = buildStatisticMapper.toDto(buildStatistic);


        restBuildStatisticMockMvc.perform(post("/api/build-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStatisticDTO)))
            .andExpect(status().isBadRequest());

        List<BuildStatistic> buildStatisticList = buildStatisticRepository.findAll();
        assertThat(buildStatisticList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBuildStatistics() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList
        restBuildStatisticMockMvc.perform(get("/api/build-statistics?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buildStatistic.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }

    @Test
    @Transactional
    public void getBuildStatistic() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get the buildStatistic
        restBuildStatisticMockMvc.perform(get("/api/build-statistics/{id}", buildStatistic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(buildStatistic.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()));
    }


    @Test
    @Transactional
    public void getBuildStatisticsByIdFiltering() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        Long id = buildStatistic.getId();

        defaultBuildStatisticShouldBeFound("id.equals=" + id);
        defaultBuildStatisticShouldNotBeFound("id.notEquals=" + id);

        defaultBuildStatisticShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBuildStatisticShouldNotBeFound("id.greaterThan=" + id);

        defaultBuildStatisticShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBuildStatisticShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBuildStatisticsByKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where key equals to DEFAULT_KEY
        defaultBuildStatisticShouldBeFound("key.equals=" + DEFAULT_KEY);

        // Get all the buildStatisticList where key equals to UPDATED_KEY
        defaultBuildStatisticShouldNotBeFound("key.equals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where key not equals to DEFAULT_KEY
        defaultBuildStatisticShouldNotBeFound("key.notEquals=" + DEFAULT_KEY);

        // Get all the buildStatisticList where key not equals to UPDATED_KEY
        defaultBuildStatisticShouldBeFound("key.notEquals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByKeyIsInShouldWork() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where key in DEFAULT_KEY or UPDATED_KEY
        defaultBuildStatisticShouldBeFound("key.in=" + DEFAULT_KEY + "," + UPDATED_KEY);

        // Get all the buildStatisticList where key equals to UPDATED_KEY
        defaultBuildStatisticShouldNotBeFound("key.in=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where key is not null
        defaultBuildStatisticShouldBeFound("key.specified=true");

        // Get all the buildStatisticList where key is null
        defaultBuildStatisticShouldNotBeFound("key.specified=false");
    }
                @Test
    @Transactional
    public void getAllBuildStatisticsByKeyContainsSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where key contains DEFAULT_KEY
        defaultBuildStatisticShouldBeFound("key.contains=" + DEFAULT_KEY);

        // Get all the buildStatisticList where key contains UPDATED_KEY
        defaultBuildStatisticShouldNotBeFound("key.contains=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByKeyNotContainsSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where key does not contain DEFAULT_KEY
        defaultBuildStatisticShouldNotBeFound("key.doesNotContain=" + DEFAULT_KEY);

        // Get all the buildStatisticList where key does not contain UPDATED_KEY
        defaultBuildStatisticShouldBeFound("key.doesNotContain=" + UPDATED_KEY);
    }


    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value equals to DEFAULT_VALUE
        defaultBuildStatisticShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the buildStatisticList where value equals to UPDATED_VALUE
        defaultBuildStatisticShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value not equals to DEFAULT_VALUE
        defaultBuildStatisticShouldNotBeFound("value.notEquals=" + DEFAULT_VALUE);

        // Get all the buildStatisticList where value not equals to UPDATED_VALUE
        defaultBuildStatisticShouldBeFound("value.notEquals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultBuildStatisticShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the buildStatisticList where value equals to UPDATED_VALUE
        defaultBuildStatisticShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value is not null
        defaultBuildStatisticShouldBeFound("value.specified=true");

        // Get all the buildStatisticList where value is null
        defaultBuildStatisticShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value is greater than or equal to DEFAULT_VALUE
        defaultBuildStatisticShouldBeFound("value.greaterThanOrEqual=" + DEFAULT_VALUE);

        // Get all the buildStatisticList where value is greater than or equal to UPDATED_VALUE
        defaultBuildStatisticShouldNotBeFound("value.greaterThanOrEqual=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value is less than or equal to DEFAULT_VALUE
        defaultBuildStatisticShouldBeFound("value.lessThanOrEqual=" + DEFAULT_VALUE);

        // Get all the buildStatisticList where value is less than or equal to SMALLER_VALUE
        defaultBuildStatisticShouldNotBeFound("value.lessThanOrEqual=" + SMALLER_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsLessThanSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value is less than DEFAULT_VALUE
        defaultBuildStatisticShouldNotBeFound("value.lessThan=" + DEFAULT_VALUE);

        // Get all the buildStatisticList where value is less than UPDATED_VALUE
        defaultBuildStatisticShouldBeFound("value.lessThan=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllBuildStatisticsByValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        // Get all the buildStatisticList where value is greater than DEFAULT_VALUE
        defaultBuildStatisticShouldNotBeFound("value.greaterThan=" + DEFAULT_VALUE);

        // Get all the buildStatisticList where value is greater than SMALLER_VALUE
        defaultBuildStatisticShouldBeFound("value.greaterThan=" + SMALLER_VALUE);
    }


    @Test
    @Transactional
    public void getAllBuildStatisticsByBuildIsEqualToSomething() throws Exception {
        // Get already existing entity
        Build build = buildStatistic.getBuild();
        buildStatisticRepository.saveAndFlush(buildStatistic);
        Long buildId = build.getId();

        // Get all the buildStatisticList where build equals to buildId
        defaultBuildStatisticShouldBeFound("buildId.equals=" + buildId);

        // Get all the buildStatisticList where build equals to buildId + 1
        defaultBuildStatisticShouldNotBeFound("buildId.equals=" + (buildId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBuildStatisticShouldBeFound(String filter) throws Exception {
        restBuildStatisticMockMvc.perform(get("/api/build-statistics?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buildStatistic.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));

        // Check, that the count call also returns 1
        restBuildStatisticMockMvc.perform(get("/api/build-statistics/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBuildStatisticShouldNotBeFound(String filter) throws Exception {
        restBuildStatisticMockMvc.perform(get("/api/build-statistics?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBuildStatisticMockMvc.perform(get("/api/build-statistics/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBuildStatistic() throws Exception {
        // Get the buildStatistic
        restBuildStatisticMockMvc.perform(get("/api/build-statistics/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBuildStatistic() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        int databaseSizeBeforeUpdate = buildStatisticRepository.findAll().size();

        // Update the buildStatistic
        BuildStatistic updatedBuildStatistic = buildStatisticRepository.findById(buildStatistic.getId()).get();
        // Disconnect from session so that the updates on updatedBuildStatistic are not directly saved in db
        em.detach(updatedBuildStatistic);
        updatedBuildStatistic
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE);
        BuildStatisticDTO buildStatisticDTO = buildStatisticMapper.toDto(updatedBuildStatistic);

        restBuildStatisticMockMvc.perform(put("/api/build-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStatisticDTO)))
            .andExpect(status().isOk());

        // Validate the BuildStatistic in the database
        List<BuildStatistic> buildStatisticList = buildStatisticRepository.findAll();
        assertThat(buildStatisticList).hasSize(databaseSizeBeforeUpdate);
        BuildStatistic testBuildStatistic = buildStatisticList.get(buildStatisticList.size() - 1);
        assertThat(testBuildStatistic.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testBuildStatistic.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void updateNonExistingBuildStatistic() throws Exception {
        int databaseSizeBeforeUpdate = buildStatisticRepository.findAll().size();

        // Create the BuildStatistic
        BuildStatisticDTO buildStatisticDTO = buildStatisticMapper.toDto(buildStatistic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildStatisticMockMvc.perform(put("/api/build-statistics")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStatisticDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BuildStatistic in the database
        List<BuildStatistic> buildStatisticList = buildStatisticRepository.findAll();
        assertThat(buildStatisticList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBuildStatistic() throws Exception {
        // Initialize the database
        buildStatisticRepository.saveAndFlush(buildStatistic);

        int databaseSizeBeforeDelete = buildStatisticRepository.findAll().size();

        // Delete the buildStatistic
        restBuildStatisticMockMvc.perform(delete("/api/build-statistics/{id}", buildStatistic.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BuildStatistic> buildStatisticList = buildStatisticRepository.findAll();
        assertThat(buildStatisticList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
