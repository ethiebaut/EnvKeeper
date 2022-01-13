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

import cc.envkeeper.app.security.AuthoritiesConstants;
import cc.envkeeper.app.service.ProductComponentVersionService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.ProductComponentVersionDTO;
import cc.envkeeper.app.service.dto.ProductComponentVersionCriteria;
import cc.envkeeper.app.service.ProductComponentVersionQueryService;

import cc.envkeeper.app.service.mapper.ReleaseNotesParser;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link cc.envkeeper.app.domain.ProductComponentVersion}.
 */
@RestController
@RequestMapping("/api")
public class ProductComponentVersionResource {

    private final Logger log = LoggerFactory.getLogger(ProductComponentVersionResource.class);

    private static final String ENTITY_NAME = "productComponentVersion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductComponentVersionService productComponentVersionService;

    private final ProductComponentVersionQueryService productComponentVersionQueryService;

    public ProductComponentVersionResource(ProductComponentVersionService productComponentVersionService, ProductComponentVersionQueryService productComponentVersionQueryService) {
        this.productComponentVersionService = productComponentVersionService;
        this.productComponentVersionQueryService = productComponentVersionQueryService;
    }

    /**
     * {@code POST  /product-component-versions} : Create a new productComponentVersion.
     *
     * @param productComponentVersionDTO the productComponentVersionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productComponentVersionDTO, or with status {@code 400 (Bad Request)} if the productComponentVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-component-versions")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<ProductComponentVersionDTO> createProductComponentVersion(@Valid @RequestBody ProductComponentVersionDTO productComponentVersionDTO) throws URISyntaxException {
        log.debug("REST request to save ProductComponentVersion : {}", productComponentVersionDTO);
        if (productComponentVersionDTO.getId() != null) {
            throw new BadRequestAlertException("A new productComponentVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // Set release notes
        productComponentVersionDTO.setReleaseNotes(
            ReleaseNotesParser.toReleaseNotes(
                productComponentVersionDTO.getReleaseNotes(),
                productComponentVersionDTO.getReleaseNotesBase64()));

        ProductComponentVersionDTO result = productComponentVersionService.save(productComponentVersionDTO);
        return ResponseEntity.created(new URI("/api/product-component-versions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-component-versions} : Updates an existing productComponentVersion.
     *
     * @param productComponentVersionDTO the productComponentVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productComponentVersionDTO,
     * or with status {@code 400 (Bad Request)} if the productComponentVersionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productComponentVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-component-versions")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<ProductComponentVersionDTO> updateProductComponentVersion(@Valid @RequestBody ProductComponentVersionDTO productComponentVersionDTO) throws URISyntaxException {
        log.debug("REST request to update ProductComponentVersion : {}", productComponentVersionDTO);
        if (productComponentVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        // Set release notes
        productComponentVersionDTO.setReleaseNotes(
            ReleaseNotesParser.toReleaseNotes(
                productComponentVersionDTO.getReleaseNotes(),
                productComponentVersionDTO.getReleaseNotesBase64()));

        ProductComponentVersionDTO result = productComponentVersionService.save(productComponentVersionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productComponentVersionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /product-component-versions} : get all the productComponentVersions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productComponentVersions in body.
     */
    @GetMapping("/product-component-versions")
    public ResponseEntity<List<ProductComponentVersionDTO>> getAllProductComponentVersions(ProductComponentVersionCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProductComponentVersions by criteria: {}", criteria);
        Page<ProductComponentVersionDTO> page = productComponentVersionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-component-versions/count} : count all the productComponentVersions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/product-component-versions/count")
    public ResponseEntity<Long> countProductComponentVersions(ProductComponentVersionCriteria criteria) {
        log.debug("REST request to count ProductComponentVersions by criteria: {}", criteria);
        return ResponseEntity.ok().body(productComponentVersionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /product-component-versions/:id} : get the "id" productComponentVersion.
     *
     * @param id the id of the productComponentVersionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productComponentVersionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-component-versions/{id}")
    public ResponseEntity<ProductComponentVersionDTO> getProductComponentVersion(@PathVariable Long id) {
        log.debug("REST request to get ProductComponentVersion : {}", id);
        Optional<ProductComponentVersionDTO> productComponentVersionDTO = productComponentVersionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productComponentVersionDTO);
    }

    /**
     * {@code DELETE  /product-component-versions/:id} : delete the "id" productComponentVersion.
     *
     * @param id the id of the productComponentVersionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-component-versions/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteProductComponentVersion(@PathVariable Long id) {
        log.debug("REST request to delete ProductComponentVersion : {}", id);
        productComponentVersionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
