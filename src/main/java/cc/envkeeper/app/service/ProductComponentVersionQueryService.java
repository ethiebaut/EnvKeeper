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

package cc.envkeeper.app.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import cc.envkeeper.app.domain.ProductComponentVersion;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.ProductComponentVersionRepository;
import cc.envkeeper.app.service.dto.ProductComponentVersionCriteria;
import cc.envkeeper.app.service.dto.ProductComponentVersionDTO;
import cc.envkeeper.app.service.mapper.ProductComponentVersionMapper;

/**
 * Service for executing complex queries for {@link ProductComponentVersion} entities in the database.
 * The main input is a {@link ProductComponentVersionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductComponentVersionDTO} or a {@link Page} of {@link ProductComponentVersionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductComponentVersionQueryService extends QueryService<ProductComponentVersion> {

    private final Logger log = LoggerFactory.getLogger(ProductComponentVersionQueryService.class);

    private final ProductComponentVersionRepository productComponentVersionRepository;

    private final ProductComponentVersionMapper productComponentVersionMapper;

    public ProductComponentVersionQueryService(ProductComponentVersionRepository productComponentVersionRepository, ProductComponentVersionMapper productComponentVersionMapper) {
        this.productComponentVersionRepository = productComponentVersionRepository;
        this.productComponentVersionMapper = productComponentVersionMapper;
    }

    /**
     * Return a {@link List} of {@link ProductComponentVersionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductComponentVersionDTO> findByCriteria(ProductComponentVersionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductComponentVersion> specification = createSpecification(criteria);
        return productComponentVersionMapper.toDto(productComponentVersionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductComponentVersionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductComponentVersionDTO> findByCriteria(ProductComponentVersionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProductComponentVersion> specification = createSpecification(criteria);
        return productComponentVersionRepository.findAll(specification, page)
            .map(productComponentVersionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductComponentVersionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductComponentVersion> specification = createSpecification(criteria);
        return productComponentVersionRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductComponentVersionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductComponentVersion> createSpecification(ProductComponentVersionCriteria criteria) {
        Specification<ProductComponentVersion> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProductComponentVersion_.id));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVersion(), ProductComponentVersion_.version));
            }
            if (criteria.getBuildStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getBuildStatus(), ProductComponentVersion_.buildStatus));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartTime(), ProductComponentVersion_.startTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), ProductComponentVersion_.endTime));
            }
            if (criteria.getBuildUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBuildUrl(), ProductComponentVersion_.buildUrl));
            }
            if (criteria.getReleaseNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReleaseNotes(), ProductComponentVersion_.releaseNotes));
            }
            if (criteria.getComponentId() != null) {
                specification = specification.and(buildSpecification(criteria.getComponentId(),
                    root -> root.join(ProductComponentVersion_.component, JoinType.LEFT).get(ProductComponent_.id)));
            }
            if (criteria.getProductVersionId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductVersionId(),
                    root -> root.join(ProductComponentVersion_.productVersions, JoinType.LEFT).get(ProductVersion_.id)));
            }
        }
        return specification;
    }
}
