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

import cc.envkeeper.app.domain.BuildStepStatistic;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.BuildStepStatisticRepository;
import cc.envkeeper.app.service.dto.BuildStepStatisticCriteria;
import cc.envkeeper.app.service.dto.BuildStepStatisticDTO;
import cc.envkeeper.app.service.mapper.BuildStepStatisticMapper;

/**
 * Service for executing complex queries for {@link BuildStepStatistic} entities in the database.
 * The main input is a {@link BuildStepStatisticCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BuildStepStatisticDTO} or a {@link Page} of {@link BuildStepStatisticDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BuildStepStatisticQueryService extends QueryService<BuildStepStatistic> {

    private final Logger log = LoggerFactory.getLogger(BuildStepStatisticQueryService.class);

    private final BuildStepStatisticRepository buildStepStatisticRepository;

    private final BuildStepStatisticMapper buildStepStatisticMapper;

    public BuildStepStatisticQueryService(BuildStepStatisticRepository buildStepStatisticRepository, BuildStepStatisticMapper buildStepStatisticMapper) {
        this.buildStepStatisticRepository = buildStepStatisticRepository;
        this.buildStepStatisticMapper = buildStepStatisticMapper;
    }

    /**
     * Return a {@link List} of {@link BuildStepStatisticDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BuildStepStatisticDTO> findByCriteria(BuildStepStatisticCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BuildStepStatistic> specification = createSpecification(criteria);
        return buildStepStatisticMapper.toDto(buildStepStatisticRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BuildStepStatisticDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildStepStatisticDTO> findByCriteria(BuildStepStatisticCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BuildStepStatistic> specification = createSpecification(criteria);
        return buildStepStatisticRepository.findAll(specification, page)
            .map(buildStepStatisticMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BuildStepStatisticCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BuildStepStatistic> specification = createSpecification(criteria);
        return buildStepStatisticRepository.count(specification);
    }

    /**
     * Function to convert {@link BuildStepStatisticCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BuildStepStatistic> createSpecification(BuildStepStatisticCriteria criteria) {
        Specification<BuildStepStatistic> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BuildStepStatistic_.id));
            }
            if (criteria.getKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKey(), BuildStepStatistic_.key));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValue(), BuildStepStatistic_.value));
            }
            if (criteria.getBuildStepId() != null) {
                specification = specification.and(buildSpecification(criteria.getBuildStepId(),
                    root -> root.join(BuildStepStatistic_.buildStep, JoinType.LEFT).get(BuildStep_.id)));
            }
        }
        return specification;
    }
}
