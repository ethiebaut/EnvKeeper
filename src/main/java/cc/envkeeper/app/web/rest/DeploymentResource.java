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
import cc.envkeeper.app.service.DeploymentService;
import cc.envkeeper.app.service.dto.DeploymentTableRowDTO;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import cc.envkeeper.app.service.dto.DeploymentDTO;
import cc.envkeeper.app.service.dto.DeploymentCriteria;
import cc.envkeeper.app.service.DeploymentQueryService;

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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link cc.envkeeper.app.domain.Deployment}.
 */
@RestController
@RequestMapping("/api")
public class DeploymentResource {

    private final Logger log = LoggerFactory.getLogger(DeploymentResource.class);

    public static final String ENTITY_NAME = "deployment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeploymentService deploymentService;

    private final DeploymentQueryService deploymentQueryService;

    public DeploymentResource(DeploymentService deploymentService, DeploymentQueryService deploymentQueryService) {
        this.deploymentService = deploymentService;
        this.deploymentQueryService = deploymentQueryService;
    }

    /**
     * {@code POST  /deployments} : Create a new deployment.
     *
     * @param deploymentDTO the deploymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deploymentDTO, or with status {@code 400 (Bad Request)} if the deployment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/deployments")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<DeploymentDTO> createDeployment(@Valid @RequestBody DeploymentDTO deploymentDTO) throws URISyntaxException {
        log.debug("REST request to save Deployment : {}", deploymentDTO);
        if (deploymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new deployment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DeploymentDTO result = deploymentService.save(deploymentDTO);
        return ResponseEntity.created(new URI("/api/deployments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /deployments/bulk} : Create several new deployments.
     *
     * @param deploymentDTOs the deploymentDTO objects to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deploymentDTOs
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/deployments/bulk")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<List<DeploymentDTO>> createDeployments(@Valid @RequestBody DeploymentDTO[] deploymentDTOs) throws URISyntaxException {
        log.debug("REST request to save Deployments : {}", deploymentDTOs);

        List<DeploymentDTO> ret = new ArrayList<>();

        for (DeploymentDTO deploymentDTO: deploymentDTOs) {
            ResponseEntity<DeploymentDTO> thisRet = createDeployment(deploymentDTO);
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
     * {@code PUT  /deployments} : Updates an existing deployment.
     *
     * @param deploymentDTO the deploymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deploymentDTO,
     * or with status {@code 400 (Bad Request)} if the deploymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deploymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/deployments")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<DeploymentDTO> updateDeployment(@Valid @RequestBody DeploymentDTO deploymentDTO) throws URISyntaxException {
        log.debug("REST request to update Deployment : {}", deploymentDTO);
        if (deploymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DeploymentDTO result = deploymentService.save(deploymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deploymentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /deployments} : Patches an existing deployment.
     *
     * @param deploymentDTO the deploymentDTO to patch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deploymentDTO,
     * or with status {@code 400 (Bad Request)} if the deploymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deploymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/deployments")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<DeploymentDTO> patchDeployment(@RequestBody DeploymentDTO deploymentDTO) throws URISyntaxException {
        log.debug("REST request to patch Deployment : {}", deploymentDTO);
        if (deploymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<DeploymentDTO> old = deploymentService.findOne(deploymentDTO.getId());
        if (!old.isPresent()) {
            return ResponseUtil.wrapOrNotFound(old);
        }
        DeploymentDTO oldDTO = old.get();
        if (deploymentDTO.getStartTime() != null) {
            oldDTO.setStartTime(deploymentDTO.getStartTime());
        }
        if (deploymentDTO.getEndTime() != null) {
            oldDTO.setEndTime(deploymentDTO.getEndTime());
        }
        if (deploymentDTO.getUser() != null) {
            oldDTO.setUser(deploymentDTO.getUser());
        }
        if (deploymentDTO.getStatus() != null) {
            oldDTO.setStatus(deploymentDTO.getStatus());
        }
        if (deploymentDTO.getNamespace() != null) {
            oldDTO.setNamespace(deploymentDTO.getNamespace());
        }
        if (deploymentDTO.getUrl() != null) {
            oldDTO.setUrl(deploymentDTO.getUrl());
        }
        if (deploymentDTO.getTestUrl() != null) {
            oldDTO.setTestUrl(deploymentDTO.getTestUrl());
        }
        if (deploymentDTO.getProductVersionId() != null) {
            oldDTO.setProductVersionId(deploymentDTO.getProductVersionId());
        }
        if (deploymentDTO.getEnvironmentId() != null) {
            oldDTO.setEnvironmentId(deploymentDTO.getEnvironmentId());
        }
        if (deploymentDTO.getProduct() != null) {
            oldDTO.setProduct(deploymentDTO.getProduct());
        }
        if (deploymentDTO.getEnvironment() != null) {
            oldDTO.setEnvironment(deploymentDTO.getEnvironment());
        }
        if (deploymentDTO.getBuildId() != null) {
            oldDTO.setBuildId(deploymentDTO.getBuildId());
        }
        DeploymentDTO result = deploymentService.save(oldDTO);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deploymentDTO.getId().toString()))
                             .body(result);
    }

    /**
     * {@code GET  /deployments} : get all the deployments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deployments in body.
     */
    @GetMapping("/deployments")
    public ResponseEntity<List<DeploymentDTO>> getAllDeployments(DeploymentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Deployments by criteria: {}", criteria);
        Page<DeploymentDTO> page = deploymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /deployments/table} : get all the deployments in table format.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deployments in body.
     */
    @GetMapping("/deployments/table")
    public ResponseEntity<List<DeploymentTableRowDTO>> getAllDeploymentTable(DeploymentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Deployments by criteria: {}", criteria);
        Page<DeploymentTableRowDTO> page = deploymentQueryService.findTableByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /deployments/latest} : get latest deployments for al environments & products.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deployments in body.
     */
    @GetMapping("/deployments/latest")
    public ResponseEntity<List<DeploymentDTO>> getLatestDeployments(Instant asOf) {
        log.debug("REST request to get latest Deployments");
        List<DeploymentDTO> result = deploymentQueryService.findLatest(asOf);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code GET  /deployments/count} : count all the deployments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/deployments/count")
    public ResponseEntity<Long> countDeployments(DeploymentCriteria criteria) {
        log.debug("REST request to count Deployments by criteria: {}", criteria);
        return ResponseEntity.ok().body(deploymentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /deployments/:id} : get the "id" deployment.
     *
     * @param id the id of the deploymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deploymentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/deployments/{id}")
    public ResponseEntity<DeploymentDTO> getDeployment(@PathVariable Long id) {
        log.debug("REST request to get Deployment : {}", id);
        Optional<DeploymentDTO> deploymentDTO = deploymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deploymentDTO);
    }

    /**
     * {@code DELETE  /deployments/:id} : delete the "id" deployment.
     *
     * @param id the id of the deploymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/deployments/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.WRITER + "\")")
    public ResponseEntity<Void> deleteDeployment(@PathVariable Long id) {
        log.debug("REST request to delete Deployment : {}", id);
        deploymentService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
