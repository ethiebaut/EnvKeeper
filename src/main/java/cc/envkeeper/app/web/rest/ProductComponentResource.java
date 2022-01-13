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
import cc.envkeeper.app.service.ProductComponentService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.ProductComponentDTO;
import cc.envkeeper.app.service.dto.ProductComponentCriteria;
import cc.envkeeper.app.service.ProductComponentQueryService;

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
 * REST controller for managing {@link cc.envkeeper.app.domain.ProductComponent}.
 */
@RestController
@RequestMapping("/api")
public class ProductComponentResource {

    private final Logger log = LoggerFactory.getLogger(ProductComponentResource.class);

    private static final String ENTITY_NAME = "productComponent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductComponentService productComponentService;

    private final ProductComponentQueryService productComponentQueryService;

    public ProductComponentResource(ProductComponentService productComponentService, ProductComponentQueryService productComponentQueryService) {
        this.productComponentService = productComponentService;
        this.productComponentQueryService = productComponentQueryService;
    }

    /**
     * {@code POST  /product-components} : Create a new productComponent.
     *
     * @param productComponentDTO the productComponentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productComponentDTO, or with status {@code 400 (Bad Request)} if the productComponent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-components")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<ProductComponentDTO> createProductComponent(@Valid @RequestBody ProductComponentDTO productComponentDTO) throws URISyntaxException {
        log.debug("REST request to save ProductComponent : {}", productComponentDTO);
        if (productComponentDTO.getId() != null) {
            throw new BadRequestAlertException("A new productComponent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductComponentDTO result = productComponentService.save(productComponentDTO);
        return ResponseEntity.created(new URI("/api/product-components/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-components} : Updates an existing productComponent.
     *
     * @param productComponentDTO the productComponentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productComponentDTO,
     * or with status {@code 400 (Bad Request)} if the productComponentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productComponentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-components")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<ProductComponentDTO> updateProductComponent(@Valid @RequestBody ProductComponentDTO productComponentDTO) throws URISyntaxException {
        log.debug("REST request to update ProductComponent : {}", productComponentDTO);
        if (productComponentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProductComponentDTO result = productComponentService.save(productComponentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productComponentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /product-components} : get all the productComponents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productComponents in body.
     */
    @GetMapping("/product-components")
    public ResponseEntity<List<ProductComponentDTO>> getAllProductComponents(ProductComponentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProductComponents by criteria: {}", criteria);
        Page<ProductComponentDTO> page = productComponentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-components/count} : count all the productComponents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/product-components/count")
    public ResponseEntity<Long> countProductComponents(ProductComponentCriteria criteria) {
        log.debug("REST request to count ProductComponents by criteria: {}", criteria);
        return ResponseEntity.ok().body(productComponentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /product-components/:id} : get the "id" productComponent.
     *
     * @param id the id of the productComponentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productComponentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-components/{id}")
    public ResponseEntity<ProductComponentDTO> getProductComponent(@PathVariable Long id) {
        log.debug("REST request to get ProductComponent : {}", id);
        Optional<ProductComponentDTO> productComponentDTO = productComponentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productComponentDTO);
    }

    /**
     * {@code DELETE  /product-components/:id} : delete the "id" productComponent.
     *
     * @param id the id of the productComponentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-components/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteProductComponent(@PathVariable Long id) {
        log.debug("REST request to delete ProductComponent : {}", id);
        productComponentService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
