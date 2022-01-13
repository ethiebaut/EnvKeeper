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
import cc.envkeeper.app.service.BuildService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.BuildDTO;
import cc.envkeeper.app.service.dto.BuildCriteria;
import cc.envkeeper.app.service.BuildQueryService;

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
 * REST controller for managing {@link cc.envkeeper.app.domain.Build}.
 */
@RestController
@RequestMapping("/api")
public class BuildResource {

    private final Logger log = LoggerFactory.getLogger(BuildResource.class);

    public static final String ENTITY_NAME = "build";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BuildService buildService;

    private final BuildQueryService buildQueryService;

    public BuildResource(BuildService buildService, BuildQueryService buildQueryService) {
        this.buildService = buildService;
        this.buildQueryService = buildQueryService;
    }

    /**
     * {@code POST  /builds} : Create a new build.
     *
     * @param buildDTO the buildDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new buildDTO, or with status {@code 400 (Bad Request)} if the build has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/builds")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildDTO> createBuild(@Valid @RequestBody BuildDTO buildDTO) throws URISyntaxException {
        log.debug("REST request to save Build : {}", buildDTO);
        if (buildDTO.getId() != null) {
            throw new BadRequestAlertException("A new build cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BuildDTO result = buildService.save(buildDTO);
        return ResponseEntity.created(new URI("/api/builds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /builds} : Updates an existing build.
     *
     * @param buildDTO the buildDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildDTO,
     * or with status {@code 400 (Bad Request)} if the buildDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buildDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/builds")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildDTO> updateBuild(@Valid @RequestBody BuildDTO buildDTO) throws URISyntaxException {
        log.debug("REST request to update Build : {}", buildDTO);
        if (buildDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BuildDTO result = buildService.save(buildDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /builds} : Patches an existing build.
     *
     * @param buildDTO the buildDTO to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildDTO,
     * or with status {@code 400 (Bad Request)} if the buildDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buildDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/builds")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildDTO> patchBuild(@RequestBody BuildDTO buildDTO) throws URISyntaxException {
        log.debug("REST request to patch Build : {}", buildDTO);
        if (buildDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<BuildDTO> old = buildService.findOne(buildDTO.getId());
        if (!old.isPresent()) {
            return ResponseUtil.wrapOrNotFound(old);
        }
        BuildDTO oldDTO = old.get();
        if (buildDTO.getBuildUrl() != null) {
            oldDTO.setBuildUrl(buildDTO.getBuildUrl());
        }
        if (buildDTO.getBuildName() != null) {
            oldDTO.setBuildName(buildDTO.getBuildName());
        }
        if (buildDTO.getStatus() != null) {
            oldDTO.setStatus(buildDTO.getStatus());
        }
        if (buildDTO.getStartTime() != null) {
            oldDTO.setStartTime(buildDTO.getStartTime());
        }
        if (buildDTO.getEndTime() != null) {
            oldDTO.setEndTime(buildDTO.getEndTime());
        }
        if (buildDTO.getParentBuildId() != null) {
            oldDTO.setParentBuildId(buildDTO.getParentBuildId());
        }
        if (buildDTO.getDeployments() != null) {
            oldDTO.setDeployments(buildDTO.getDeployments());
        }
        BuildDTO result = buildService.save(oldDTO);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildDTO.getId().toString()))
                             .body(result);
    }

    /**
     * {@code GET  /builds} : get all the builds.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of builds in body.
     */
    @GetMapping("/builds")
    public ResponseEntity<List<BuildDTO>> getAllBuilds(BuildCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Builds by criteria: {}", criteria);
        Page<BuildDTO> page = buildQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /builds/count} : count all the builds.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/builds/count")
    public ResponseEntity<Long> countBuilds(BuildCriteria criteria) {
        log.debug("REST request to count Builds by criteria: {}", criteria);
        return ResponseEntity.ok().body(buildQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /builds/:id} : get the "id" build.
     *
     * @param id the id of the buildDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the buildDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/builds/{id}")
    public ResponseEntity<BuildDTO> getBuild(@PathVariable Long id) {
        log.debug("REST request to get Build : {}", id);
        Optional<BuildDTO> buildDTO = buildService.findOne(id);
        return ResponseUtil.wrapOrNotFound(buildDTO);
    }

    /**
     * {@code DELETE  /builds/:id} : delete the "id" build.
     *
     * @param id the id of the buildDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/builds/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteBuild(@PathVariable Long id) {
        log.debug("REST request to delete Build : {}", id);
        buildService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
