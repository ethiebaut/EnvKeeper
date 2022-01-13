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
import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.domain.Deployment;
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.repository.BuildRepository;
import cc.envkeeper.app.service.BuildService;
import cc.envkeeper.app.service.dto.BuildDTO;
import cc.envkeeper.app.service.mapper.BuildMapper;
import cc.envkeeper.app.service.dto.BuildCriteria;
import cc.envkeeper.app.service.BuildQueryService;

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
 * Integration tests for the {@link BuildResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class BuildResourceIT {

    private static final String DEFAULT_BUILD_URL = "AAAAAAAAAA";
    private static final String UPDATED_BUILD_URL = "BBBBBBBBBB";

    private static final String DEFAULT_BUILD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BUILD_NAME = "BBBBBBBBBB";

    private static final BuildStatus DEFAULT_STATUS = BuildStatus.IN_PROGRESS;
    private static final BuildStatus UPDATED_STATUS = BuildStatus.SUCCEEDED;

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private BuildMapper buildMapper;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildQueryService buildQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBuildMockMvc;

    private Build build;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Build createEntity(EntityManager em) {
        Build build = new Build()
            .buildUrl(DEFAULT_BUILD_URL)
            .buildName(DEFAULT_BUILD_NAME)
            .status(DEFAULT_STATUS)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME);
        return build;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Build createUpdatedEntity(EntityManager em) {
        Build build = new Build()
            .buildUrl(UPDATED_BUILD_URL)
            .buildName(UPDATED_BUILD_NAME)
            .status(UPDATED_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);
        return build;
    }

    @BeforeEach
    public void initTest() {
        build = createEntity(em);
    }

    @Test
    @Transactional
    public void createBuild() throws Exception {
        int databaseSizeBeforeCreate = buildRepository.findAll().size();
        // Create the Build
        BuildDTO buildDTO = buildMapper.toDto(build);
        restBuildMockMvc.perform(post("/api/builds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildDTO)))
            .andExpect(status().isCreated());

        // Validate the Build in the database
        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeCreate + 1);
        Build testBuild = buildList.get(buildList.size() - 1);
        assertThat(testBuild.getBuildUrl()).isEqualTo(DEFAULT_BUILD_URL);
        assertThat(testBuild.getBuildName()).isEqualTo(DEFAULT_BUILD_NAME);
        assertThat(testBuild.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBuild.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testBuild.getEndTime()).isEqualTo(DEFAULT_END_TIME);
    }

    @Test
    @Transactional
    public void createBuildWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = buildRepository.findAll().size();

        // Create the Build with an existing ID
        build.setId(1L);
        BuildDTO buildDTO = buildMapper.toDto(build);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBuildMockMvc.perform(post("/api/builds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Build in the database
        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkBuildUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildRepository.findAll().size();
        // set the field null
        build.setBuildUrl(null);

        // Create the Build, which fails.
        BuildDTO buildDTO = buildMapper.toDto(build);


        restBuildMockMvc.perform(post("/api/builds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildDTO)))
            .andExpect(status().isBadRequest());

        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildRepository.findAll().size();
        // set the field null
        build.setStatus(null);

        // Create the Build, which fails.
        BuildDTO buildDTO = buildMapper.toDto(build);


        restBuildMockMvc.perform(post("/api/builds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildDTO)))
            .andExpect(status().isBadRequest());

        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildRepository.findAll().size();
        // set the field null
        build.setStartTime(null);

        // Create the Build, which fails.
        BuildDTO buildDTO = buildMapper.toDto(build);


        restBuildMockMvc.perform(post("/api/builds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildDTO)))
            .andExpect(status().isBadRequest());

        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBuilds() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList
        restBuildMockMvc.perform(get("/api/builds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(build.getId().intValue())))
            .andExpect(jsonPath("$.[*].buildUrl").value(hasItem(DEFAULT_BUILD_URL)))
            .andExpect(jsonPath("$.[*].buildName").value(hasItem(DEFAULT_BUILD_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }

    @Test
    @Transactional
    public void getBuild() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get the build
        restBuildMockMvc.perform(get("/api/builds/{id}", build.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(build.getId().intValue()))
            .andExpect(jsonPath("$.buildUrl").value(DEFAULT_BUILD_URL))
            .andExpect(jsonPath("$.buildName").value(DEFAULT_BUILD_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }


    @Test
    @Transactional
    public void getBuildsByIdFiltering() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        Long id = build.getId();

        defaultBuildShouldBeFound("id.equals=" + id);
        defaultBuildShouldNotBeFound("id.notEquals=" + id);

        defaultBuildShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBuildShouldNotBeFound("id.greaterThan=" + id);

        defaultBuildShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBuildShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBuildsByBuildUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildUrl equals to DEFAULT_BUILD_URL
        defaultBuildShouldBeFound("buildUrl.equals=" + DEFAULT_BUILD_URL);

        // Get all the buildList where buildUrl equals to UPDATED_BUILD_URL
        defaultBuildShouldNotBeFound("buildUrl.equals=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildUrl not equals to DEFAULT_BUILD_URL
        defaultBuildShouldNotBeFound("buildUrl.notEquals=" + DEFAULT_BUILD_URL);

        // Get all the buildList where buildUrl not equals to UPDATED_BUILD_URL
        defaultBuildShouldBeFound("buildUrl.notEquals=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildUrlIsInShouldWork() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildUrl in DEFAULT_BUILD_URL or UPDATED_BUILD_URL
        defaultBuildShouldBeFound("buildUrl.in=" + DEFAULT_BUILD_URL + "," + UPDATED_BUILD_URL);

        // Get all the buildList where buildUrl equals to UPDATED_BUILD_URL
        defaultBuildShouldNotBeFound("buildUrl.in=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildUrl is not null
        defaultBuildShouldBeFound("buildUrl.specified=true");

        // Get all the buildList where buildUrl is null
        defaultBuildShouldNotBeFound("buildUrl.specified=false");
    }
                @Test
    @Transactional
    public void getAllBuildsByBuildUrlContainsSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildUrl contains DEFAULT_BUILD_URL
        defaultBuildShouldBeFound("buildUrl.contains=" + DEFAULT_BUILD_URL);

        // Get all the buildList where buildUrl contains UPDATED_BUILD_URL
        defaultBuildShouldNotBeFound("buildUrl.contains=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildUrlNotContainsSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildUrl does not contain DEFAULT_BUILD_URL
        defaultBuildShouldNotBeFound("buildUrl.doesNotContain=" + DEFAULT_BUILD_URL);

        // Get all the buildList where buildUrl does not contain UPDATED_BUILD_URL
        defaultBuildShouldBeFound("buildUrl.doesNotContain=" + UPDATED_BUILD_URL);
    }


    @Test
    @Transactional
    public void getAllBuildsByBuildNameIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildName equals to DEFAULT_BUILD_NAME
        defaultBuildShouldBeFound("buildName.equals=" + DEFAULT_BUILD_NAME);

        // Get all the buildList where buildName equals to UPDATED_BUILD_NAME
        defaultBuildShouldNotBeFound("buildName.equals=" + UPDATED_BUILD_NAME);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildName not equals to DEFAULT_BUILD_NAME
        defaultBuildShouldNotBeFound("buildName.notEquals=" + DEFAULT_BUILD_NAME);

        // Get all the buildList where buildName not equals to UPDATED_BUILD_NAME
        defaultBuildShouldBeFound("buildName.notEquals=" + UPDATED_BUILD_NAME);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildNameIsInShouldWork() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildName in DEFAULT_BUILD_NAME or UPDATED_BUILD_NAME
        defaultBuildShouldBeFound("buildName.in=" + DEFAULT_BUILD_NAME + "," + UPDATED_BUILD_NAME);

        // Get all the buildList where buildName equals to UPDATED_BUILD_NAME
        defaultBuildShouldNotBeFound("buildName.in=" + UPDATED_BUILD_NAME);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildName is not null
        defaultBuildShouldBeFound("buildName.specified=true");

        // Get all the buildList where buildName is null
        defaultBuildShouldNotBeFound("buildName.specified=false");
    }
                @Test
    @Transactional
    public void getAllBuildsByBuildNameContainsSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildName contains DEFAULT_BUILD_NAME
        defaultBuildShouldBeFound("buildName.contains=" + DEFAULT_BUILD_NAME);

        // Get all the buildList where buildName contains UPDATED_BUILD_NAME
        defaultBuildShouldNotBeFound("buildName.contains=" + UPDATED_BUILD_NAME);
    }

    @Test
    @Transactional
    public void getAllBuildsByBuildNameNotContainsSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where buildName does not contain DEFAULT_BUILD_NAME
        defaultBuildShouldNotBeFound("buildName.doesNotContain=" + DEFAULT_BUILD_NAME);

        // Get all the buildList where buildName does not contain UPDATED_BUILD_NAME
        defaultBuildShouldBeFound("buildName.doesNotContain=" + UPDATED_BUILD_NAME);
    }


    @Test
    @Transactional
    public void getAllBuildsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where status equals to DEFAULT_STATUS
        defaultBuildShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the buildList where status equals to UPDATED_STATUS
        defaultBuildShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBuildsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where status not equals to DEFAULT_STATUS
        defaultBuildShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the buildList where status not equals to UPDATED_STATUS
        defaultBuildShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBuildsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultBuildShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the buildList where status equals to UPDATED_STATUS
        defaultBuildShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBuildsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where status is not null
        defaultBuildShouldBeFound("status.specified=true");

        // Get all the buildList where status is null
        defaultBuildShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where startTime equals to DEFAULT_START_TIME
        defaultBuildShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the buildList where startTime equals to UPDATED_START_TIME
        defaultBuildShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildsByStartTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where startTime not equals to DEFAULT_START_TIME
        defaultBuildShouldNotBeFound("startTime.notEquals=" + DEFAULT_START_TIME);

        // Get all the buildList where startTime not equals to UPDATED_START_TIME
        defaultBuildShouldBeFound("startTime.notEquals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where startTime in DEFAULT_START_TIME or UPDATED_START_TIME
        defaultBuildShouldBeFound("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME);

        // Get all the buildList where startTime equals to UPDATED_START_TIME
        defaultBuildShouldNotBeFound("startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where startTime is not null
        defaultBuildShouldBeFound("startTime.specified=true");

        // Get all the buildList where startTime is null
        defaultBuildShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where endTime equals to DEFAULT_END_TIME
        defaultBuildShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the buildList where endTime equals to UPDATED_END_TIME
        defaultBuildShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildsByEndTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where endTime not equals to DEFAULT_END_TIME
        defaultBuildShouldNotBeFound("endTime.notEquals=" + DEFAULT_END_TIME);

        // Get all the buildList where endTime not equals to UPDATED_END_TIME
        defaultBuildShouldBeFound("endTime.notEquals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where endTime in DEFAULT_END_TIME or UPDATED_END_TIME
        defaultBuildShouldBeFound("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME);

        // Get all the buildList where endTime equals to UPDATED_END_TIME
        defaultBuildShouldNotBeFound("endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllBuildsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        // Get all the buildList where endTime is not null
        defaultBuildShouldBeFound("endTime.specified=true");

        // Get all the buildList where endTime is null
        defaultBuildShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllBuildsByParentBuildIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);
        Build parentBuild = BuildResourceIT.createEntity(em);
        em.persist(parentBuild);
        em.flush();
        build.setParentBuild(parentBuild);
        buildRepository.saveAndFlush(build);
        Long parentBuildId = parentBuild.getId();

        // Get all the buildList where parentBuild equals to parentBuildId
        defaultBuildShouldBeFound("parentBuildId.equals=" + parentBuildId);

        // Get all the buildList where parentBuild equals to parentBuildId + 1
        defaultBuildShouldNotBeFound("parentBuildId.equals=" + (parentBuildId + 1));
    }


    @Test
    @Transactional
    public void getAllBuildsByDeploymentIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);
        Deployment deployment = DeploymentResourceIT.createEntity(em);
        em.persist(deployment);
        em.flush();
        build.addDeployment(deployment);
        buildRepository.saveAndFlush(build);
        Long deploymentId = deployment.getId();

        // Get all the buildList where deployment equals to deploymentId
        defaultBuildShouldBeFound("deploymentId.equals=" + deploymentId);

        // Get all the buildList where deployment equals to deploymentId + 1
        defaultBuildShouldNotBeFound("deploymentId.equals=" + (deploymentId + 1));
    }


    @Test
    @Transactional
    public void getAllBuildsByProductVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);
        ProductVersion productVersion = ProductVersionResourceIT.createEntity(em);
        em.persist(productVersion);
        em.flush();
        build.addProductVersion(productVersion);
        buildRepository.saveAndFlush(build);
        Long productVersionId = productVersion.getId();

        // Get all the buildList where productVersion equals to productVersionId
        defaultBuildShouldBeFound("productVersionId.equals=" + productVersionId);

        // Get all the buildList where productVersion equals to productVersionId + 1
        defaultBuildShouldNotBeFound("productVersionId.equals=" + (productVersionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBuildShouldBeFound(String filter) throws Exception {
        restBuildMockMvc.perform(get("/api/builds?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(build.getId().intValue())))
            .andExpect(jsonPath("$.[*].buildUrl").value(hasItem(DEFAULT_BUILD_URL)))
            .andExpect(jsonPath("$.[*].buildName").value(hasItem(DEFAULT_BUILD_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));

        // Check, that the count call also returns 1
        restBuildMockMvc.perform(get("/api/builds/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBuildShouldNotBeFound(String filter) throws Exception {
        restBuildMockMvc.perform(get("/api/builds?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBuildMockMvc.perform(get("/api/builds/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBuild() throws Exception {
        // Get the build
        restBuildMockMvc.perform(get("/api/builds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBuild() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        int databaseSizeBeforeUpdate = buildRepository.findAll().size();

        // Update the build
        Build updatedBuild = buildRepository.findById(build.getId()).get();
        // Disconnect from session so that the updates on updatedBuild are not directly saved in db
        em.detach(updatedBuild);
        updatedBuild
            .buildUrl(UPDATED_BUILD_URL)
            .buildName(UPDATED_BUILD_NAME)
            .status(UPDATED_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);
        BuildDTO buildDTO = buildMapper.toDto(updatedBuild);

        restBuildMockMvc.perform(put("/api/builds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildDTO)))
            .andExpect(status().isOk());

        // Validate the Build in the database
        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeUpdate);
        Build testBuild = buildList.get(buildList.size() - 1);
        assertThat(testBuild.getBuildUrl()).isEqualTo(UPDATED_BUILD_URL);
        assertThat(testBuild.getBuildName()).isEqualTo(UPDATED_BUILD_NAME);
        assertThat(testBuild.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBuild.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testBuild.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingBuild() throws Exception {
        int databaseSizeBeforeUpdate = buildRepository.findAll().size();

        // Create the Build
        BuildDTO buildDTO = buildMapper.toDto(build);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildMockMvc.perform(put("/api/builds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(buildDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Build in the database
        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBuild() throws Exception {
        // Initialize the database
        buildRepository.saveAndFlush(build);

        int databaseSizeBeforeDelete = buildRepository.findAll().size();

        // Delete the build
        restBuildMockMvc.perform(delete("/api/builds/{id}", build.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Build> buildList = buildRepository.findAll();
        assertThat(buildList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
