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
import cc.envkeeper.app.domain.EnvironmentGroup;
import cc.envkeeper.app.repository.EnvironmentGroupRepository;
import cc.envkeeper.app.service.EnvironmentGroupService;
import cc.envkeeper.app.service.dto.EnvironmentGroupDTO;
import cc.envkeeper.app.service.mapper.EnvironmentGroupMapper;
import cc.envkeeper.app.service.dto.EnvironmentGroupCriteria;
import cc.envkeeper.app.service.EnvironmentGroupQueryService;

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
 * Integration tests for the {@link EnvironmentGroupResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class EnvironmentGroupResourceIT {

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;
    private static final Integer SMALLER_SORT_ORDER = 1 - 1;

    private static final Boolean DEFAULT_HIDDEN = false;
    private static final Boolean UPDATED_HIDDEN = true;

    @Autowired
    private EnvironmentGroupRepository environmentGroupRepository;

    @Autowired
    private EnvironmentGroupMapper environmentGroupMapper;

    @Autowired
    private EnvironmentGroupService environmentGroupService;

    @Autowired
    private EnvironmentGroupQueryService environmentGroupQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvironmentGroupMockMvc;

    private EnvironmentGroup environmentGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentGroup createEntity(EntityManager em) {
        EnvironmentGroup environmentGroup = new EnvironmentGroup()
            .shortName(DEFAULT_SHORT_NAME)
            .fullName(DEFAULT_FULL_NAME)
            .description(DEFAULT_DESCRIPTION)
            .sortOrder(DEFAULT_SORT_ORDER)
            .hidden(DEFAULT_HIDDEN);
        return environmentGroup;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentGroup createUpdatedEntity(EntityManager em) {
        EnvironmentGroup environmentGroup = new EnvironmentGroup()
            .shortName(UPDATED_SHORT_NAME)
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .hidden(UPDATED_HIDDEN);
        return environmentGroup;
    }

    @BeforeEach
    public void initTest() {
        environmentGroup = createEntity(em);
    }

    @Test
    @Transactional
    public void createEnvironmentGroup() throws Exception {
        int databaseSizeBeforeCreate = environmentGroupRepository.findAll().size();
        // Create the EnvironmentGroup
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(environmentGroup);
        restEnvironmentGroupMockMvc.perform(post("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isCreated());

        // Validate the EnvironmentGroup in the database
        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeCreate + 1);
        EnvironmentGroup testEnvironmentGroup = environmentGroupList.get(environmentGroupList.size() - 1);
        assertThat(testEnvironmentGroup.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
        assertThat(testEnvironmentGroup.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testEnvironmentGroup.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEnvironmentGroup.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testEnvironmentGroup.isHidden()).isEqualTo(DEFAULT_HIDDEN);
    }

    @Test
    @Transactional
    public void createEnvironmentGroupWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = environmentGroupRepository.findAll().size();

        // Create the EnvironmentGroup with an existing ID
        environmentGroup.setId(1L);
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(environmentGroup);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvironmentGroupMockMvc.perform(post("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentGroup in the database
        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkShortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentGroupRepository.findAll().size();
        // set the field null
        environmentGroup.setShortName(null);

        // Create the EnvironmentGroup, which fails.
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(environmentGroup);


        restEnvironmentGroupMockMvc.perform(post("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isBadRequest());

        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentGroupRepository.findAll().size();
        // set the field null
        environmentGroup.setFullName(null);

        // Create the EnvironmentGroup, which fails.
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(environmentGroup);


        restEnvironmentGroupMockMvc.perform(post("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isBadRequest());

        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentGroupRepository.findAll().size();
        // set the field null
        environmentGroup.setDescription(null);

        // Create the EnvironmentGroup, which fails.
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(environmentGroup);


        restEnvironmentGroupMockMvc.perform(post("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isBadRequest());

        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHiddenIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentGroupRepository.findAll().size();
        // set the field null
        environmentGroup.setHidden(null);

        // Create the EnvironmentGroup, which fails.
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(environmentGroup);


        restEnvironmentGroupMockMvc.perform(post("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isBadRequest());

        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroups() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList
        restEnvironmentGroupMockMvc.perform(get("/api/environment-groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environmentGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].hidden").value(hasItem(DEFAULT_HIDDEN.booleanValue())));
    }

    @Test
    @Transactional
    public void getEnvironmentGroup() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get the environmentGroup
        restEnvironmentGroupMockMvc.perform(get("/api/environment-groups/{id}", environmentGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(environmentGroup.getId().intValue()))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER))
            .andExpect(jsonPath("$.hidden").value(DEFAULT_HIDDEN.booleanValue()));
    }


    @Test
    @Transactional
    public void getEnvironmentGroupsByIdFiltering() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        Long id = environmentGroup.getId();

        defaultEnvironmentGroupShouldBeFound("id.equals=" + id);
        defaultEnvironmentGroupShouldNotBeFound("id.notEquals=" + id);

        defaultEnvironmentGroupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEnvironmentGroupShouldNotBeFound("id.greaterThan=" + id);

        defaultEnvironmentGroupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEnvironmentGroupShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllEnvironmentGroupsByShortNameIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where shortName equals to DEFAULT_SHORT_NAME
        defaultEnvironmentGroupShouldBeFound("shortName.equals=" + DEFAULT_SHORT_NAME);

        // Get all the environmentGroupList where shortName equals to UPDATED_SHORT_NAME
        defaultEnvironmentGroupShouldNotBeFound("shortName.equals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByShortNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where shortName not equals to DEFAULT_SHORT_NAME
        defaultEnvironmentGroupShouldNotBeFound("shortName.notEquals=" + DEFAULT_SHORT_NAME);

        // Get all the environmentGroupList where shortName not equals to UPDATED_SHORT_NAME
        defaultEnvironmentGroupShouldBeFound("shortName.notEquals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByShortNameIsInShouldWork() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where shortName in DEFAULT_SHORT_NAME or UPDATED_SHORT_NAME
        defaultEnvironmentGroupShouldBeFound("shortName.in=" + DEFAULT_SHORT_NAME + "," + UPDATED_SHORT_NAME);

        // Get all the environmentGroupList where shortName equals to UPDATED_SHORT_NAME
        defaultEnvironmentGroupShouldNotBeFound("shortName.in=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByShortNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where shortName is not null
        defaultEnvironmentGroupShouldBeFound("shortName.specified=true");

        // Get all the environmentGroupList where shortName is null
        defaultEnvironmentGroupShouldNotBeFound("shortName.specified=false");
    }
                @Test
    @Transactional
    public void getAllEnvironmentGroupsByShortNameContainsSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where shortName contains DEFAULT_SHORT_NAME
        defaultEnvironmentGroupShouldBeFound("shortName.contains=" + DEFAULT_SHORT_NAME);

        // Get all the environmentGroupList where shortName contains UPDATED_SHORT_NAME
        defaultEnvironmentGroupShouldNotBeFound("shortName.contains=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByShortNameNotContainsSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where shortName does not contain DEFAULT_SHORT_NAME
        defaultEnvironmentGroupShouldNotBeFound("shortName.doesNotContain=" + DEFAULT_SHORT_NAME);

        // Get all the environmentGroupList where shortName does not contain UPDATED_SHORT_NAME
        defaultEnvironmentGroupShouldBeFound("shortName.doesNotContain=" + UPDATED_SHORT_NAME);
    }


    @Test
    @Transactional
    public void getAllEnvironmentGroupsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where fullName equals to DEFAULT_FULL_NAME
        defaultEnvironmentGroupShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the environmentGroupList where fullName equals to UPDATED_FULL_NAME
        defaultEnvironmentGroupShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByFullNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where fullName not equals to DEFAULT_FULL_NAME
        defaultEnvironmentGroupShouldNotBeFound("fullName.notEquals=" + DEFAULT_FULL_NAME);

        // Get all the environmentGroupList where fullName not equals to UPDATED_FULL_NAME
        defaultEnvironmentGroupShouldBeFound("fullName.notEquals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultEnvironmentGroupShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the environmentGroupList where fullName equals to UPDATED_FULL_NAME
        defaultEnvironmentGroupShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where fullName is not null
        defaultEnvironmentGroupShouldBeFound("fullName.specified=true");

        // Get all the environmentGroupList where fullName is null
        defaultEnvironmentGroupShouldNotBeFound("fullName.specified=false");
    }
                @Test
    @Transactional
    public void getAllEnvironmentGroupsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where fullName contains DEFAULT_FULL_NAME
        defaultEnvironmentGroupShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the environmentGroupList where fullName contains UPDATED_FULL_NAME
        defaultEnvironmentGroupShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where fullName does not contain DEFAULT_FULL_NAME
        defaultEnvironmentGroupShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the environmentGroupList where fullName does not contain UPDATED_FULL_NAME
        defaultEnvironmentGroupShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }


    @Test
    @Transactional
    public void getAllEnvironmentGroupsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where description equals to DEFAULT_DESCRIPTION
        defaultEnvironmentGroupShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the environmentGroupList where description equals to UPDATED_DESCRIPTION
        defaultEnvironmentGroupShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where description not equals to DEFAULT_DESCRIPTION
        defaultEnvironmentGroupShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the environmentGroupList where description not equals to UPDATED_DESCRIPTION
        defaultEnvironmentGroupShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultEnvironmentGroupShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the environmentGroupList where description equals to UPDATED_DESCRIPTION
        defaultEnvironmentGroupShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where description is not null
        defaultEnvironmentGroupShouldBeFound("description.specified=true");

        // Get all the environmentGroupList where description is null
        defaultEnvironmentGroupShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllEnvironmentGroupsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where description contains DEFAULT_DESCRIPTION
        defaultEnvironmentGroupShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the environmentGroupList where description contains UPDATED_DESCRIPTION
        defaultEnvironmentGroupShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where description does not contain DEFAULT_DESCRIPTION
        defaultEnvironmentGroupShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the environmentGroupList where description does not contain UPDATED_DESCRIPTION
        defaultEnvironmentGroupShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder equals to DEFAULT_SORT_ORDER
        defaultEnvironmentGroupShouldBeFound("sortOrder.equals=" + DEFAULT_SORT_ORDER);

        // Get all the environmentGroupList where sortOrder equals to UPDATED_SORT_ORDER
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.equals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder not equals to DEFAULT_SORT_ORDER
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.notEquals=" + DEFAULT_SORT_ORDER);

        // Get all the environmentGroupList where sortOrder not equals to UPDATED_SORT_ORDER
        defaultEnvironmentGroupShouldBeFound("sortOrder.notEquals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsInShouldWork() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder in DEFAULT_SORT_ORDER or UPDATED_SORT_ORDER
        defaultEnvironmentGroupShouldBeFound("sortOrder.in=" + DEFAULT_SORT_ORDER + "," + UPDATED_SORT_ORDER);

        // Get all the environmentGroupList where sortOrder equals to UPDATED_SORT_ORDER
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.in=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder is not null
        defaultEnvironmentGroupShouldBeFound("sortOrder.specified=true");

        // Get all the environmentGroupList where sortOrder is null
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.specified=false");
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder is greater than or equal to DEFAULT_SORT_ORDER
        defaultEnvironmentGroupShouldBeFound("sortOrder.greaterThanOrEqual=" + DEFAULT_SORT_ORDER);

        // Get all the environmentGroupList where sortOrder is greater than or equal to UPDATED_SORT_ORDER
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.greaterThanOrEqual=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder is less than or equal to DEFAULT_SORT_ORDER
        defaultEnvironmentGroupShouldBeFound("sortOrder.lessThanOrEqual=" + DEFAULT_SORT_ORDER);

        // Get all the environmentGroupList where sortOrder is less than or equal to SMALLER_SORT_ORDER
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.lessThanOrEqual=" + SMALLER_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder is less than DEFAULT_SORT_ORDER
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.lessThan=" + DEFAULT_SORT_ORDER);

        // Get all the environmentGroupList where sortOrder is less than UPDATED_SORT_ORDER
        defaultEnvironmentGroupShouldBeFound("sortOrder.lessThan=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsBySortOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where sortOrder is greater than DEFAULT_SORT_ORDER
        defaultEnvironmentGroupShouldNotBeFound("sortOrder.greaterThan=" + DEFAULT_SORT_ORDER);

        // Get all the environmentGroupList where sortOrder is greater than SMALLER_SORT_ORDER
        defaultEnvironmentGroupShouldBeFound("sortOrder.greaterThan=" + SMALLER_SORT_ORDER);
    }


    @Test
    @Transactional
    public void getAllEnvironmentGroupsByHiddenIsEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where hidden equals to DEFAULT_HIDDEN
        defaultEnvironmentGroupShouldBeFound("hidden.equals=" + DEFAULT_HIDDEN);

        // Get all the environmentGroupList where hidden equals to UPDATED_HIDDEN
        defaultEnvironmentGroupShouldNotBeFound("hidden.equals=" + UPDATED_HIDDEN);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByHiddenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where hidden not equals to DEFAULT_HIDDEN
        defaultEnvironmentGroupShouldNotBeFound("hidden.notEquals=" + DEFAULT_HIDDEN);

        // Get all the environmentGroupList where hidden not equals to UPDATED_HIDDEN
        defaultEnvironmentGroupShouldBeFound("hidden.notEquals=" + UPDATED_HIDDEN);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByHiddenIsInShouldWork() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where hidden in DEFAULT_HIDDEN or UPDATED_HIDDEN
        defaultEnvironmentGroupShouldBeFound("hidden.in=" + DEFAULT_HIDDEN + "," + UPDATED_HIDDEN);

        // Get all the environmentGroupList where hidden equals to UPDATED_HIDDEN
        defaultEnvironmentGroupShouldNotBeFound("hidden.in=" + UPDATED_HIDDEN);
    }

    @Test
    @Transactional
    public void getAllEnvironmentGroupsByHiddenIsNullOrNotNull() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        // Get all the environmentGroupList where hidden is not null
        defaultEnvironmentGroupShouldBeFound("hidden.specified=true");

        // Get all the environmentGroupList where hidden is null
        defaultEnvironmentGroupShouldNotBeFound("hidden.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnvironmentGroupShouldBeFound(String filter) throws Exception {
        restEnvironmentGroupMockMvc.perform(get("/api/environment-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environmentGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].hidden").value(hasItem(DEFAULT_HIDDEN.booleanValue())));

        // Check, that the count call also returns 1
        restEnvironmentGroupMockMvc.perform(get("/api/environment-groups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnvironmentGroupShouldNotBeFound(String filter) throws Exception {
        restEnvironmentGroupMockMvc.perform(get("/api/environment-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnvironmentGroupMockMvc.perform(get("/api/environment-groups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingEnvironmentGroup() throws Exception {
        // Get the environmentGroup
        restEnvironmentGroupMockMvc.perform(get("/api/environment-groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEnvironmentGroup() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        int databaseSizeBeforeUpdate = environmentGroupRepository.findAll().size();

        // Update the environmentGroup
        EnvironmentGroup updatedEnvironmentGroup = environmentGroupRepository.findById(environmentGroup.getId()).get();
        // Disconnect from session so that the updates on updatedEnvironmentGroup are not directly saved in db
        em.detach(updatedEnvironmentGroup);
        updatedEnvironmentGroup
            .shortName(UPDATED_SHORT_NAME)
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .hidden(UPDATED_HIDDEN);
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(updatedEnvironmentGroup);

        restEnvironmentGroupMockMvc.perform(put("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isOk());

        // Validate the EnvironmentGroup in the database
        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeUpdate);
        EnvironmentGroup testEnvironmentGroup = environmentGroupList.get(environmentGroupList.size() - 1);
        assertThat(testEnvironmentGroup.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
        assertThat(testEnvironmentGroup.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testEnvironmentGroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEnvironmentGroup.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testEnvironmentGroup.isHidden()).isEqualTo(UPDATED_HIDDEN);
    }

    @Test
    @Transactional
    public void updateNonExistingEnvironmentGroup() throws Exception {
        int databaseSizeBeforeUpdate = environmentGroupRepository.findAll().size();

        // Create the EnvironmentGroup
        EnvironmentGroupDTO environmentGroupDTO = environmentGroupMapper.toDto(environmentGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentGroupMockMvc.perform(put("/api/environment-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(environmentGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentGroup in the database
        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEnvironmentGroup() throws Exception {
        // Initialize the database
        environmentGroupRepository.saveAndFlush(environmentGroup);

        int databaseSizeBeforeDelete = environmentGroupRepository.findAll().size();

        // Delete the environmentGroup
        restEnvironmentGroupMockMvc.perform(delete("/api/environment-groups/{id}", environmentGroup.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EnvironmentGroup> environmentGroupList = environmentGroupRepository.findAll();
        assertThat(environmentGroupList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
