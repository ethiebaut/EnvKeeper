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
import cc.envkeeper.app.domain.ProductComponentVersion;
import cc.envkeeper.app.domain.ProductComponent;
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.repository.ProductComponentVersionRepository;
import cc.envkeeper.app.service.ProductComponentVersionService;
import cc.envkeeper.app.service.dto.ProductComponentVersionDTO;
import cc.envkeeper.app.service.mapper.ProductComponentVersionMapper;
import cc.envkeeper.app.service.dto.ProductComponentVersionCriteria;
import cc.envkeeper.app.service.ProductComponentVersionQueryService;

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
 * Integration tests for the {@link ProductComponentVersionResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class ProductComponentVersionResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final BuildStatus DEFAULT_BUILD_STATUS = BuildStatus.IN_PROGRESS;
    private static final BuildStatus UPDATED_BUILD_STATUS = BuildStatus.SUCCEEDED;

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_BUILD_URL = "AAAAAAAAAA";
    private static final String UPDATED_BUILD_URL = "BBBBBBBBBB";

    private static final String DEFAULT_RELEASE_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_RELEASE_NOTES = "BBBBBBBBBB";

    @Autowired
    private ProductComponentVersionRepository productComponentVersionRepository;

    @Autowired
    private ProductComponentVersionMapper productComponentVersionMapper;

    @Autowired
    private ProductComponentVersionService productComponentVersionService;

    @Autowired
    private ProductComponentVersionQueryService productComponentVersionQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductComponentVersionMockMvc;

    private ProductComponentVersion productComponentVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductComponentVersion createEntity(EntityManager em) {
        ProductComponentVersion productComponentVersion = new ProductComponentVersion()
            .version(DEFAULT_VERSION)
            .buildStatus(DEFAULT_BUILD_STATUS)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .buildUrl(DEFAULT_BUILD_URL)
            .releaseNotes(DEFAULT_RELEASE_NOTES);
        // Add required entity
        ProductComponent productComponent;
        if (TestUtil.findAll(em, ProductComponent.class).isEmpty()) {
            productComponent = ProductComponentResourceIT.createEntity(em);
            em.persist(productComponent);
            em.flush();
        } else {
            productComponent = TestUtil.findAll(em, ProductComponent.class).get(0);
        }
        productComponentVersion.setComponent(productComponent);
        return productComponentVersion;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductComponentVersion createUpdatedEntity(EntityManager em) {
        ProductComponentVersion productComponentVersion = new ProductComponentVersion()
            .version(UPDATED_VERSION)
            .buildStatus(UPDATED_BUILD_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .buildUrl(UPDATED_BUILD_URL)
            .releaseNotes(UPDATED_RELEASE_NOTES);
        // Add required entity
        ProductComponent productComponent;
        if (TestUtil.findAll(em, ProductComponent.class).isEmpty()) {
            productComponent = ProductComponentResourceIT.createUpdatedEntity(em);
            em.persist(productComponent);
            em.flush();
        } else {
            productComponent = TestUtil.findAll(em, ProductComponent.class).get(0);
        }
        productComponentVersion.setComponent(productComponent);
        return productComponentVersion;
    }

    @BeforeEach
    public void initTest() {
        productComponentVersion = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductComponentVersion() throws Exception {
        int databaseSizeBeforeCreate = productComponentVersionRepository.findAll().size();
        // Create the ProductComponentVersion
        ProductComponentVersionDTO productComponentVersionDTO = productComponentVersionMapper.toDto(productComponentVersion);
        restProductComponentVersionMockMvc.perform(post("/api/product-component-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentVersionDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductComponentVersion in the database
        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeCreate + 1);
        ProductComponentVersion testProductComponentVersion = productComponentVersionList.get(productComponentVersionList.size() - 1);
        assertThat(testProductComponentVersion.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testProductComponentVersion.getBuildStatus()).isEqualTo(DEFAULT_BUILD_STATUS);
        assertThat(testProductComponentVersion.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testProductComponentVersion.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testProductComponentVersion.getBuildUrl()).isEqualTo(DEFAULT_BUILD_URL);
        assertThat(testProductComponentVersion.getReleaseNotes()).isEqualTo(DEFAULT_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void createProductComponentVersionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productComponentVersionRepository.findAll().size();

        // Create the ProductComponentVersion with an existing ID
        productComponentVersion.setId(1L);
        ProductComponentVersionDTO productComponentVersionDTO = productComponentVersionMapper.toDto(productComponentVersion);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductComponentVersionMockMvc.perform(post("/api/product-component-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentVersionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductComponentVersion in the database
        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productComponentVersionRepository.findAll().size();
        // set the field null
        productComponentVersion.setVersion(null);

        // Create the ProductComponentVersion, which fails.
        ProductComponentVersionDTO productComponentVersionDTO = productComponentVersionMapper.toDto(productComponentVersion);


        restProductComponentVersionMockMvc.perform(post("/api/product-component-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentVersionDTO)))
            .andExpect(status().isBadRequest());

        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBuildStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = productComponentVersionRepository.findAll().size();
        // set the field null
        productComponentVersion.setBuildStatus(null);

        // Create the ProductComponentVersion, which fails.
        ProductComponentVersionDTO productComponentVersionDTO = productComponentVersionMapper.toDto(productComponentVersion);


        restProductComponentVersionMockMvc.perform(post("/api/product-component-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentVersionDTO)))
            .andExpect(status().isBadRequest());

        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = productComponentVersionRepository.findAll().size();
        // set the field null
        productComponentVersion.setStartTime(null);

        // Create the ProductComponentVersion, which fails.
        ProductComponentVersionDTO productComponentVersionDTO = productComponentVersionMapper.toDto(productComponentVersion);


        restProductComponentVersionMockMvc.perform(post("/api/product-component-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentVersionDTO)))
            .andExpect(status().isBadRequest());

        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersions() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList
        restProductComponentVersionMockMvc.perform(get("/api/product-component-versions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productComponentVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].buildStatus").value(hasItem(DEFAULT_BUILD_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].buildUrl").value(hasItem(DEFAULT_BUILD_URL)))
            .andExpect(jsonPath("$.[*].releaseNotes").value(hasItem(DEFAULT_RELEASE_NOTES)));
    }

    @Test
    @Transactional
    public void getProductComponentVersion() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get the productComponentVersion
        restProductComponentVersionMockMvc.perform(get("/api/product-component-versions/{id}", productComponentVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productComponentVersion.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.buildStatus").value(DEFAULT_BUILD_STATUS.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.buildUrl").value(DEFAULT_BUILD_URL))
            .andExpect(jsonPath("$.releaseNotes").value(DEFAULT_RELEASE_NOTES));
    }


    @Test
    @Transactional
    public void getProductComponentVersionsByIdFiltering() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        Long id = productComponentVersion.getId();

        defaultProductComponentVersionShouldBeFound("id.equals=" + id);
        defaultProductComponentVersionShouldNotBeFound("id.notEquals=" + id);

        defaultProductComponentVersionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductComponentVersionShouldNotBeFound("id.greaterThan=" + id);

        defaultProductComponentVersionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductComponentVersionShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllProductComponentVersionsByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where version equals to DEFAULT_VERSION
        defaultProductComponentVersionShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the productComponentVersionList where version equals to UPDATED_VERSION
        defaultProductComponentVersionShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where version not equals to DEFAULT_VERSION
        defaultProductComponentVersionShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the productComponentVersionList where version not equals to UPDATED_VERSION
        defaultProductComponentVersionShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultProductComponentVersionShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the productComponentVersionList where version equals to UPDATED_VERSION
        defaultProductComponentVersionShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where version is not null
        defaultProductComponentVersionShouldBeFound("version.specified=true");

        // Get all the productComponentVersionList where version is null
        defaultProductComponentVersionShouldNotBeFound("version.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductComponentVersionsByVersionContainsSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where version contains DEFAULT_VERSION
        defaultProductComponentVersionShouldBeFound("version.contains=" + DEFAULT_VERSION);

        // Get all the productComponentVersionList where version contains UPDATED_VERSION
        defaultProductComponentVersionShouldNotBeFound("version.contains=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByVersionNotContainsSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where version does not contain DEFAULT_VERSION
        defaultProductComponentVersionShouldNotBeFound("version.doesNotContain=" + DEFAULT_VERSION);

        // Get all the productComponentVersionList where version does not contain UPDATED_VERSION
        defaultProductComponentVersionShouldBeFound("version.doesNotContain=" + UPDATED_VERSION);
    }


    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildStatus equals to DEFAULT_BUILD_STATUS
        defaultProductComponentVersionShouldBeFound("buildStatus.equals=" + DEFAULT_BUILD_STATUS);

        // Get all the productComponentVersionList where buildStatus equals to UPDATED_BUILD_STATUS
        defaultProductComponentVersionShouldNotBeFound("buildStatus.equals=" + UPDATED_BUILD_STATUS);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildStatus not equals to DEFAULT_BUILD_STATUS
        defaultProductComponentVersionShouldNotBeFound("buildStatus.notEquals=" + DEFAULT_BUILD_STATUS);

        // Get all the productComponentVersionList where buildStatus not equals to UPDATED_BUILD_STATUS
        defaultProductComponentVersionShouldBeFound("buildStatus.notEquals=" + UPDATED_BUILD_STATUS);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildStatusIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildStatus in DEFAULT_BUILD_STATUS or UPDATED_BUILD_STATUS
        defaultProductComponentVersionShouldBeFound("buildStatus.in=" + DEFAULT_BUILD_STATUS + "," + UPDATED_BUILD_STATUS);

        // Get all the productComponentVersionList where buildStatus equals to UPDATED_BUILD_STATUS
        defaultProductComponentVersionShouldNotBeFound("buildStatus.in=" + UPDATED_BUILD_STATUS);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildStatus is not null
        defaultProductComponentVersionShouldBeFound("buildStatus.specified=true");

        // Get all the productComponentVersionList where buildStatus is null
        defaultProductComponentVersionShouldNotBeFound("buildStatus.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where startTime equals to DEFAULT_START_TIME
        defaultProductComponentVersionShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the productComponentVersionList where startTime equals to UPDATED_START_TIME
        defaultProductComponentVersionShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByStartTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where startTime not equals to DEFAULT_START_TIME
        defaultProductComponentVersionShouldNotBeFound("startTime.notEquals=" + DEFAULT_START_TIME);

        // Get all the productComponentVersionList where startTime not equals to UPDATED_START_TIME
        defaultProductComponentVersionShouldBeFound("startTime.notEquals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where startTime in DEFAULT_START_TIME or UPDATED_START_TIME
        defaultProductComponentVersionShouldBeFound("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME);

        // Get all the productComponentVersionList where startTime equals to UPDATED_START_TIME
        defaultProductComponentVersionShouldNotBeFound("startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where startTime is not null
        defaultProductComponentVersionShouldBeFound("startTime.specified=true");

        // Get all the productComponentVersionList where startTime is null
        defaultProductComponentVersionShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where endTime equals to DEFAULT_END_TIME
        defaultProductComponentVersionShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the productComponentVersionList where endTime equals to UPDATED_END_TIME
        defaultProductComponentVersionShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByEndTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where endTime not equals to DEFAULT_END_TIME
        defaultProductComponentVersionShouldNotBeFound("endTime.notEquals=" + DEFAULT_END_TIME);

        // Get all the productComponentVersionList where endTime not equals to UPDATED_END_TIME
        defaultProductComponentVersionShouldBeFound("endTime.notEquals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where endTime in DEFAULT_END_TIME or UPDATED_END_TIME
        defaultProductComponentVersionShouldBeFound("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME);

        // Get all the productComponentVersionList where endTime equals to UPDATED_END_TIME
        defaultProductComponentVersionShouldNotBeFound("endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where endTime is not null
        defaultProductComponentVersionShouldBeFound("endTime.specified=true");

        // Get all the productComponentVersionList where endTime is null
        defaultProductComponentVersionShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildUrl equals to DEFAULT_BUILD_URL
        defaultProductComponentVersionShouldBeFound("buildUrl.equals=" + DEFAULT_BUILD_URL);

        // Get all the productComponentVersionList where buildUrl equals to UPDATED_BUILD_URL
        defaultProductComponentVersionShouldNotBeFound("buildUrl.equals=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildUrl not equals to DEFAULT_BUILD_URL
        defaultProductComponentVersionShouldNotBeFound("buildUrl.notEquals=" + DEFAULT_BUILD_URL);

        // Get all the productComponentVersionList where buildUrl not equals to UPDATED_BUILD_URL
        defaultProductComponentVersionShouldBeFound("buildUrl.notEquals=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildUrlIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildUrl in DEFAULT_BUILD_URL or UPDATED_BUILD_URL
        defaultProductComponentVersionShouldBeFound("buildUrl.in=" + DEFAULT_BUILD_URL + "," + UPDATED_BUILD_URL);

        // Get all the productComponentVersionList where buildUrl equals to UPDATED_BUILD_URL
        defaultProductComponentVersionShouldNotBeFound("buildUrl.in=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildUrl is not null
        defaultProductComponentVersionShouldBeFound("buildUrl.specified=true");

        // Get all the productComponentVersionList where buildUrl is null
        defaultProductComponentVersionShouldNotBeFound("buildUrl.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildUrlContainsSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildUrl contains DEFAULT_BUILD_URL
        defaultProductComponentVersionShouldBeFound("buildUrl.contains=" + DEFAULT_BUILD_URL);

        // Get all the productComponentVersionList where buildUrl contains UPDATED_BUILD_URL
        defaultProductComponentVersionShouldNotBeFound("buildUrl.contains=" + UPDATED_BUILD_URL);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByBuildUrlNotContainsSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where buildUrl does not contain DEFAULT_BUILD_URL
        defaultProductComponentVersionShouldNotBeFound("buildUrl.doesNotContain=" + DEFAULT_BUILD_URL);

        // Get all the productComponentVersionList where buildUrl does not contain UPDATED_BUILD_URL
        defaultProductComponentVersionShouldBeFound("buildUrl.doesNotContain=" + UPDATED_BUILD_URL);
    }


    @Test
    @Transactional
    public void getAllProductComponentVersionsByReleaseNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where releaseNotes equals to DEFAULT_RELEASE_NOTES
        defaultProductComponentVersionShouldBeFound("releaseNotes.equals=" + DEFAULT_RELEASE_NOTES);

        // Get all the productComponentVersionList where releaseNotes equals to UPDATED_RELEASE_NOTES
        defaultProductComponentVersionShouldNotBeFound("releaseNotes.equals=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByReleaseNotesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where releaseNotes not equals to DEFAULT_RELEASE_NOTES
        defaultProductComponentVersionShouldNotBeFound("releaseNotes.notEquals=" + DEFAULT_RELEASE_NOTES);

        // Get all the productComponentVersionList where releaseNotes not equals to UPDATED_RELEASE_NOTES
        defaultProductComponentVersionShouldBeFound("releaseNotes.notEquals=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByReleaseNotesIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where releaseNotes in DEFAULT_RELEASE_NOTES or UPDATED_RELEASE_NOTES
        defaultProductComponentVersionShouldBeFound("releaseNotes.in=" + DEFAULT_RELEASE_NOTES + "," + UPDATED_RELEASE_NOTES);

        // Get all the productComponentVersionList where releaseNotes equals to UPDATED_RELEASE_NOTES
        defaultProductComponentVersionShouldNotBeFound("releaseNotes.in=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByReleaseNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where releaseNotes is not null
        defaultProductComponentVersionShouldBeFound("releaseNotes.specified=true");

        // Get all the productComponentVersionList where releaseNotes is null
        defaultProductComponentVersionShouldNotBeFound("releaseNotes.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductComponentVersionsByReleaseNotesContainsSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where releaseNotes contains DEFAULT_RELEASE_NOTES
        defaultProductComponentVersionShouldBeFound("releaseNotes.contains=" + DEFAULT_RELEASE_NOTES);

        // Get all the productComponentVersionList where releaseNotes contains UPDATED_RELEASE_NOTES
        defaultProductComponentVersionShouldNotBeFound("releaseNotes.contains=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductComponentVersionsByReleaseNotesNotContainsSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        // Get all the productComponentVersionList where releaseNotes does not contain DEFAULT_RELEASE_NOTES
        defaultProductComponentVersionShouldNotBeFound("releaseNotes.doesNotContain=" + DEFAULT_RELEASE_NOTES);

        // Get all the productComponentVersionList where releaseNotes does not contain UPDATED_RELEASE_NOTES
        defaultProductComponentVersionShouldBeFound("releaseNotes.doesNotContain=" + UPDATED_RELEASE_NOTES);
    }


    @Test
    @Transactional
    public void getAllProductComponentVersionsByComponentIsEqualToSomething() throws Exception {
        // Get already existing entity
        ProductComponent component = productComponentVersion.getComponent();
        productComponentVersionRepository.saveAndFlush(productComponentVersion);
        Long componentId = component.getId();

        // Get all the productComponentVersionList where component equals to componentId
        defaultProductComponentVersionShouldBeFound("componentId.equals=" + componentId);

        // Get all the productComponentVersionList where component equals to componentId + 1
        defaultProductComponentVersionShouldNotBeFound("componentId.equals=" + (componentId + 1));
    }


    @Test
    @Transactional
    public void getAllProductComponentVersionsByProductVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);
        ProductVersion productVersion = ProductVersionResourceIT.createEntity(em);
        em.persist(productVersion);
        em.flush();
        productComponentVersion.addProductVersion(productVersion);
        productComponentVersionRepository.saveAndFlush(productComponentVersion);
        Long productVersionId = productVersion.getId();

        // Get all the productComponentVersionList where productVersion equals to productVersionId
        defaultProductComponentVersionShouldBeFound("productVersionId.equals=" + productVersionId);

        // Get all the productComponentVersionList where productVersion equals to productVersionId + 1
        defaultProductComponentVersionShouldNotBeFound("productVersionId.equals=" + (productVersionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductComponentVersionShouldBeFound(String filter) throws Exception {
        restProductComponentVersionMockMvc.perform(get("/api/product-component-versions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productComponentVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].buildStatus").value(hasItem(DEFAULT_BUILD_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].buildUrl").value(hasItem(DEFAULT_BUILD_URL)))
            .andExpect(jsonPath("$.[*].releaseNotes").value(hasItem(DEFAULT_RELEASE_NOTES)));

        // Check, that the count call also returns 1
        restProductComponentVersionMockMvc.perform(get("/api/product-component-versions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductComponentVersionShouldNotBeFound(String filter) throws Exception {
        restProductComponentVersionMockMvc.perform(get("/api/product-component-versions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductComponentVersionMockMvc.perform(get("/api/product-component-versions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingProductComponentVersion() throws Exception {
        // Get the productComponentVersion
        restProductComponentVersionMockMvc.perform(get("/api/product-component-versions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductComponentVersion() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        int databaseSizeBeforeUpdate = productComponentVersionRepository.findAll().size();

        // Update the productComponentVersion
        ProductComponentVersion updatedProductComponentVersion = productComponentVersionRepository.findById(productComponentVersion.getId()).get();
        // Disconnect from session so that the updates on updatedProductComponentVersion are not directly saved in db
        em.detach(updatedProductComponentVersion);
        updatedProductComponentVersion
            .version(UPDATED_VERSION)
            .buildStatus(UPDATED_BUILD_STATUS)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .buildUrl(UPDATED_BUILD_URL)
            .releaseNotes(UPDATED_RELEASE_NOTES);
        ProductComponentVersionDTO productComponentVersionDTO = productComponentVersionMapper.toDto(updatedProductComponentVersion);

        restProductComponentVersionMockMvc.perform(put("/api/product-component-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentVersionDTO)))
            .andExpect(status().isOk());

        // Validate the ProductComponentVersion in the database
        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeUpdate);
        ProductComponentVersion testProductComponentVersion = productComponentVersionList.get(productComponentVersionList.size() - 1);
        assertThat(testProductComponentVersion.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testProductComponentVersion.getBuildStatus()).isEqualTo(UPDATED_BUILD_STATUS);
        assertThat(testProductComponentVersion.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testProductComponentVersion.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testProductComponentVersion.getBuildUrl()).isEqualTo(UPDATED_BUILD_URL);
        assertThat(testProductComponentVersion.getReleaseNotes()).isEqualTo(UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void updateNonExistingProductComponentVersion() throws Exception {
        int databaseSizeBeforeUpdate = productComponentVersionRepository.findAll().size();

        // Create the ProductComponentVersion
        ProductComponentVersionDTO productComponentVersionDTO = productComponentVersionMapper.toDto(productComponentVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductComponentVersionMockMvc.perform(put("/api/product-component-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentVersionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductComponentVersion in the database
        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProductComponentVersion() throws Exception {
        // Initialize the database
        productComponentVersionRepository.saveAndFlush(productComponentVersion);

        int databaseSizeBeforeDelete = productComponentVersionRepository.findAll().size();

        // Delete the productComponentVersion
        restProductComponentVersionMockMvc.perform(delete("/api/product-component-versions/{id}", productComponentVersion.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductComponentVersion> productComponentVersionList = productComponentVersionRepository.findAll();
        assertThat(productComponentVersionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
