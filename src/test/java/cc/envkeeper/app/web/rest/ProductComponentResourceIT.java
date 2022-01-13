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
import cc.envkeeper.app.domain.ProductComponent;
import cc.envkeeper.app.repository.ProductComponentRepository;
import cc.envkeeper.app.service.ProductComponentService;
import cc.envkeeper.app.service.dto.ProductComponentDTO;
import cc.envkeeper.app.service.mapper.ProductComponentMapper;
import cc.envkeeper.app.service.dto.ProductComponentCriteria;
import cc.envkeeper.app.service.ProductComponentQueryService;

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
 * Integration tests for the {@link ProductComponentResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class ProductComponentResourceIT {

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private ProductComponentRepository productComponentRepository;

    @Autowired
    private ProductComponentMapper productComponentMapper;

    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private ProductComponentQueryService productComponentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductComponentMockMvc;

    private ProductComponent productComponent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductComponent createEntity(EntityManager em) {
        ProductComponent productComponent = new ProductComponent()
            .shortName(DEFAULT_SHORT_NAME)
            .fullName(DEFAULT_FULL_NAME)
            .description(DEFAULT_DESCRIPTION);
        return productComponent;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductComponent createUpdatedEntity(EntityManager em) {
        ProductComponent productComponent = new ProductComponent()
            .shortName(UPDATED_SHORT_NAME)
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION);
        return productComponent;
    }

    @BeforeEach
    public void initTest() {
        productComponent = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductComponent() throws Exception {
        int databaseSizeBeforeCreate = productComponentRepository.findAll().size();
        // Create the ProductComponent
        ProductComponentDTO productComponentDTO = productComponentMapper.toDto(productComponent);
        restProductComponentMockMvc.perform(post("/api/product-components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductComponent in the database
        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeCreate + 1);
        ProductComponent testProductComponent = productComponentList.get(productComponentList.size() - 1);
        assertThat(testProductComponent.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
        assertThat(testProductComponent.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testProductComponent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createProductComponentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productComponentRepository.findAll().size();

        // Create the ProductComponent with an existing ID
        productComponent.setId(1L);
        ProductComponentDTO productComponentDTO = productComponentMapper.toDto(productComponent);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductComponentMockMvc.perform(post("/api/product-components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductComponent in the database
        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkShortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productComponentRepository.findAll().size();
        // set the field null
        productComponent.setShortName(null);

        // Create the ProductComponent, which fails.
        ProductComponentDTO productComponentDTO = productComponentMapper.toDto(productComponent);


        restProductComponentMockMvc.perform(post("/api/product-components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentDTO)))
            .andExpect(status().isBadRequest());

        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productComponentRepository.findAll().size();
        // set the field null
        productComponent.setFullName(null);

        // Create the ProductComponent, which fails.
        ProductComponentDTO productComponentDTO = productComponentMapper.toDto(productComponent);


        restProductComponentMockMvc.perform(post("/api/product-components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentDTO)))
            .andExpect(status().isBadRequest());

        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productComponentRepository.findAll().size();
        // set the field null
        productComponent.setDescription(null);

        // Create the ProductComponent, which fails.
        ProductComponentDTO productComponentDTO = productComponentMapper.toDto(productComponent);


        restProductComponentMockMvc.perform(post("/api/product-components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentDTO)))
            .andExpect(status().isBadRequest());

        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProductComponents() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList
        restProductComponentMockMvc.perform(get("/api/product-components?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void getProductComponent() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get the productComponent
        restProductComponentMockMvc.perform(get("/api/product-components/{id}", productComponent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productComponent.getId().intValue()))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }


    @Test
    @Transactional
    public void getProductComponentsByIdFiltering() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        Long id = productComponent.getId();

        defaultProductComponentShouldBeFound("id.equals=" + id);
        defaultProductComponentShouldNotBeFound("id.notEquals=" + id);

        defaultProductComponentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductComponentShouldNotBeFound("id.greaterThan=" + id);

        defaultProductComponentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductComponentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllProductComponentsByShortNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where shortName equals to DEFAULT_SHORT_NAME
        defaultProductComponentShouldBeFound("shortName.equals=" + DEFAULT_SHORT_NAME);

        // Get all the productComponentList where shortName equals to UPDATED_SHORT_NAME
        defaultProductComponentShouldNotBeFound("shortName.equals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByShortNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where shortName not equals to DEFAULT_SHORT_NAME
        defaultProductComponentShouldNotBeFound("shortName.notEquals=" + DEFAULT_SHORT_NAME);

        // Get all the productComponentList where shortName not equals to UPDATED_SHORT_NAME
        defaultProductComponentShouldBeFound("shortName.notEquals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByShortNameIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where shortName in DEFAULT_SHORT_NAME or UPDATED_SHORT_NAME
        defaultProductComponentShouldBeFound("shortName.in=" + DEFAULT_SHORT_NAME + "," + UPDATED_SHORT_NAME);

        // Get all the productComponentList where shortName equals to UPDATED_SHORT_NAME
        defaultProductComponentShouldNotBeFound("shortName.in=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByShortNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where shortName is not null
        defaultProductComponentShouldBeFound("shortName.specified=true");

        // Get all the productComponentList where shortName is null
        defaultProductComponentShouldNotBeFound("shortName.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductComponentsByShortNameContainsSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where shortName contains DEFAULT_SHORT_NAME
        defaultProductComponentShouldBeFound("shortName.contains=" + DEFAULT_SHORT_NAME);

        // Get all the productComponentList where shortName contains UPDATED_SHORT_NAME
        defaultProductComponentShouldNotBeFound("shortName.contains=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByShortNameNotContainsSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where shortName does not contain DEFAULT_SHORT_NAME
        defaultProductComponentShouldNotBeFound("shortName.doesNotContain=" + DEFAULT_SHORT_NAME);

        // Get all the productComponentList where shortName does not contain UPDATED_SHORT_NAME
        defaultProductComponentShouldBeFound("shortName.doesNotContain=" + UPDATED_SHORT_NAME);
    }


    @Test
    @Transactional
    public void getAllProductComponentsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where fullName equals to DEFAULT_FULL_NAME
        defaultProductComponentShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the productComponentList where fullName equals to UPDATED_FULL_NAME
        defaultProductComponentShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByFullNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where fullName not equals to DEFAULT_FULL_NAME
        defaultProductComponentShouldNotBeFound("fullName.notEquals=" + DEFAULT_FULL_NAME);

        // Get all the productComponentList where fullName not equals to UPDATED_FULL_NAME
        defaultProductComponentShouldBeFound("fullName.notEquals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultProductComponentShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the productComponentList where fullName equals to UPDATED_FULL_NAME
        defaultProductComponentShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where fullName is not null
        defaultProductComponentShouldBeFound("fullName.specified=true");

        // Get all the productComponentList where fullName is null
        defaultProductComponentShouldNotBeFound("fullName.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductComponentsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where fullName contains DEFAULT_FULL_NAME
        defaultProductComponentShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the productComponentList where fullName contains UPDATED_FULL_NAME
        defaultProductComponentShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where fullName does not contain DEFAULT_FULL_NAME
        defaultProductComponentShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the productComponentList where fullName does not contain UPDATED_FULL_NAME
        defaultProductComponentShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }


    @Test
    @Transactional
    public void getAllProductComponentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where description equals to DEFAULT_DESCRIPTION
        defaultProductComponentShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the productComponentList where description equals to UPDATED_DESCRIPTION
        defaultProductComponentShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where description not equals to DEFAULT_DESCRIPTION
        defaultProductComponentShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the productComponentList where description not equals to UPDATED_DESCRIPTION
        defaultProductComponentShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProductComponentShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the productComponentList where description equals to UPDATED_DESCRIPTION
        defaultProductComponentShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where description is not null
        defaultProductComponentShouldBeFound("description.specified=true");

        // Get all the productComponentList where description is null
        defaultProductComponentShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductComponentsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where description contains DEFAULT_DESCRIPTION
        defaultProductComponentShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the productComponentList where description contains UPDATED_DESCRIPTION
        defaultProductComponentShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductComponentsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        // Get all the productComponentList where description does not contain DEFAULT_DESCRIPTION
        defaultProductComponentShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the productComponentList where description does not contain UPDATED_DESCRIPTION
        defaultProductComponentShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductComponentShouldBeFound(String filter) throws Exception {
        restProductComponentMockMvc.perform(get("/api/product-components?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restProductComponentMockMvc.perform(get("/api/product-components/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductComponentShouldNotBeFound(String filter) throws Exception {
        restProductComponentMockMvc.perform(get("/api/product-components?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductComponentMockMvc.perform(get("/api/product-components/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingProductComponent() throws Exception {
        // Get the productComponent
        restProductComponentMockMvc.perform(get("/api/product-components/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductComponent() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        int databaseSizeBeforeUpdate = productComponentRepository.findAll().size();

        // Update the productComponent
        ProductComponent updatedProductComponent = productComponentRepository.findById(productComponent.getId()).get();
        // Disconnect from session so that the updates on updatedProductComponent are not directly saved in db
        em.detach(updatedProductComponent);
        updatedProductComponent
            .shortName(UPDATED_SHORT_NAME)
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION);
        ProductComponentDTO productComponentDTO = productComponentMapper.toDto(updatedProductComponent);

        restProductComponentMockMvc.perform(put("/api/product-components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentDTO)))
            .andExpect(status().isOk());

        // Validate the ProductComponent in the database
        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeUpdate);
        ProductComponent testProductComponent = productComponentList.get(productComponentList.size() - 1);
        assertThat(testProductComponent.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
        assertThat(testProductComponent.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testProductComponent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingProductComponent() throws Exception {
        int databaseSizeBeforeUpdate = productComponentRepository.findAll().size();

        // Create the ProductComponent
        ProductComponentDTO productComponentDTO = productComponentMapper.toDto(productComponent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductComponentMockMvc.perform(put("/api/product-components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productComponentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductComponent in the database
        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProductComponent() throws Exception {
        // Initialize the database
        productComponentRepository.saveAndFlush(productComponent);

        int databaseSizeBeforeDelete = productComponentRepository.findAll().size();

        // Delete the productComponent
        restProductComponentMockMvc.perform(delete("/api/product-components/{id}", productComponent.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductComponent> productComponentList = productComponentRepository.findAll();
        assertThat(productComponentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
