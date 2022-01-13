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
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.domain.Product;
import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.domain.ProductComponentVersion;
import cc.envkeeper.app.repository.ProductVersionRepository;
import cc.envkeeper.app.service.ProductVersionService;
import cc.envkeeper.app.service.dto.ProductVersionDTO;
import cc.envkeeper.app.service.mapper.ProductVersionMapper;
import cc.envkeeper.app.service.dto.ProductVersionCriteria;
import cc.envkeeper.app.service.ProductVersionQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProductVersionResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class ProductVersionResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_RELEASE_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_RELEASE_NOTES = "BBBBBBBBBB";

    @Autowired
    private ProductVersionRepository productVersionRepository;

    @Mock
    private ProductVersionRepository productVersionRepositoryMock;

    @Autowired
    private ProductVersionMapper productVersionMapper;

    @Mock
    private ProductVersionService productVersionServiceMock;

    @Autowired
    private ProductVersionService productVersionService;

    @Autowired
    private ProductVersionQueryService productVersionQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductVersionMockMvc;

    private ProductVersion productVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVersion createEntity(EntityManager em) {
        ProductVersion productVersion = new ProductVersion()
            .version(DEFAULT_VERSION)
            .releaseNotes(DEFAULT_RELEASE_NOTES);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        productVersion.setProduct(product);
        return productVersion;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVersion createUpdatedEntity(EntityManager em) {
        ProductVersion productVersion = new ProductVersion()
            .version(UPDATED_VERSION)
            .releaseNotes(UPDATED_RELEASE_NOTES);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        productVersion.setProduct(product);
        return productVersion;
    }

    @BeforeEach
    public void initTest() {
        productVersion = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductVersion() throws Exception {
        int databaseSizeBeforeCreate = productVersionRepository.findAll().size();
        // Create the ProductVersion
        ProductVersionDTO productVersionDTO = productVersionMapper.toDto(productVersion);
        restProductVersionMockMvc.perform(post("/api/product-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productVersionDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductVersion in the database
        List<ProductVersion> productVersionList = productVersionRepository.findAll();
        assertThat(productVersionList).hasSize(databaseSizeBeforeCreate + 1);
        ProductVersion testProductVersion = productVersionList.get(productVersionList.size() - 1);
        assertThat(testProductVersion.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testProductVersion.getReleaseNotes()).isEqualTo(DEFAULT_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void createProductVersionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productVersionRepository.findAll().size();

        // Create the ProductVersion with an existing ID
        productVersion.setId(1L);
        ProductVersionDTO productVersionDTO = productVersionMapper.toDto(productVersion);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductVersionMockMvc.perform(post("/api/product-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productVersionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductVersion in the database
        List<ProductVersion> productVersionList = productVersionRepository.findAll();
        assertThat(productVersionList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVersionRepository.findAll().size();
        // set the field null
        productVersion.setVersion(null);

        // Create the ProductVersion, which fails.
        ProductVersionDTO productVersionDTO = productVersionMapper.toDto(productVersion);


        restProductVersionMockMvc.perform(post("/api/product-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productVersionDTO)))
            .andExpect(status().isBadRequest());

        List<ProductVersion> productVersionList = productVersionRepository.findAll();
        assertThat(productVersionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProductVersions() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList
        restProductVersionMockMvc.perform(get("/api/product-versions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].releaseNotes").value(hasItem(DEFAULT_RELEASE_NOTES)));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllProductVersionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productVersionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVersionMockMvc.perform(get("/api/product-versions?eagerload=true"))
            .andExpect(status().isOk());

        verify(productVersionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllProductVersionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productVersionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVersionMockMvc.perform(get("/api/product-versions?eagerload=true"))
            .andExpect(status().isOk());

        verify(productVersionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getProductVersion() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get the productVersion
        restProductVersionMockMvc.perform(get("/api/product-versions/{id}", productVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productVersion.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.releaseNotes").value(DEFAULT_RELEASE_NOTES));
    }


    @Test
    @Transactional
    public void getProductVersionsByIdFiltering() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        Long id = productVersion.getId();

        defaultProductVersionShouldBeFound("id.equals=" + id);
        defaultProductVersionShouldNotBeFound("id.notEquals=" + id);

        defaultProductVersionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductVersionShouldNotBeFound("id.greaterThan=" + id);

        defaultProductVersionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductVersionShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllProductVersionsByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where version equals to DEFAULT_VERSION
        defaultProductVersionShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the productVersionList where version equals to UPDATED_VERSION
        defaultProductVersionShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where version not equals to DEFAULT_VERSION
        defaultProductVersionShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the productVersionList where version not equals to UPDATED_VERSION
        defaultProductVersionShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultProductVersionShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the productVersionList where version equals to UPDATED_VERSION
        defaultProductVersionShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where version is not null
        defaultProductVersionShouldBeFound("version.specified=true");

        // Get all the productVersionList where version is null
        defaultProductVersionShouldNotBeFound("version.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductVersionsByVersionContainsSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where version contains DEFAULT_VERSION
        defaultProductVersionShouldBeFound("version.contains=" + DEFAULT_VERSION);

        // Get all the productVersionList where version contains UPDATED_VERSION
        defaultProductVersionShouldNotBeFound("version.contains=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByVersionNotContainsSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where version does not contain DEFAULT_VERSION
        defaultProductVersionShouldNotBeFound("version.doesNotContain=" + DEFAULT_VERSION);

        // Get all the productVersionList where version does not contain UPDATED_VERSION
        defaultProductVersionShouldBeFound("version.doesNotContain=" + UPDATED_VERSION);
    }


    @Test
    @Transactional
    public void getAllProductVersionsByReleaseNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where releaseNotes equals to DEFAULT_RELEASE_NOTES
        defaultProductVersionShouldBeFound("releaseNotes.equals=" + DEFAULT_RELEASE_NOTES);

        // Get all the productVersionList where releaseNotes equals to UPDATED_RELEASE_NOTES
        defaultProductVersionShouldNotBeFound("releaseNotes.equals=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByReleaseNotesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where releaseNotes not equals to DEFAULT_RELEASE_NOTES
        defaultProductVersionShouldNotBeFound("releaseNotes.notEquals=" + DEFAULT_RELEASE_NOTES);

        // Get all the productVersionList where releaseNotes not equals to UPDATED_RELEASE_NOTES
        defaultProductVersionShouldBeFound("releaseNotes.notEquals=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByReleaseNotesIsInShouldWork() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where releaseNotes in DEFAULT_RELEASE_NOTES or UPDATED_RELEASE_NOTES
        defaultProductVersionShouldBeFound("releaseNotes.in=" + DEFAULT_RELEASE_NOTES + "," + UPDATED_RELEASE_NOTES);

        // Get all the productVersionList where releaseNotes equals to UPDATED_RELEASE_NOTES
        defaultProductVersionShouldNotBeFound("releaseNotes.in=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByReleaseNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where releaseNotes is not null
        defaultProductVersionShouldBeFound("releaseNotes.specified=true");

        // Get all the productVersionList where releaseNotes is null
        defaultProductVersionShouldNotBeFound("releaseNotes.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductVersionsByReleaseNotesContainsSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where releaseNotes contains DEFAULT_RELEASE_NOTES
        defaultProductVersionShouldBeFound("releaseNotes.contains=" + DEFAULT_RELEASE_NOTES);

        // Get all the productVersionList where releaseNotes contains UPDATED_RELEASE_NOTES
        defaultProductVersionShouldNotBeFound("releaseNotes.contains=" + UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void getAllProductVersionsByReleaseNotesNotContainsSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList where releaseNotes does not contain DEFAULT_RELEASE_NOTES
        defaultProductVersionShouldNotBeFound("releaseNotes.doesNotContain=" + DEFAULT_RELEASE_NOTES);

        // Get all the productVersionList where releaseNotes does not contain UPDATED_RELEASE_NOTES
        defaultProductVersionShouldBeFound("releaseNotes.doesNotContain=" + UPDATED_RELEASE_NOTES);
    }


    @Test
    @Transactional
    public void getAllProductVersionsByProductIsEqualToSomething() throws Exception {
        // Get already existing entity
        Product product = productVersion.getProduct();
        productVersionRepository.saveAndFlush(productVersion);
        Long productId = product.getId();

        // Get all the productVersionList where product equals to productId
        defaultProductVersionShouldBeFound("productId.equals=" + productId);

        // Get all the productVersionList where product equals to productId + 1
        defaultProductVersionShouldNotBeFound("productId.equals=" + (productId + 1));
    }


    @Test
    @Transactional
    public void getAllProductVersionsByBuildIsEqualToSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);
        Build build = BuildResourceIT.createEntity(em);
        em.persist(build);
        em.flush();
        productVersion.setBuild(build);
        productVersionRepository.saveAndFlush(productVersion);
        Long buildId = build.getId();

        // Get all the productVersionList where build equals to buildId
        defaultProductVersionShouldBeFound("buildId.equals=" + buildId);

        // Get all the productVersionList where build equals to buildId + 1
        defaultProductVersionShouldNotBeFound("buildId.equals=" + (buildId + 1));
    }


    @Test
    @Transactional
    public void getAllProductVersionsByComponentIsEqualToSomething() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);
        ProductComponentVersion component = ProductComponentVersionResourceIT.createEntity(em);
        em.persist(component);
        em.flush();
        productVersion.addComponent(component);
        productVersionRepository.saveAndFlush(productVersion);
        Long componentId = component.getId();

        // Get all the productVersionList where component equals to componentId
        defaultProductVersionShouldBeFound("componentId.equals=" + componentId);

        // Get all the productVersionList where component equals to componentId + 1
        defaultProductVersionShouldNotBeFound("componentId.equals=" + (componentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductVersionShouldBeFound(String filter) throws Exception {
        restProductVersionMockMvc.perform(get("/api/product-versions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].releaseNotes").value(hasItem(DEFAULT_RELEASE_NOTES)));

        // Check, that the count call also returns 1
        restProductVersionMockMvc.perform(get("/api/product-versions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductVersionShouldNotBeFound(String filter) throws Exception {
        restProductVersionMockMvc.perform(get("/api/product-versions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductVersionMockMvc.perform(get("/api/product-versions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingProductVersion() throws Exception {
        // Get the productVersion
        restProductVersionMockMvc.perform(get("/api/product-versions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductVersion() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        int databaseSizeBeforeUpdate = productVersionRepository.findAll().size();

        // Update the productVersion
        ProductVersion updatedProductVersion = productVersionRepository.findById(productVersion.getId()).get();
        // Disconnect from session so that the updates on updatedProductVersion are not directly saved in db
        em.detach(updatedProductVersion);
        updatedProductVersion
            .version(UPDATED_VERSION)
            .releaseNotes(UPDATED_RELEASE_NOTES);
        ProductVersionDTO productVersionDTO = productVersionMapper.toDto(updatedProductVersion);

        restProductVersionMockMvc.perform(put("/api/product-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productVersionDTO)))
            .andExpect(status().isOk());

        // Validate the ProductVersion in the database
        List<ProductVersion> productVersionList = productVersionRepository.findAll();
        assertThat(productVersionList).hasSize(databaseSizeBeforeUpdate);
        ProductVersion testProductVersion = productVersionList.get(productVersionList.size() - 1);
        assertThat(testProductVersion.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testProductVersion.getReleaseNotes()).isEqualTo(UPDATED_RELEASE_NOTES);
    }

    @Test
    @Transactional
    public void updateNonExistingProductVersion() throws Exception {
        int databaseSizeBeforeUpdate = productVersionRepository.findAll().size();

        // Create the ProductVersion
        ProductVersionDTO productVersionDTO = productVersionMapper.toDto(productVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductVersionMockMvc.perform(put("/api/product-versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productVersionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductVersion in the database
        List<ProductVersion> productVersionList = productVersionRepository.findAll();
        assertThat(productVersionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProductVersion() throws Exception {
        // Initialize the database
        productVersionRepository.saveAndFlush(productVersion);

        int databaseSizeBeforeDelete = productVersionRepository.findAll().size();

        // Delete the productVersion
        restProductVersionMockMvc.perform(delete("/api/product-versions/{id}", productVersion.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductVersion> productVersionList = productVersionRepository.findAll();
        assertThat(productVersionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
