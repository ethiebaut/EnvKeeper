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

import cc.envkeeper.app.domain.BuildStep;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.BuildStepRepository;
import cc.envkeeper.app.service.dto.BuildStepCriteria;
import cc.envkeeper.app.service.dto.BuildStepDTO;
import cc.envkeeper.app.service.mapper.BuildStepMapper;

/**
 * Service for executing complex queries for {@link BuildStep} entities in the database.
 * The main input is a {@link BuildStepCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BuildStepDTO} or a {@link Page} of {@link BuildStepDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BuildStepQueryService extends QueryService<BuildStep> {

    private final Logger log = LoggerFactory.getLogger(BuildStepQueryService.class);

    private final BuildStepRepository buildStepRepository;

    private final BuildStepMapper buildStepMapper;

    public BuildStepQueryService(BuildStepRepository buildStepRepository, BuildStepMapper buildStepMapper) {
        this.buildStepRepository = buildStepRepository;
        this.buildStepMapper = buildStepMapper;
    }

    /**
     * Return a {@link List} of {@link BuildStepDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BuildStepDTO> findByCriteria(BuildStepCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BuildStep> specification = createSpecification(criteria);
        return buildStepMapper.toDto(buildStepRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BuildStepDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildStepDTO> findByCriteria(BuildStepCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BuildStep> specification = createSpecification(criteria);
        return buildStepRepository.findAll(specification, page)
            .map(buildStepMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BuildStepCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BuildStep> specification = createSpecification(criteria);
        return buildStepRepository.count(specification);
    }

    /**
     * Function to convert {@link BuildStepCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BuildStep> createSpecification(BuildStepCriteria criteria) {
        Specification<BuildStep> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BuildStep_.id));
            }
            if (criteria.getStep() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStep(), BuildStep_.step));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), BuildStep_.status));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartTime(), BuildStep_.startTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), BuildStep_.endTime));
            }
            if (criteria.getBuildId() != null) {
                specification = specification.and(buildSpecification(criteria.getBuildId(),
                    root -> root.join(BuildStep_.build, JoinType.LEFT).get(Build_.id)));
            }
        }
        return specification;
    }
}
