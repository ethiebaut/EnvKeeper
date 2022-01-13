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

import cc.envkeeper.app.domain.EnvironmentGroup;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.EnvironmentGroupRepository;
import cc.envkeeper.app.service.dto.EnvironmentGroupCriteria;
import cc.envkeeper.app.service.dto.EnvironmentGroupDTO;
import cc.envkeeper.app.service.mapper.EnvironmentGroupMapper;

/**
 * Service for executing complex queries for {@link EnvironmentGroup} entities in the database.
 * The main input is a {@link EnvironmentGroupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EnvironmentGroupDTO} or a {@link Page} of {@link EnvironmentGroupDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EnvironmentGroupQueryService extends QueryService<EnvironmentGroup> {

    private final Logger log = LoggerFactory.getLogger(EnvironmentGroupQueryService.class);

    private final EnvironmentGroupRepository environmentGroupRepository;

    private final EnvironmentGroupMapper environmentGroupMapper;

    public EnvironmentGroupQueryService(EnvironmentGroupRepository environmentGroupRepository, EnvironmentGroupMapper environmentGroupMapper) {
        this.environmentGroupRepository = environmentGroupRepository;
        this.environmentGroupMapper = environmentGroupMapper;
    }

    /**
     * Return a {@link List} of {@link EnvironmentGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EnvironmentGroupDTO> findByCriteria(EnvironmentGroupCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<EnvironmentGroup> specification = createSpecification(criteria);
        return environmentGroupMapper.toDto(environmentGroupRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EnvironmentGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EnvironmentGroupDTO> findByCriteria(EnvironmentGroupCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EnvironmentGroup> specification = createSpecification(criteria);
        return environmentGroupRepository.findAll(specification, page)
            .map(environmentGroupMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EnvironmentGroupCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<EnvironmentGroup> specification = createSpecification(criteria);
        return environmentGroupRepository.count(specification);
    }

    /**
     * Function to convert {@link EnvironmentGroupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EnvironmentGroup> createSpecification(EnvironmentGroupCriteria criteria) {
        Specification<EnvironmentGroup> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), EnvironmentGroup_.id));
            }
            if (criteria.getShortName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getShortName(), EnvironmentGroup_.shortName));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), EnvironmentGroup_.fullName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), EnvironmentGroup_.description));
            }
            if (criteria.getSortOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSortOrder(), EnvironmentGroup_.sortOrder));
            }
            if (criteria.getHidden() != null) {
                specification = specification.and(buildSpecification(criteria.getHidden(), EnvironmentGroup_.hidden));
            }
        }
        return specification;
    }
}
