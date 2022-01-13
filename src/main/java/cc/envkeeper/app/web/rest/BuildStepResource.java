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
import cc.envkeeper.app.service.BuildStepService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.BuildStepDTO;
import cc.envkeeper.app.service.dto.BuildStepCriteria;
import cc.envkeeper.app.service.BuildStepQueryService;

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
 * REST controller for managing {@link cc.envkeeper.app.domain.BuildStep}.
 */
@RestController
@RequestMapping("/api")
public class BuildStepResource {

    private final Logger log = LoggerFactory.getLogger(BuildStepResource.class);

    private static final String ENTITY_NAME = "buildStep";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BuildStepService buildStepService;

    private final BuildStepQueryService buildStepQueryService;

    public BuildStepResource(BuildStepService buildStepService, BuildStepQueryService buildStepQueryService) {
        this.buildStepService = buildStepService;
        this.buildStepQueryService = buildStepQueryService;
    }

    /**
     * {@code POST  /build-steps} : Create a new buildStep.
     *
     * @param buildStepDTO the buildStepDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new buildStepDTO, or with status {@code 400 (Bad Request)} if the buildStep has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/build-steps")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildStepDTO> createBuildStep(@Valid @RequestBody BuildStepDTO buildStepDTO) throws URISyntaxException {
        log.debug("REST request to save BuildStep : {}", buildStepDTO);
        if (buildStepDTO.getId() != null) {
            throw new BadRequestAlertException("A new buildStep cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BuildStepDTO result = buildStepService.save(buildStepDTO);
        return ResponseEntity.created(new URI("/api/build-steps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /build-steps} : Updates an existing buildStep.
     *
     * @param buildStepDTO the buildStepDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildStepDTO,
     * or with status {@code 400 (Bad Request)} if the buildStepDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buildStepDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/build-steps")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildStepDTO> updateBuildStep(@Valid @RequestBody BuildStepDTO buildStepDTO) throws URISyntaxException {
        log.debug("REST request to update BuildStep : {}", buildStepDTO);
        if (buildStepDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BuildStepDTO result = buildStepService.save(buildStepDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildStepDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /build-steps} : Patches an existing buildStep.
     *
     * @param buildStepDTO the buildStepDTO to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildStepDTO,
     * or with status {@code 400 (Bad Request)} if the buildStepDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buildStepDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/build-steps")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildStepDTO> patchBuildStep(@RequestBody BuildStepDTO buildStepDTO) throws URISyntaxException {
        log.debug("REST request to patch BuildStep : {}", buildStepDTO);
        if (buildStepDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<BuildStepDTO> old = buildStepService.findOne(buildStepDTO.getId());
        if (!old.isPresent()) {
            return ResponseUtil.wrapOrNotFound(old);
        }
        BuildStepDTO oldDTO = old.get();
        if (buildStepDTO.getStep() != null) {
            oldDTO.setStep(buildStepDTO.getStep());
        }
        if (buildStepDTO.getStatus() != null) {
            oldDTO.setStatus(buildStepDTO.getStatus());
        }
        if (buildStepDTO.getStartTime() != null) {
            oldDTO.setStartTime(buildStepDTO.getStartTime());
        }
        if (buildStepDTO.getEndTime() != null) {
            oldDTO.setEndTime(buildStepDTO.getEndTime());
        }
        if (buildStepDTO.getBuildId() != null) {
            oldDTO.setBuildId(buildStepDTO.getBuildId());
        }
        BuildStepDTO result = buildStepService.save(oldDTO);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildStepDTO.getId().toString()))
                             .body(result);
    }

    /**
     * {@code GET  /build-steps} : get all the buildSteps.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of buildSteps in body.
     */
    @GetMapping("/build-steps")
    public ResponseEntity<List<BuildStepDTO>> getAllBuildSteps(BuildStepCriteria criteria, Pageable pageable) {
        log.debug("REST request to get BuildSteps by criteria: {}", criteria);
        Page<BuildStepDTO> page = buildStepQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /build-steps/count} : count all the buildSteps.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/build-steps/count")
    public ResponseEntity<Long> countBuildSteps(BuildStepCriteria criteria) {
        log.debug("REST request to count BuildSteps by criteria: {}", criteria);
        return ResponseEntity.ok().body(buildStepQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /build-steps/:id} : get the "id" buildStep.
     *
     * @param id the id of the buildStepDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the buildStepDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/build-steps/{id}")
    public ResponseEntity<BuildStepDTO> getBuildStep(@PathVariable Long id) {
        log.debug("REST request to get BuildStep : {}", id);
        Optional<BuildStepDTO> buildStepDTO = buildStepService.findOne(id);
        return ResponseUtil.wrapOrNotFound(buildStepDTO);
    }

    /**
     * {@code DELETE  /build-steps/:id} : delete the "id" buildStep.
     *
     * @param id the id of the buildStepDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/build-steps/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteBuildStep(@PathVariable Long id) {
        log.debug("REST request to delete BuildStep : {}", id);
        buildStepService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
