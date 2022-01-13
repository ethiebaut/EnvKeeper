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
import cc.envkeeper.app.service.EnvironmentService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.EnvironmentDTO;
import cc.envkeeper.app.service.dto.EnvironmentCriteria;
import cc.envkeeper.app.service.EnvironmentQueryService;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link cc.envkeeper.app.domain.Environment}.
 */
@RestController
@RequestMapping("/api")
public class EnvironmentResource {

    private final Logger log = LoggerFactory.getLogger(EnvironmentResource.class);

    private static final String ENTITY_NAME = "environment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnvironmentService environmentService;

    private final EnvironmentQueryService environmentQueryService;

    public EnvironmentResource(EnvironmentService environmentService, EnvironmentQueryService environmentQueryService) {
        this.environmentService = environmentService;
        this.environmentQueryService = environmentQueryService;
    }

    /**
     * {@code POST  /environments} : Create a new environment.
     *
     * @param environmentDTO the environmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new environmentDTO, or with status {@code 400 (Bad Request)} if the environment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/environments")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<EnvironmentDTO> createEnvironment(@Valid @RequestBody EnvironmentDTO environmentDTO) throws URISyntaxException {
        log.debug("REST request to save Environment : {}", environmentDTO);
        if (environmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new environment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EnvironmentDTO result = environmentService.save(environmentDTO);
        return ResponseEntity.created(new URI("/api/environments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /environments} : Updates an existing environment.
     *
     * @param environmentDTO the environmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentDTO,
     * or with status {@code 400 (Bad Request)} if the environmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the environmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/environments")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<EnvironmentDTO> updateEnvironment(@Valid @RequestBody EnvironmentDTO environmentDTO) throws URISyntaxException {
        log.debug("REST request to update Environment : {}", environmentDTO);
        if (environmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EnvironmentDTO result = environmentService.save(environmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, environmentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /environments} : patches an existing environment.
     *
     * @param environmentDTO the environmentDTO to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentDTO,
     * or with status {@code 400 (Bad Request)} if the environmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the environmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/environments")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<EnvironmentDTO> patchEnvironment(@RequestBody EnvironmentDTO environmentDTO) throws URISyntaxException {
        log.debug("REST request to patch Environment : {}", environmentDTO);
        if (environmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<EnvironmentDTO> old = environmentService.findOne(environmentDTO.getId());
        if (!old.isPresent()) {
            return ResponseUtil.wrapOrNotFound(old);
        }
        EnvironmentDTO oldDTO = old.get();
        if (environmentDTO.getShortName() != null) {
            oldDTO.setShortName(environmentDTO.getShortName());
        }
        if (environmentDTO.getFullName() != null) {
            oldDTO.setFullName(environmentDTO.getFullName());
        }
        if (environmentDTO.getDescription() != null) {
            oldDTO.setDescription(environmentDTO.getDescription());
        }
        if (environmentDTO.getSortOrder() != null) {
            oldDTO.setSortOrder(environmentDTO.getSortOrder());
        }
        if (environmentDTO.getEnvironmentGroupId() != null) {
            oldDTO.setEnvironmentGroupId(environmentDTO.getEnvironmentGroupId());
            oldDTO.setEnvironmentGroupShortName(null);
        }
        EnvironmentDTO result = environmentService.save(oldDTO);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, environmentDTO.getId().toString()))
                             .body(result);
    }

    /**
     * {@code PATCH  /environments/bulk} : patches several existing environments.
     *
     * @param environmentDTOs the environmentDTO objects to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentDTO,
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/environments/bulk")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<List<EnvironmentDTO>> patchEnvironments(@RequestBody EnvironmentDTO[] environmentDTOs) throws URISyntaxException {
        log.debug("REST request to patch Environments : {}", environmentDTOs);

        List<EnvironmentDTO> ret = new ArrayList<>();

        for (EnvironmentDTO environmentDTO: environmentDTOs) {
            ResponseEntity<EnvironmentDTO> thisRet = patchEnvironment(environmentDTO);
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
     * {@code GET  /environments} : get all the environments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of environments in body.
     */
    @GetMapping("/environments")
    public ResponseEntity<List<EnvironmentDTO>> getAllEnvironments(EnvironmentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Environments by criteria: {}", criteria);
        Page<EnvironmentDTO> page = environmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /environments/count} : count all the environments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/environments/count")
    public ResponseEntity<Long> countEnvironments(EnvironmentCriteria criteria) {
        log.debug("REST request to count Environments by criteria: {}", criteria);
        return ResponseEntity.ok().body(environmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /environments/:id} : get the "id" environment.
     *
     * @param id the id of the environmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the environmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/environments/{id}")
    public ResponseEntity<EnvironmentDTO> getEnvironment(@PathVariable Long id) {
        log.debug("REST request to get Environment : {}", id);
        Optional<EnvironmentDTO> environmentDTO = environmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(environmentDTO);
    }

    /**
     * {@code DELETE  /environments/:id} : delete the "id" environment.
     *
     * @param id the id of the environmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/environments/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteEnvironment(@PathVariable Long id) {
        log.debug("REST request to delete Environment : {}", id);
        environmentService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
