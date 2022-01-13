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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import cc.envkeeper.app.domain.enumeration.BuildStatus;
import cc.envkeeper.app.security.AuthoritiesConstants;
import cc.envkeeper.app.service.ProductComponentQueryService;
import cc.envkeeper.app.service.ProductComponentService;
import cc.envkeeper.app.service.ProductComponentVersionQueryService;
import cc.envkeeper.app.service.ProductComponentVersionService;
import cc.envkeeper.app.service.ProductVersionQueryService;
import cc.envkeeper.app.service.ProductVersionService;
import cc.envkeeper.app.service.dto.ProductComponentCriteria;
import cc.envkeeper.app.service.dto.ProductComponentDTO;
import cc.envkeeper.app.service.dto.ProductComponentVersionCriteria;
import cc.envkeeper.app.service.dto.ProductComponentVersionDTO;
import cc.envkeeper.app.service.dto.ProductVersionCriteria;
import cc.envkeeper.app.service.dto.ProductVersionDTO;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.mapper.ReleaseNotesParser;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link cc.envkeeper.app.domain.ProductVersion}.
 */
@RestController
@RequestMapping("/api")
public class ProductVersionResource {

    private final Logger log = LoggerFactory.getLogger(ProductVersionResource.class);

    private static final String ENTITY_NAME = "productVersion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductVersionService productVersionService;

    private final ProductVersionQueryService productVersionQueryService;

    private final ProductComponentService productComponentService;

    private final ProductComponentQueryService productComponentQueryService;

    private final ProductComponentVersionService productComponentVersionService;

    private final ProductComponentVersionQueryService productComponentVersionQueryService;

    public ProductVersionResource(
        ProductVersionService productVersionService,
        ProductVersionQueryService productVersionQueryService,
        ProductComponentService productComponentService,
        ProductComponentQueryService productComponentQueryService,
        ProductComponentVersionService productComponentVersionService,
        ProductComponentVersionQueryService productComponentVersionQueryService) {
        this.productVersionService = productVersionService;
        this.productComponentService = productComponentService;
        this.productVersionQueryService = productVersionQueryService;
        this.productComponentQueryService = productComponentQueryService;
        this.productComponentVersionService = productComponentVersionService;
        this.productComponentVersionQueryService = productComponentVersionQueryService;
    }

    /**
     * {@code POST  /product-versions} : Create a new productVersion.
     *
     * @param productVersionDTO the productVersionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productVersionDTO, or with status {@code 400 (Bad Request)} if the productVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-versions")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    @Transactional
    public ResponseEntity<ProductVersionDTO> createProductVersion(@Valid @RequestBody ProductVersionDTO productVersionDTO) throws URISyntaxException {
        log.debug("REST request to save ProductVersion : {}", productVersionDTO);
        if (productVersionDTO.getId() != null) {
            throw new BadRequestAlertException("A new productVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // Resolve and/or create nested objects
        createdNestedObjects(productVersionDTO);

        ProductVersionDTO result = productVersionService.save(productVersionDTO);
        return ResponseEntity.created(new URI("/api/product-versions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /product-versions/bulk} : Create several productVersions.
     *
     * @param productVersionDTOs the productVersionDTO objects to create.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the new productVersionDTO objects or null for each failure
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-versions/bulk")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    @Transactional
    public ResponseEntity<List<ProductVersionDTO>> createProductVersions(@Valid @RequestBody ProductVersionDTO[] productVersionDTOs) throws URISyntaxException {
        log.debug("REST request to save ProductVersions : {}", productVersionDTOs);

        List<ProductVersionDTO> ret = new ArrayList<>();
        for (ProductVersionDTO productVersionDTO: productVersionDTOs) {
            ResponseEntity<ProductVersionDTO> thisRet = createProductVersion(productVersionDTO);
            if (thisRet.getStatusCode().is2xxSuccessful()) {
                ret.add(thisRet.getBody());
            } else {
                ret.add(null);
            }
        }
        return ResponseEntity.ok()
                             .body(ret);
    }

    /**
     * {@code PUT  /product-versions} : Updates an existing productVersion.
     *
     * @param productVersionDTO the productVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVersionDTO,
     * or with status {@code 400 (Bad Request)} if the productVersionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-versions")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<ProductVersionDTO> updateProductVersion(@Valid @RequestBody ProductVersionDTO productVersionDTO) throws URISyntaxException {
        log.debug("REST request to update ProductVersion : {}", productVersionDTO);
        if (productVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        // Resolve and/or create nested objects
        createdNestedObjects(productVersionDTO);

        ProductVersionDTO result = productVersionService.save(productVersionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productVersionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /product-versions} : Patches an existing productVersion.
     *
     * @param productVersionDTO the productVersionDTO to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVersionDTO,
     * or with status {@code 400 (Bad Request)} if the productVersionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/product-versions")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<ProductVersionDTO> patchProductVersion(@RequestBody ProductVersionDTO productVersionDTO) throws URISyntaxException {
        log.debug("REST request to patch ProductVersion : {}", productVersionDTO);
        if (productVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<ProductVersionDTO> old = productVersionService.findOne(productVersionDTO.getId());
        if (!old.isPresent()) {
            return ResponseUtil.wrapOrNotFound(old);
        }
        ProductVersionDTO oldDto = old.get();
        if (productVersionDTO.getVersion() != null) {
            oldDto.setVersion(productVersionDTO.getVersion());
        }
        if (productVersionDTO.getReleaseNotes() != null) {
            oldDto.setReleaseNotes(productVersionDTO.getReleaseNotes());
        }
        if (productVersionDTO.getProductId() != null) {
            oldDto.setProductId(productVersionDTO.getProductId());
            oldDto.setProductShortName(null);
        }
        if (productVersionDTO.getBuildId() != null) {
            oldDto.setBuildId(productVersionDTO.getBuildId());
        }
        if (productVersionDTO.getComponents() != null) {
            oldDto.setComponents(productVersionDTO.getComponents());
        }

        // Resolve and/or create nested objects
        createdNestedObjects(oldDto);

        ProductVersionDTO result = productVersionService.save(oldDto);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productVersionDTO.getId().toString()))
                             .body(result);
    }

    private void createdNestedObjects(ProductVersionDTO productVersionDTO) {

        // Set release notes
        productVersionDTO.setReleaseNotes(
            ReleaseNotesParser.toReleaseNotes(
                productVersionDTO.getReleaseNotes(),
                productVersionDTO.getReleaseNotesBase64()));

        // Resolve and/or create component versions
        if (productVersionDTO.getComponents() != null) {
            for (ProductComponentVersionDTO componentVersion: productVersionDTO.getComponents()) {
                if (componentVersion.getComponentId() == null && !StringUtils.isEmpty(componentVersion.getComponentShortName())) {

                    // Find component
                    ProductComponentCriteria productComponentCriteria = new ProductComponentCriteria();
                    StringFilter componentShortnameFilter = new StringFilter();
                    componentShortnameFilter.setEquals(componentVersion.getComponentShortName());
                    productComponentCriteria.setShortName(componentShortnameFilter);
                    List<ProductComponentDTO> components = productComponentQueryService.findByCriteria(productComponentCriteria);
                    if (components.isEmpty()) {
                        // Create it so
                        ProductComponentDTO productComponentDTO = new ProductComponentDTO();
                        productComponentDTO.setShortName(componentVersion.getComponentShortName());
                        productComponentDTO.setFullName(componentVersion.getComponentShortName());
                        productComponentDTO.setDescription(componentVersion.getComponentShortName());
                        components.add(productComponentService.save(productComponentDTO));
                    }

                    // And then its version
                    ProductComponentVersionCriteria componentVersionCriteria = new ProductComponentVersionCriteria();
                    LongFilter productComponentFilter = new LongFilter();
                    productComponentFilter.setEquals(components.get(0).getId());
                    StringFilter componentVersionFilter = new StringFilter();
                    componentVersionFilter.setEquals(componentVersion.getVersion());
                    componentVersionCriteria.setComponentId(productComponentFilter);
                    componentVersionCriteria.setVersion(componentVersionFilter);
                    List<ProductComponentVersionDTO> componentVersions = productComponentVersionQueryService.findByCriteria(componentVersionCriteria);
                    if (componentVersions.isEmpty()) {
                        // Create it so
                        componentVersion.setComponentId(components.get(0).getId());
                        if (componentVersion.getBuildStatus() == null) {
                            componentVersion.setBuildStatus(BuildStatus.SUCCEEDED);
                        }
                        if (componentVersion.getStartTime() == null) {
                            componentVersion.setStartTime(Instant.now());
                        }
                        if (componentVersion.getEndTime() == null) {
                            componentVersion.setEndTime(Instant.now());
                        }
                        componentVersion.setId(productComponentVersionService.save(componentVersion).getId());
                    } else {
                        componentVersion.setId(componentVersions.get(0).getId());
                    }
                }
            }
        }
    }

    /**
     * {@code GET  /product-versions} : get all the productVersions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productVersions in body.
     */
    @GetMapping("/product-versions")
    public ResponseEntity<List<ProductVersionDTO>> getAllProductVersions(ProductVersionCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProductVersions by criteria: {}", criteria);
        Page<ProductVersionDTO> page = productVersionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-versions/count} : count all the productVersions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/product-versions/count")
    public ResponseEntity<Long> countProductVersions(ProductVersionCriteria criteria) {
        log.debug("REST request to count ProductVersions by criteria: {}", criteria);
        return ResponseEntity.ok().body(productVersionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /product-versions/:id} : get the "id" productVersion.
     *
     * @param id the id of the productVersionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productVersionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-versions/{id}")
    public ResponseEntity<ProductVersionDTO> getProductVersion(@PathVariable Long id) {
        log.debug("REST request to get ProductVersion : {}", id);
        Optional<ProductVersionDTO> productVersionDTO = productVersionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productVersionDTO);
    }

    /**
     * {@code DELETE  /product-versions/:id} : delete the "id" productVersion.
     *
     * @param id the id of the productVersionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-versions/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteProductVersion(@PathVariable Long id) {
        log.debug("REST request to delete ProductVersion : {}", id);
        productVersionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
