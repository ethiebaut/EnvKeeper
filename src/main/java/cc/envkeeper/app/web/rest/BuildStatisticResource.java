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
import cc.envkeeper.app.service.BuildStatisticService;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.BuildStatisticDTO;
import cc.envkeeper.app.service.dto.BuildStatisticCriteria;
import cc.envkeeper.app.service.BuildStatisticQueryService;

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
 * REST controller for managing {@link cc.envkeeper.app.domain.BuildStatistic}.
 */
@RestController
@RequestMapping("/api")
public class BuildStatisticResource {

    private final Logger log = LoggerFactory.getLogger(BuildStatisticResource.class);

    private static final String ENTITY_NAME = "buildStatistic";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BuildStatisticService buildStatisticService;

    private final BuildStatisticQueryService buildStatisticQueryService;

    public BuildStatisticResource(BuildStatisticService buildStatisticService, BuildStatisticQueryService buildStatisticQueryService) {
        this.buildStatisticService = buildStatisticService;
        this.buildStatisticQueryService = buildStatisticQueryService;
    }

    /**
     * {@code POST  /build-statistics} : Create a new buildStatistic.
     *
     * @param buildStatisticDTO the buildStatisticDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new buildStatisticDTO, or with status {@code 400 (Bad Request)} if the buildStatistic has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/build-statistics")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildStatisticDTO> createBuildStatistic(@Valid @RequestBody BuildStatisticDTO buildStatisticDTO) throws URISyntaxException {
        log.debug("REST request to save BuildStatistic : {}", buildStatisticDTO);
        if (buildStatisticDTO.getId() != null) {
            throw new BadRequestAlertException("A new buildStatistic cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BuildStatisticDTO result = buildStatisticService.save(buildStatisticDTO);
        return ResponseEntity.created(new URI("/api/build-statistics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /build-statistics} : Updates an existing buildStatistic.
     *
     * @param buildStatisticDTO the buildStatisticDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildStatisticDTO,
     * or with status {@code 400 (Bad Request)} if the buildStatisticDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buildStatisticDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/build-statistics")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<BuildStatisticDTO> updateBuildStatistic(@Valid @RequestBody BuildStatisticDTO buildStatisticDTO) throws URISyntaxException {
        log.debug("REST request to update BuildStatistic : {}", buildStatisticDTO);
        if (buildStatisticDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BuildStatisticDTO result = buildStatisticService.save(buildStatisticDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildStatisticDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /build-statistics} : get all the buildStatistics.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of buildStatistics in body.
     */
    @GetMapping("/build-statistics")
    public ResponseEntity<List<BuildStatisticDTO>> getAllBuildStatistics(BuildStatisticCriteria criteria, Pageable pageable) {
        log.debug("REST request to get BuildStatistics by criteria: {}", criteria);
        Page<BuildStatisticDTO> page = buildStatisticQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /build-statistics/count} : count all the buildStatistics.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/build-statistics/count")
    public ResponseEntity<Long> countBuildStatistics(BuildStatisticCriteria criteria) {
        log.debug("REST request to count BuildStatistics by criteria: {}", criteria);
        return ResponseEntity.ok().body(buildStatisticQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /build-statistics/:id} : get the "id" buildStatistic.
     *
     * @param id the id of the buildStatisticDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the buildStatisticDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/build-statistics/{id}")
    public ResponseEntity<BuildStatisticDTO> getBuildStatistic(@PathVariable Long id) {
        log.debug("REST request to get BuildStatistic : {}", id);
        Optional<BuildStatisticDTO> buildStatisticDTO = buildStatisticService.findOne(id);
        return ResponseUtil.wrapOrNotFound(buildStatisticDTO);
    }

    /**
     * {@code DELETE  /build-statistics/:id} : delete the "id" buildStatistic.
     *
     * @param id the id of the buildStatisticDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/build-statistics/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteBuildStatistic(@PathVariable Long id) {
        log.debug("REST request to delete BuildStatistic : {}", id);
        buildStatisticService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
