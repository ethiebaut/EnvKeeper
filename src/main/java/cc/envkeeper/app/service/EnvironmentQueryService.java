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

import cc.envkeeper.app.domain.Environment;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.EnvironmentRepository;
import cc.envkeeper.app.service.dto.EnvironmentCriteria;
import cc.envkeeper.app.service.dto.EnvironmentDTO;
import cc.envkeeper.app.service.mapper.EnvironmentMapper;

/**
 * Service for executing complex queries for {@link Environment} entities in the database.
 * The main input is a {@link EnvironmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EnvironmentDTO} or a {@link Page} of {@link EnvironmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EnvironmentQueryService extends QueryService<Environment> {

    private final Logger log = LoggerFactory.getLogger(EnvironmentQueryService.class);

    private final EnvironmentRepository environmentRepository;

    private final EnvironmentMapper environmentMapper;

    public EnvironmentQueryService(EnvironmentRepository environmentRepository, EnvironmentMapper environmentMapper) {
        this.environmentRepository = environmentRepository;
        this.environmentMapper = environmentMapper;
    }

    /**
     * Return a {@link List} of {@link EnvironmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EnvironmentDTO> findByCriteria(EnvironmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Environment> specification = createSpecification(criteria);
        return environmentMapper.toDto(environmentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EnvironmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EnvironmentDTO> findByCriteria(EnvironmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Environment> specification = createSpecification(criteria);
        return environmentRepository.findAll(specification, page)
            .map(environmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EnvironmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Environment> specification = createSpecification(criteria);
        return environmentRepository.count(specification);
    }

    /**
     * Function to convert {@link EnvironmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Environment> createSpecification(EnvironmentCriteria criteria) {
        Specification<Environment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Environment_.id));
            }
            if (criteria.getShortName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getShortName(), Environment_.shortName));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), Environment_.fullName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Environment_.description));
            }
            if (criteria.getSortOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSortOrder(), Environment_.sortOrder));
            }
            if (criteria.getEnvironmentGroupId() != null) {
                specification = specification.and(buildSpecification(criteria.getEnvironmentGroupId(),
                    root -> root.join(Environment_.environmentGroup, JoinType.LEFT).get(EnvironmentGroup_.id)));
            }
        }
        return specification;
    }
}
