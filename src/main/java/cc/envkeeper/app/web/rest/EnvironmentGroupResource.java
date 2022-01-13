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
import cc.envkeeper.app.service.EnvironmentGroupService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.EnvironmentGroupDTO;
import cc.envkeeper.app.service.dto.EnvironmentGroupCriteria;
import cc.envkeeper.app.service.EnvironmentGroupQueryService;

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
 * REST controller for managing {@link cc.envkeeper.app.domain.EnvironmentGroup}.
 */
@RestController
@RequestMapping("/api")
public class EnvironmentGroupResource {

    private final Logger log = LoggerFactory.getLogger(EnvironmentGroupResource.class);

    private static final String ENTITY_NAME = "environmentGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnvironmentGroupService environmentGroupService;

    private final EnvironmentGroupQueryService environmentGroupQueryService;

    public EnvironmentGroupResource(EnvironmentGroupService environmentGroupService, EnvironmentGroupQueryService environmentGroupQueryService) {
        this.environmentGroupService = environmentGroupService;
        this.environmentGroupQueryService = environmentGroupQueryService;
    }

    /**
     * {@code POST  /environment-groups} : Create a new environmentGroup.
     *
     * @param environmentGroupDTO the environmentGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new environmentGroupDTO, or with status {@code 400 (Bad Request)} if the environmentGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/environment-groups")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<EnvironmentGroupDTO> createEnvironmentGroup(@Valid @RequestBody EnvironmentGroupDTO environmentGroupDTO) throws URISyntaxException {
        log.debug("REST request to save EnvironmentGroup : {}", environmentGroupDTO);
        if (environmentGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new environmentGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EnvironmentGroupDTO result = environmentGroupService.save(environmentGroupDTO);
        return ResponseEntity.created(new URI("/api/environment-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /environment-groups} : Updates an existing environmentGroup.
     *
     * @param environmentGroupDTO the environmentGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentGroupDTO,
     * or with status {@code 400 (Bad Request)} if the environmentGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the environmentGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/environment-groups")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<EnvironmentGroupDTO> updateEnvironmentGroup(@Valid @RequestBody EnvironmentGroupDTO environmentGroupDTO) throws URISyntaxException {
        log.debug("REST request to update EnvironmentGroup : {}", environmentGroupDTO);
        if (environmentGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EnvironmentGroupDTO result = environmentGroupService.save(environmentGroupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, environmentGroupDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /environment-groups} : Patches an existing environmentGroup.
     *
     * @param environmentGroupDTO the environmentGroupDTO to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentGroupDTO,
     * or with status {@code 400 (Bad Request)} if the environmentGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the environmentGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/environment-groups")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<EnvironmentGroupDTO> patchEnvironmentGroup(@RequestBody EnvironmentGroupDTO environmentGroupDTO) throws URISyntaxException {
        log.debug("REST request to patch EnvironmentGroup : {}", environmentGroupDTO);
        if (environmentGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<EnvironmentGroupDTO> old = environmentGroupService.findOne(environmentGroupDTO.getId());
        if (!old.isPresent()) {
            return ResponseUtil.wrapOrNotFound(old);

        }
        EnvironmentGroupDTO oldDTO = old.get();
        if (environmentGroupDTO.getShortName() != null) {
            oldDTO.setShortName(environmentGroupDTO.getShortName());
        }
        if (environmentGroupDTO.getFullName() != null) {
            oldDTO.setFullName(environmentGroupDTO.getFullName());
        }
        if (environmentGroupDTO.getDescription() != null) {
            oldDTO.setDescription(environmentGroupDTO.getDescription());
        }
        if (environmentGroupDTO.getSortOrder() != null) {
            oldDTO.setSortOrder(environmentGroupDTO.getSortOrder());
        }
        EnvironmentGroupDTO result = environmentGroupService.save(oldDTO);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, environmentGroupDTO.getId().toString()))
                             .body(result);
    }

    /**
     * {@code PATCH  /environment-groups/batch} : Patches several existing environmentGroups.
     *
     * @param environmentGroupDTOs the environmentGroupDTO objects to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentGroupDTO,
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/environment-groups/bulk")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<List<EnvironmentGroupDTO>> patchEnvironmentGroups(@RequestBody EnvironmentGroupDTO[] environmentGroupDTOs) throws URISyntaxException {
        log.debug("REST request to patch EnvironmentGroups : {}", environmentGroupDTOs);

        List<EnvironmentGroupDTO> ret = new ArrayList<>();
        for (EnvironmentGroupDTO environmentGroupDTO: environmentGroupDTOs) {
            ResponseEntity<EnvironmentGroupDTO> thisRet = patchEnvironmentGroup(environmentGroupDTO);
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
     * {@code GET  /environment-groups} : get all the environmentGroups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of environmentGroups in body.
     */
    @GetMapping("/environment-groups")
    public ResponseEntity<List<EnvironmentGroupDTO>> getAllEnvironmentGroups(EnvironmentGroupCriteria criteria, Pageable pageable) {
        log.debug("REST request to get EnvironmentGroups by criteria: {}", criteria);
        Page<EnvironmentGroupDTO> page = environmentGroupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /environment-groups/count} : count all the environmentGroups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/environment-groups/count")
    public ResponseEntity<Long> countEnvironmentGroups(EnvironmentGroupCriteria criteria) {
        log.debug("REST request to count EnvironmentGroups by criteria: {}", criteria);
        return ResponseEntity.ok().body(environmentGroupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /environment-groups/:id} : get the "id" environmentGroup.
     *
     * @param id the id of the environmentGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the environmentGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/environment-groups/{id}")
    public ResponseEntity<EnvironmentGroupDTO> getEnvironmentGroup(@PathVariable Long id) {
        log.debug("REST request to get EnvironmentGroup : {}", id);
        Optional<EnvironmentGroupDTO> environmentGroupDTO = environmentGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(environmentGroupDTO);
    }

    /**
     * {@code DELETE  /environment-groups/:id} : delete the "id" environmentGroup.
     *
     * @param id the id of the environmentGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/environment-groups/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteEnvironmentGroup(@PathVariable Long id) {
        log.debug("REST request to delete EnvironmentGroup : {}", id);
        environmentGroupService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
