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
import cc.envkeeper.app.domain.Deployment;
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.domain.Environment;
import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.repository.DeploymentRepository;
import cc.envkeeper.app.service.DeploymentService;
import cc.envkeeper.app.service.dto.DeploymentDTO;
import cc.envkeeper.app.service.mapper.DeploymentMapper;
import cc.envkeeper.app.service.dto.DeploymentCriteria;
import cc.envkeeper.app.service.DeploymentQueryService;

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

import cc.envkeeper.app.domain.enumeration.DeploymentStatus;
/**
 * Integration tests for the {@link DeploymentResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class DeploymentResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_USER = "AAAAAAAAAA";
    private static final String UPDATED_USER = "BBBBBBBBBB";

    private static final DeploymentStatus DEFAULT_STATUS = DeploymentStatus.IN_PROGRESS;
    private static final DeploymentStatus UPDATED_STATUS = DeploymentStatus.DELETED;

    private static final String DEFAULT_NAMESPACE = "AAAAAAAAAA";
    private static final String UPDATED_NAMESPACE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_TEST_URL = "AAAAAAAAAA";
    private static final String UPDATED_TEST_URL = "BBBBBBBBBB";

    @Autowired
    private DeploymentRepository deploymentRepository;

    @Autowired
    private DeploymentMapper deploymentMapper;

    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private DeploymentQueryService deploymentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeploymentMockMvc;

    private Deployment deployment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deployment createEntity(EntityManager em) {
        Deployment deployment = new Deployment()
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .user(DEFAULT_USER)
            .status(DEFAULT_STATUS)
            .namespace(DEFAULT_NAMESPACE)
            .url(DEFAULT_URL)
            .testUrl(DEFAULT_TEST_URL);
        // Add required entity
        ProductVersion productVersion;
        if (TestUtil.findAll(em, ProductVersion.class).isEmpty()) {
            productVersion = ProductVersionResourceIT.createEntity(em);
            em.persist(productVersion);
            em.flush();
        } else {
            productVersion = TestUtil.findAll(em, ProductVersion.class).get(0);
        }
        deployment.setProductVersion(productVersion);
        // Add required entity
        Environment environment;
        if (TestUtil.findAll(em, Environment.class).isEmpty()) {
            environment = EnvironmentResourceIT.createEntity(em);
            em.persist(environment);
            em.flush();
        } else {
            environment = TestUtil.findAll(em, Environment.class).get(0);
        }
        deployment.setEnvironment(environment);
        return deployment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deployment createUpdatedEntity(EntityManager em) {
        Deployment deployment = new Deployment()
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .user(UPDATED_USER)
            .status(UPDATED_STATUS)
            .namespace(UPDATED_NAMESPACE)
            .url(UPDATED_URL)
            .testUrl(UPDATED_TEST_URL);
        // Add required entity
        ProductVersion productVersion;
        if (TestUtil.findAll(em, ProductVersion.class).isEmpty()) {
            productVersion = ProductVersionResourceIT.createUpdatedEntity(em);
            em.persist(productVersion);
            em.flush();
        } else {
            productVersion = TestUtil.findAll(em, ProductVersion.class).get(0);
        }
        deployment.setProductVersion(productVersion);
        // Add required entity
        Environment environment;
        if (TestUtil.findAll(em, Environment.class).isEmpty()) {
            environment = EnvironmentResourceIT.createUpdatedEntity(em);
            em.persist(environment);
            em.flush();
        } else {
            environment = TestUtil.findAll(em, Environment.class).get(0);
        }
        deployment.setEnvironment(environment);
        return deployment;
    }

    @BeforeEach
    public void initTest() {
        deployment = createEntity(em);
    }

    @Test
    @Transactional
    public void createDeployment() throws Exception {
        int databaseSizeBeforeCreate = deploymentRepository.findAll().size();
        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);
        restDeploymentMockMvc.perform(post("/api/deployments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(deploymentDTO)))
            .andExpect(status().isCreated());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeCreate + 1);
        Deployment testDeployment = deploymentList.get(deploymentList.size() - 1);
        assertThat(testDeployment.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testDeployment.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testDeployment.getUser()).isEqualTo(DEFAULT_USER);
        assertThat(testDeployment.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDeployment.getNamespace()).isEqualTo(DEFAULT_NAMESPACE);
        assertThat(testDeployment.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testDeployment.getTestUrl()).isEqualTo(DEFAULT_TEST_URL);
    }

    @Test
    @Transactional
    public void createDeploymentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = deploymentRepository.findAll().size();

        // Create the Deployment with an existing ID
        deployment.setId(1L);
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeploymentMockMvc.perform(post("/api/deployments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(deploymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = deploymentRepository.findAll().size();
        // set the field null
        deployment.setStartTime(null);

        // Create the Deployment, which fails.
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);


        restDeploymentMockMvc.perform(post("/api/deployments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(deploymentDTO)))
            .andExpect(status().isBadRequest());

        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = deploymentRepository.findAll().size();
        // set the field null
        deployment.setStatus(null);

        // Create the Deployment, which fails.
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);


        restDeploymentMockMvc.perform(post("/api/deployments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(deploymentDTO)))
            .andExpect(status().isBadRequest());

        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDeployments() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList
        restDeploymentMockMvc.perform(get("/api/deployments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deployment.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].namespace").value(hasItem(DEFAULT_NAMESPACE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].testUrl").value(hasItem(DEFAULT_TEST_URL)));
    }

    @Test
    @Transactional
    public void getDeployment() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get the deployment
        restDeploymentMockMvc.perform(get("/api/deployments/{id}", deployment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(deployment.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.user").value(DEFAULT_USER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.namespace").value(DEFAULT_NAMESPACE))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.testUrl").value(DEFAULT_TEST_URL));
    }


    @Test
    @Transactional
    public void getDeploymentsByIdFiltering() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        Long id = deployment.getId();

        defaultDeploymentShouldBeFound("id.equals=" + id);
        defaultDeploymentShouldNotBeFound("id.notEquals=" + id);

        defaultDeploymentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDeploymentShouldNotBeFound("id.greaterThan=" + id);

        defaultDeploymentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDeploymentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllDeploymentsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where startTime equals to DEFAULT_START_TIME
        defaultDeploymentShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the deploymentList where startTime equals to UPDATED_START_TIME
        defaultDeploymentShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByStartTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where startTime not equals to DEFAULT_START_TIME
        defaultDeploymentShouldNotBeFound("startTime.notEquals=" + DEFAULT_START_TIME);

        // Get all the deploymentList where startTime not equals to UPDATED_START_TIME
        defaultDeploymentShouldBeFound("startTime.notEquals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where startTime in DEFAULT_START_TIME or UPDATED_START_TIME
        defaultDeploymentShouldBeFound("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME);

        // Get all the deploymentList where startTime equals to UPDATED_START_TIME
        defaultDeploymentShouldNotBeFound("startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where startTime is not null
        defaultDeploymentShouldBeFound("startTime.specified=true");

        // Get all the deploymentList where startTime is null
        defaultDeploymentShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllDeploymentsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where endTime equals to DEFAULT_END_TIME
        defaultDeploymentShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the deploymentList where endTime equals to UPDATED_END_TIME
        defaultDeploymentShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByEndTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where endTime not equals to DEFAULT_END_TIME
        defaultDeploymentShouldNotBeFound("endTime.notEquals=" + DEFAULT_END_TIME);

        // Get all the deploymentList where endTime not equals to UPDATED_END_TIME
        defaultDeploymentShouldBeFound("endTime.notEquals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where endTime in DEFAULT_END_TIME or UPDATED_END_TIME
        defaultDeploymentShouldBeFound("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME);

        // Get all the deploymentList where endTime equals to UPDATED_END_TIME
        defaultDeploymentShouldNotBeFound("endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where endTime is not null
        defaultDeploymentShouldBeFound("endTime.specified=true");

        // Get all the deploymentList where endTime is null
        defaultDeploymentShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where user equals to DEFAULT_USER
        defaultDeploymentShouldBeFound("user.equals=" + DEFAULT_USER);

        // Get all the deploymentList where user equals to UPDATED_USER
        defaultDeploymentShouldNotBeFound("user.equals=" + UPDATED_USER);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUserIsNotEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where user not equals to DEFAULT_USER
        defaultDeploymentShouldNotBeFound("user.notEquals=" + DEFAULT_USER);

        // Get all the deploymentList where user not equals to UPDATED_USER
        defaultDeploymentShouldBeFound("user.notEquals=" + UPDATED_USER);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUserIsInShouldWork() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where user in DEFAULT_USER or UPDATED_USER
        defaultDeploymentShouldBeFound("user.in=" + DEFAULT_USER + "," + UPDATED_USER);

        // Get all the deploymentList where user equals to UPDATED_USER
        defaultDeploymentShouldNotBeFound("user.in=" + UPDATED_USER);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUserIsNullOrNotNull() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where user is not null
        defaultDeploymentShouldBeFound("user.specified=true");

        // Get all the deploymentList where user is null
        defaultDeploymentShouldNotBeFound("user.specified=false");
    }
                @Test
    @Transactional
    public void getAllDeploymentsByUserContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where user contains DEFAULT_USER
        defaultDeploymentShouldBeFound("user.contains=" + DEFAULT_USER);

        // Get all the deploymentList where user contains UPDATED_USER
        defaultDeploymentShouldNotBeFound("user.contains=" + UPDATED_USER);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUserNotContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where user does not contain DEFAULT_USER
        defaultDeploymentShouldNotBeFound("user.doesNotContain=" + DEFAULT_USER);

        // Get all the deploymentList where user does not contain UPDATED_USER
        defaultDeploymentShouldBeFound("user.doesNotContain=" + UPDATED_USER);
    }


    @Test
    @Transactional
    public void getAllDeploymentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where status equals to DEFAULT_STATUS
        defaultDeploymentShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the deploymentList where status equals to UPDATED_STATUS
        defaultDeploymentShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where status not equals to DEFAULT_STATUS
        defaultDeploymentShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the deploymentList where status not equals to UPDATED_STATUS
        defaultDeploymentShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultDeploymentShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the deploymentList where status equals to UPDATED_STATUS
        defaultDeploymentShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where status is not null
        defaultDeploymentShouldBeFound("status.specified=true");

        // Get all the deploymentList where status is null
        defaultDeploymentShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllDeploymentsByNamespaceIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where namespace equals to DEFAULT_NAMESPACE
        defaultDeploymentShouldBeFound("namespace.equals=" + DEFAULT_NAMESPACE);

        // Get all the deploymentList where namespace equals to UPDATED_NAMESPACE
        defaultDeploymentShouldNotBeFound("namespace.equals=" + UPDATED_NAMESPACE);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByNamespaceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where namespace not equals to DEFAULT_NAMESPACE
        defaultDeploymentShouldNotBeFound("namespace.notEquals=" + DEFAULT_NAMESPACE);

        // Get all the deploymentList where namespace not equals to UPDATED_NAMESPACE
        defaultDeploymentShouldBeFound("namespace.notEquals=" + UPDATED_NAMESPACE);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByNamespaceIsInShouldWork() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where namespace in DEFAULT_NAMESPACE or UPDATED_NAMESPACE
        defaultDeploymentShouldBeFound("namespace.in=" + DEFAULT_NAMESPACE + "," + UPDATED_NAMESPACE);

        // Get all the deploymentList where namespace equals to UPDATED_NAMESPACE
        defaultDeploymentShouldNotBeFound("namespace.in=" + UPDATED_NAMESPACE);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByNamespaceIsNullOrNotNull() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where namespace is not null
        defaultDeploymentShouldBeFound("namespace.specified=true");

        // Get all the deploymentList where namespace is null
        defaultDeploymentShouldNotBeFound("namespace.specified=false");
    }
                @Test
    @Transactional
    public void getAllDeploymentsByNamespaceContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where namespace contains DEFAULT_NAMESPACE
        defaultDeploymentShouldBeFound("namespace.contains=" + DEFAULT_NAMESPACE);

        // Get all the deploymentList where namespace contains UPDATED_NAMESPACE
        defaultDeploymentShouldNotBeFound("namespace.contains=" + UPDATED_NAMESPACE);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByNamespaceNotContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where namespace does not contain DEFAULT_NAMESPACE
        defaultDeploymentShouldNotBeFound("namespace.doesNotContain=" + DEFAULT_NAMESPACE);

        // Get all the deploymentList where namespace does not contain UPDATED_NAMESPACE
        defaultDeploymentShouldBeFound("namespace.doesNotContain=" + UPDATED_NAMESPACE);
    }


    @Test
    @Transactional
    public void getAllDeploymentsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where url equals to DEFAULT_URL
        defaultDeploymentShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the deploymentList where url equals to UPDATED_URL
        defaultDeploymentShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where url not equals to DEFAULT_URL
        defaultDeploymentShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the deploymentList where url not equals to UPDATED_URL
        defaultDeploymentShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where url in DEFAULT_URL or UPDATED_URL
        defaultDeploymentShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the deploymentList where url equals to UPDATED_URL
        defaultDeploymentShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where url is not null
        defaultDeploymentShouldBeFound("url.specified=true");

        // Get all the deploymentList where url is null
        defaultDeploymentShouldNotBeFound("url.specified=false");
    }
                @Test
    @Transactional
    public void getAllDeploymentsByUrlContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where url contains DEFAULT_URL
        defaultDeploymentShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the deploymentList where url contains UPDATED_URL
        defaultDeploymentShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where url does not contain DEFAULT_URL
        defaultDeploymentShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the deploymentList where url does not contain UPDATED_URL
        defaultDeploymentShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }


    @Test
    @Transactional
    public void getAllDeploymentsByTestUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where testUrl equals to DEFAULT_TEST_URL
        defaultDeploymentShouldBeFound("testUrl.equals=" + DEFAULT_TEST_URL);

        // Get all the deploymentList where testUrl equals to UPDATED_TEST_URL
        defaultDeploymentShouldNotBeFound("testUrl.equals=" + UPDATED_TEST_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByTestUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where testUrl not equals to DEFAULT_TEST_URL
        defaultDeploymentShouldNotBeFound("testUrl.notEquals=" + DEFAULT_TEST_URL);

        // Get all the deploymentList where testUrl not equals to UPDATED_TEST_URL
        defaultDeploymentShouldBeFound("testUrl.notEquals=" + UPDATED_TEST_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByTestUrlIsInShouldWork() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where testUrl in DEFAULT_TEST_URL or UPDATED_TEST_URL
        defaultDeploymentShouldBeFound("testUrl.in=" + DEFAULT_TEST_URL + "," + UPDATED_TEST_URL);

        // Get all the deploymentList where testUrl equals to UPDATED_TEST_URL
        defaultDeploymentShouldNotBeFound("testUrl.in=" + UPDATED_TEST_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByTestUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where testUrl is not null
        defaultDeploymentShouldBeFound("testUrl.specified=true");

        // Get all the deploymentList where testUrl is null
        defaultDeploymentShouldNotBeFound("testUrl.specified=false");
    }
                @Test
    @Transactional
    public void getAllDeploymentsByTestUrlContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where testUrl contains DEFAULT_TEST_URL
        defaultDeploymentShouldBeFound("testUrl.contains=" + DEFAULT_TEST_URL);

        // Get all the deploymentList where testUrl contains UPDATED_TEST_URL
        defaultDeploymentShouldNotBeFound("testUrl.contains=" + UPDATED_TEST_URL);
    }

    @Test
    @Transactional
    public void getAllDeploymentsByTestUrlNotContainsSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where testUrl does not contain DEFAULT_TEST_URL
        defaultDeploymentShouldNotBeFound("testUrl.doesNotContain=" + DEFAULT_TEST_URL);

        // Get all the deploymentList where testUrl does not contain UPDATED_TEST_URL
        defaultDeploymentShouldBeFound("testUrl.doesNotContain=" + UPDATED_TEST_URL);
    }


    @Test
    @Transactional
    public void getAllDeploymentsByProductVersionIsEqualToSomething() throws Exception {
        // Get already existing entity
        ProductVersion productVersion = deployment.getProductVersion();
        deploymentRepository.saveAndFlush(deployment);
        Long productVersionId = productVersion.getId();

        // Get all the deploymentList where productVersion equals to productVersionId
        defaultDeploymentShouldBeFound("productVersionId.equals=" + productVersionId);

        // Get all the deploymentList where productVersion equals to productVersionId + 1
        defaultDeploymentShouldNotBeFound("productVersionId.equals=" + (productVersionId + 1));
    }


    @Test
    @Transactional
    public void getAllDeploymentsByEnvironmentIsEqualToSomething() throws Exception {
        // Get already existing entity
        Environment environment = deployment.getEnvironment();
        deploymentRepository.saveAndFlush(deployment);
        Long environmentId = environment.getId();

        // Get all the deploymentList where environment equals to environmentId
        defaultDeploymentShouldBeFound("environmentId.equals=" + environmentId);

        // Get all the deploymentList where environment equals to environmentId + 1
        defaultDeploymentShouldNotBeFound("environmentId.equals=" + (environmentId + 1));
    }


    @Test
    @Transactional
    public void getAllDeploymentsByBuildIsEqualToSomething() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);
        Build build = BuildResourceIT.createEntity(em);
        em.persist(build);
        em.flush();
        deployment.setBuild(build);
        deploymentRepository.saveAndFlush(deployment);
        Long buildId = build.getId();

        // Get all the deploymentList where build equals to buildId
        defaultDeploymentShouldBeFound("buildId.equals=" + buildId);

        // Get all the deploymentList where build equals to buildId + 1
        defaultDeploymentShouldNotBeFound("buildId.equals=" + (buildId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeploymentShouldBeFound(String filter) throws Exception {
        restDeploymentMockMvc.perform(get("/api/deployments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deployment.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].namespace").value(hasItem(DEFAULT_NAMESPACE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].testUrl").value(hasItem(DEFAULT_TEST_URL)));

        // Check, that the count call also returns 1
        restDeploymentMockMvc.perform(get("/api/deployments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeploymentShouldNotBeFound(String filter) throws Exception {
        restDeploymentMockMvc.perform(get("/api/deployments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeploymentMockMvc.perform(get("/api/deployments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingDeployment() throws Exception {
        // Get the deployment
        restDeploymentMockMvc.perform(get("/api/deployments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeployment() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        int databaseSizeBeforeUpdate = deploymentRepository.findAll().size();

        // Update the deployment
        Deployment updatedDeployment = deploymentRepository.findById(deployment.getId()).get();
        // Disconnect from session so that the updates on updatedDeployment are not directly saved in db
        em.detach(updatedDeployment);
        updatedDeployment
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .user(UPDATED_USER)
            .status(UPDATED_STATUS)
            .namespace(UPDATED_NAMESPACE)
            .url(UPDATED_URL)
            .testUrl(UPDATED_TEST_URL);
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(updatedDeployment);

        restDeploymentMockMvc.perform(put("/api/deployments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(deploymentDTO)))
            .andExpect(status().isOk());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeUpdate);
        Deployment testDeployment = deploymentList.get(deploymentList.size() - 1);
        assertThat(testDeployment.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testDeployment.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testDeployment.getUser()).isEqualTo(UPDATED_USER);
        assertThat(testDeployment.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDeployment.getNamespace()).isEqualTo(UPDATED_NAMESPACE);
        assertThat(testDeployment.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testDeployment.getTestUrl()).isEqualTo(UPDATED_TEST_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingDeployment() throws Exception {
        int databaseSizeBeforeUpdate = deploymentRepository.findAll().size();

        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeploymentMockMvc.perform(put("/api/deployments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(deploymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDeployment() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        int databaseSizeBeforeDelete = deploymentRepository.findAll().size();

        // Delete the deployment
        restDeploymentMockMvc.perform(delete("/api/deployments/{id}", deployment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
