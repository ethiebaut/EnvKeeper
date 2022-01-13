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
import cc.envkeeper.app.domain.BuildStep;
import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.repository.BuildStepRepository;
import cc.envkeeper.app.service.BuildStepService;
import cc.envkeeper.app.service.dto.BuildStepDTO;
import cc.envkeeper.app.service.mapper.BuildStepMapper;
import cc.envkeeper.app.service.dto.BuildStepCriteria;
import cc.envkeeper.app.service.BuildStepQueryService;

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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cc.envkeeper.app.domain.enumeration.BuildStatus;
/**
 * Integration tests for the {@link BuildStepResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class BuildStepResourceIT {

    private static final String DEFAULT_STEP = "AAAAAAAAAA";
    private static final String UPDATED_STEP = "BBBBBBBBBB";

    private static final BuildStatus DEFAULT_STATUS = BuildStatus.IN_PROGRESS;
    private static final BuildStatus UPDATED_STATUS = BuildStatus.SUCCEEDED;

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private BuildStepRepository buildStepRepository;

    @Autowired
    private BuildStepMapper buildStepMapper;

    @Autowired
    private BuildStepService buildStepService;

    @Autowired
    private BuildStepQueryService buildStepQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBuildStepMockMvc;

    private BuildStep buildStep;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BuildStep createEntity(EntityManager em) {
        BuildStep buildStep = new BuildStep()
            .step(DEFAULT_STEP)
            .status(DEFAULT_STATUS)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME);
        // Add required entity
        Build build;
        if (TestUtil.findAll(em, Build.class).isEmpty()) {
            build = BuildResourceIT.createEntity(em);
            em.persist(build);
            em.flush();
        } else {
            build = TestUtil.findAll(em, Build.class).get(0);
        }
        buildStep.setBuild(build);
        return buildStep;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BuildStep createUpdatedEntity(EntityManager em) {
        BuildStep buildStep = new BuildStep()
            .step(UPDATED_STEP)
            .status(UPDATED_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);
        // Add required entity
        Build build;
        if (TestUtil.findAll(em, Build.class).isEmpty()) {
            build = BuildResourceIT.createUpdatedEntity(em);
            em.persist(build);
            em.flush();
        } else {
            build = TestUtil.findAll(em, Build.class).get(0);
        }
        buildStep.setBuild(build);
        return buildStep;
    }

    @BeforeEach
    public void initTest() {
        buildStep = createEntity(em);
    }

    @Test
    @Transactional
    public void createBuildStep() throws Exception {
        int databaseSizeBeforeCreate = buildStepRepository.findAll().size();
        // Create the BuildStep
        BuildStepDTO buildStepDTO = buildStepMapper.toDto(buildStep);
        restBuildStepMockMvc.perform(post("/api/build-steps")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepDTO)))
            .andExpect(status().isCreated());

        // Validate the BuildStep in the database
        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeCreate + 1);
        BuildStep testBuildStep = buildStepList.get(buildStepList.size() - 1);
        assertThat(testBuildStep.getStep()).isEqualTo(DEFAULT_STEP);
        assertThat(testBuildStep.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBuildStep.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testBuildStep.getEndTime()).isEqualTo(DEFAULT_END_TIME);
    }

    @Test
    @Transactional
    public void createBuildStepWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = buildStepRepository.findAll().size();

        // Create the BuildStep with an existing ID
        buildStep.setId(1L);
        BuildStepDTO buildStepDTO = buildStepMapper.toDto(buildStep);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBuildStepMockMvc.perform(post("/api/build-steps")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BuildStep in the database
        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkStepIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildStepRepository.findAll().size();
        // set the field null
        buildStep.setStep(null);

        // Create the BuildStep, which fails.
        BuildStepDTO buildStepDTO = buildStepMapper.toDto(buildStep);


        restBuildStepMockMvc.perform(post("/api/build-steps")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepDTO)))
            .andExpect(status().isBadRequest());

        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildStepRepository.findAll().size();
        // set the field null
        buildStep.setStatus(null);

        // Create the BuildStep, which fails.
        BuildStepDTO buildStepDTO = buildStepMapper.toDto(buildStep);


        restBuildStepMockMvc.perform(post("/api/build-steps")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepDTO)))
            .andExpect(status().isBadRequest());

        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildStepRepository.findAll().size();
        // set the field null
        buildStep.setStartTime(null);

        // Create the BuildStep, which fails.
        BuildStepDTO buildStepDTO = buildStepMapper.toDto(buildStep);


        restBuildStepMockMvc.perform(post("/api/build-steps")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepDTO)))
            .andExpect(status().isBadRequest());

        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBuildSteps() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList
        restBuildStepMockMvc.perform(get("/api/build-steps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buildStep.getId().intValue())))
            .andExpect(jsonPath("$.[*].step").value(hasItem(DEFAULT_STEP)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }

    @Test
    @Transactional
    public void getBuildStep() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get the buildStep
        restBuildStepMockMvc.perform(get("/api/build-steps/{id}", buildStep.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(buildStep.getId().intValue()))
            .andExpect(jsonPath("$.step").value(DEFAULT_STEP))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }


    @Test
    @Transactional
    public void getBuildStepsByIdFiltering() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        Long id = buildStep.getId();

        defaultBuildStepShouldBeFound("id.equals=" + id);
        defaultBuildStepShouldNotBeFound("id.notEquals=" + id);

        defaultBuildStepShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBuildStepShouldNotBeFound("id.greaterThan=" + id);

        defaultBuildStepShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBuildStepShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBuildStepsByStepIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where step equals to DEFAULT_STEP
        defaultBuildStepShouldBeFound("step.equals=" + DEFAULT_STEP);

        // Get all the buildStepList where step equals to UPDATED_STEP
        defaultBuildStepShouldNotBeFound("step.equals=" + UPDATED_STEP);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStepIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where step not equals to DEFAULT_STEP
        defaultBuildStepShouldNotBeFound("step.notEquals=" + DEFAULT_STEP);

        // Get all the buildStepList where step not equals to UPDATED_STEP
        defaultBuildStepShouldBeFound("step.notEquals=" + UPDATED_STEP);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStepIsInShouldWork() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where step in DEFAULT_STEP or UPDATED_STEP
        defaultBuildStepShouldBeFound("step.in=" + DEFAULT_STEP + "," + UPDATED_STEP);

        // Get all the buildStepList where step equals to UPDATED_STEP
        defaultBuildStepShouldNotBeFound("step.in=" + UPDATED_STEP);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStepIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where step is not null
        defaultBuildStepShouldBeFound("step.specified=true");

        // Get all the buildStepList where step is null
        defaultBuildStepShouldNotBeFound("step.specified=false");
    }
                @Test
    @Transactional
    public void getAllBuildStepsByStepContainsSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where step contains DEFAULT_STEP
        defaultBuildStepShouldBeFound("step.contains=" + DEFAULT_STEP);

        // Get all the buildStepList where step contains UPDATED_STEP
        defaultBuildStepShouldNotBeFound("step.contains=" + UPDATED_STEP);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStepNotContainsSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where step does not contain DEFAULT_STEP
        defaultBuildStepShouldNotBeFound("step.doesNotContain=" + DEFAULT_STEP);

        // Get all the buildStepList where step does not contain UPDATED_STEP
        defaultBuildStepShouldBeFound("step.doesNotContain=" + UPDATED_STEP);
    }


    @Test
    @Transactional
    public void getAllBuildStepsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where status equals to DEFAULT_STATUS
        defaultBuildStepShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the buildStepList where status equals to UPDATED_STATUS
        defaultBuildStepShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where status not equals to DEFAULT_STATUS
        defaultBuildStepShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the buildStepList where status not equals to UPDATED_STATUS
        defaultBuildStepShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultBuildStepShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the buildStepList where status equals to UPDATED_STATUS
        defaultBuildStepShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where status is not null
        defaultBuildStepShouldBeFound("status.specified=true");

        // Get all the buildStepList where status is null
        defaultBuildStepShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where startTime equals to DEFAULT_START_TIME
        defaultBuildStepShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the buildStepList where startTime equals to UPDATED_START_TIME
        defaultBuildStepShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStartTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where startTime not equals to DEFAULT_START_TIME
        defaultBuildStepShouldNotBeFound("startTime.notEquals=" + DEFAULT_START_TIME);

        // Get all the buildStepList where startTime not equals to UPDATED_START_TIME
        defaultBuildStepShouldBeFound("startTime.notEquals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where startTime in DEFAULT_START_TIME or UPDATED_START_TIME
        defaultBuildStepShouldBeFound("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME);

        // Get all the buildStepList where startTime equals to UPDATED_START_TIME
        defaultBuildStepShouldNotBeFound("startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where startTime is not null
        defaultBuildStepShouldBeFound("startTime.specified=true");

        // Get all the buildStepList where startTime is null
        defaultBuildStepShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildStepsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where endTime equals to DEFAULT_END_TIME
        defaultBuildStepShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the buildStepList where endTime equals to UPDATED_END_TIME
        defaultBuildStepShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByEndTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where endTime not equals to DEFAULT_END_TIME
        defaultBuildStepShouldNotBeFound("endTime.notEquals=" + DEFAULT_END_TIME);

        // Get all the buildStepList where endTime not equals to UPDATED_END_TIME
        defaultBuildStepShouldBeFound("endTime.notEquals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where endTime in DEFAULT_END_TIME or UPDATED_END_TIME
        defaultBuildStepShouldBeFound("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME);

        // Get all the buildStepList where endTime equals to UPDATED_END_TIME
        defaultBuildStepShouldNotBeFound("endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildStepsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        // Get all the buildStepList where endTime is not null
        defaultBuildStepShouldBeFound("endTime.specified=true");

        // Get all the buildStepList where endTime is null
        defaultBuildStepShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildStepsByBuildIsEqualToSomething() throws Exception {
        // Get already existing entity
        Build build = buildStep.getBuild();
        buildStepRepository.saveAndFlush(buildStep);
        Long buildId = build.getId();

        // Get all the buildStepList where build equals to buildId
        defaultBuildStepShouldBeFound("buildId.equals=" + buildId);

        // Get all the buildStepList where build equals to buildId + 1
        defaultBuildStepShouldNotBeFound("buildId.equals=" + (buildId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBuildStepShouldBeFound(String filter) throws Exception {
        restBuildStepMockMvc.perform(get("/api/build-steps?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buildStep.getId().intValue())))
            .andExpect(jsonPath("$.[*].step").value(hasItem(DEFAULT_STEP)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));

        // Check, that the count call also returns 1
        restBuildStepMockMvc.perform(get("/api/build-steps/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBuildStepShouldNotBeFound(String filter) throws Exception {
        restBuildStepMockMvc.perform(get("/api/build-steps?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBuildStepMockMvc.perform(get("/api/build-steps/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBuildStep() throws Exception {
        // Get the buildStep
        restBuildStepMockMvc.perform(get("/api/build-steps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBuildStep() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        int databaseSizeBeforeUpdate = buildStepRepository.findAll().size();

        // Update the buildStep
        BuildStep updatedBuildStep = buildStepRepository.findById(buildStep.getId()).get();
        // Disconnect from session so that the updates on updatedBuildStep are not directly saved in db
        em.detach(updatedBuildStep);
        updatedBuildStep
            .step(UPDATED_STEP)
            .status(UPDATED_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);
        BuildStepDTO buildStepDTO = buildStepMapper.toDto(updatedBuildStep);

        restBuildStepMockMvc.perform(put("/api/build-steps")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepDTO)))
            .andExpect(status().isOk());

        // Validate the BuildStep in the database
        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeUpdate);
        BuildStep testBuildStep = buildStepList.get(buildStepList.size() - 1);
        assertThat(testBuildStep.getStep()).isEqualTo(UPDATED_STEP);
        assertThat(testBuildStep.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBuildStep.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testBuildStep.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingBuildStep() throws Exception {
        int databaseSizeBeforeUpdate = buildStepRepository.findAll().size();

        // Create the BuildStep
        BuildStepDTO buildStepDTO = buildStepMapper.toDto(buildStep);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildStepMockMvc.perform(put("/api/build-steps")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildStepDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BuildStep in the database
        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBuildStep() throws Exception {
        // Initialize the database
        buildStepRepository.saveAndFlush(buildStep);

        int databaseSizeBeforeDelete = buildStepRepository.findAll().size();

        // Delete the buildStep
        restBuildStepMockMvc.perform(delete("/api/build-steps/{id}", buildStep.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BuildStep> buildStepList = buildStepRepository.findAll();
        assertThat(buildStepList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
