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

import cc.envkeeper.app.domain.BuildStatistic;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.BuildStatisticRepository;
import cc.envkeeper.app.service.dto.BuildStatisticCriteria;
import cc.envkeeper.app.service.dto.BuildStatisticDTO;
import cc.envkeeper.app.service.mapper.BuildStatisticMapper;

/**
 * Service for executing complex queries for {@link BuildStatistic} entities in the database.
 * The main input is a {@link BuildStatisticCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BuildStatisticDTO} or a {@link Page} of {@link BuildStatisticDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BuildStatisticQueryService extends QueryService<BuildStatistic> {

    private final Logger log = LoggerFactory.getLogger(BuildStatisticQueryService.class);

    private final BuildStatisticRepository buildStatisticRepository;

    private final BuildStatisticMapper buildStatisticMapper;

    public BuildStatisticQueryService(BuildStatisticRepository buildStatisticRepository, BuildStatisticMapper buildStatisticMapper) {
        this.buildStatisticRepository = buildStatisticRepository;
        this.buildStatisticMapper = buildStatisticMapper;
    }

    /**
     * Return a {@link List} of {@link BuildStatisticDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BuildStatisticDTO> findByCriteria(BuildStatisticCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BuildStatistic> specification = createSpecification(criteria);
        return buildStatisticMapper.toDto(buildStatisticRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BuildStatisticDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildStatisticDTO> findByCriteria(BuildStatisticCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BuildStatistic> specification = createSpecification(criteria);
        return buildStatisticRepository.findAll(specification, page)
            .map(buildStatisticMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BuildStatisticCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BuildStatistic> specification = createSpecification(criteria);
        return buildStatisticRepository.count(specification);
    }

    /**
     * Function to convert {@link BuildStatisticCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BuildStatistic> createSpecification(BuildStatisticCriteria criteria) {
        Specification<BuildStatistic> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BuildStatistic_.id));
            }
            if (criteria.getKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKey(), BuildStatistic_.key));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValue(), BuildStatistic_.value));
            }
            if (criteria.getBuildId() != null) {
                specification = specification.and(buildSpecification(criteria.getBuildId(),
                    root -> root.join(BuildStatistic_.build, JoinType.LEFT).get(Build_.id)));
            }
        }
        return specification;
    }
}
