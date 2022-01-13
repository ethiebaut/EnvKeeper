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
import cc.envkeeper.app.service.BuildStepStatisticService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.BuildStepStatisticDTO;
import cc.envkeeper.app.service.dto.BuildStepStatisticCriteria;
import cc.envkeeper.app.service.BuildStepStatisticQueryService;

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
 * REST controller for managing {@link cc.envkeeper.app.domain.BuildStepStatistic}.
 */
@RestController
@RequestMapping("/api")
public class BuildStepStatisticResource {

    private final Logger log = LoggerFactory.getLogger(BuildStepStatisticResource.class);

    private static final String ENTITY_NAME = "buildStepStatistic";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BuildStepStatisticService buildStepStatisticService;

    private final BuildStepStatisticQueryService buildStepStatisticQueryService;

    public BuildStepStatisticResource(BuildStepStatisticService buildStepStatisticService, BuildStepStatisticQueryService buildStepStatisticQueryService) {
        this.buildStepStatisticService = buildStepStatisticService;
        this.buildStepStatisticQueryService = buildStepStatisticQueryService;
    }

    /**
     * {@code POST  /build-step-statistics} : Create a new buildStepStatistic.
     *
     * @param buildStepStatisticDTO the buildStepStatisticDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new buildStepStatisticDTO, or with status {@code 400 (Bad Request)} if the buildStepStatistic has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/build-step-statistics")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildStepStatisticDTO> createBuildStepStatistic(@Valid @RequestBody BuildStepStatisticDTO buildStepStatisticDTO) throws URISyntaxException {
        log.debug("REST request to save BuildStepStatistic : {}", buildStepStatisticDTO);
        if (buildStepStatisticDTO.getId() != null) {
            throw new BadRequestAlertException("A new buildStepStatistic cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BuildStepStatisticDTO result = buildStepStatisticService.save(buildStepStatisticDTO);
        return ResponseEntity.created(new URI("/api/build-step-statistics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /build-step-statistics} : Updates an existing buildStepStatistic.
     *
     * @param buildStepStatisticDTO the buildStepStatisticDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildStepStatisticDTO,
     * or with status {@code 400 (Bad Request)} if the buildStepStatisticDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buildStepStatisticDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/build-step-statistics")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildStepStatisticDTO> updateBuildStepStatistic(@Valid @RequestBody BuildStepStatisticDTO buildStepStatisticDTO) throws URISyntaxException {
        log.debug("REST request to update BuildStepStatistic : {}", buildStepStatisticDTO);
        if (buildStepStatisticDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BuildStepStatisticDTO result = buildStepStatisticService.save(buildStepStatisticDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildStepStatisticDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /build-step-statistics} : get all the buildStepStatistics.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of buildStepStatistics in body.
     */
    @GetMapping("/build-step-statistics")
    public ResponseEntity<List<BuildStepStatisticDTO>> getAllBuildStepStatistics(BuildStepStatisticCriteria criteria, Pageable pageable) {
        log.debug("REST request to get BuildStepStatistics by criteria: {}", criteria);
        Page<BuildStepStatisticDTO> page = buildStepStatisticQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /build-step-statistics/count} : count all the buildStepStatistics.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/build-step-statistics/count")
    public ResponseEntity<Long> countBuildStepStatistics(BuildStepStatisticCriteria criteria) {
        log.debug("REST request to count BuildStepStatistics by criteria: {}", criteria);
        return ResponseEntity.ok().body(buildStepStatisticQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /build-step-statistics/:id} : get the "id" buildStepStatistic.
     *
     * @param id the id of the buildStepStatisticDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the buildStepStatisticDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/build-step-statistics/{id}")
    public ResponseEntity<BuildStepStatisticDTO> getBuildStepStatistic(@PathVariable Long id) {
        log.debug("REST request to get BuildStepStatistic : {}", id);
        Optional<BuildStepStatisticDTO> buildStepStatisticDTO = buildStepStatisticService.findOne(id);
        return ResponseUtil.wrapOrNotFound(buildStepStatisticDTO);
    }

    /**
     * {@code DELETE  /build-step-statistics/:id} : delete the "id" buildStepStatistic.
     *
     * @param id the id of the buildStepStatisticDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/build-step-statistics/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteBuildStepStatistic(@PathVariable Long id) {
        log.debug("REST request to delete BuildStepStatistic : {}", id);
        buildStepStatisticService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
