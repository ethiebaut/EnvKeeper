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

import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.BuildRepository;
import cc.envkeeper.app.service.dto.BuildCriteria;
import cc.envkeeper.app.service.dto.BuildDTO;
import cc.envkeeper.app.service.mapper.BuildMapper;

/**
 * Service for executing complex queries for {@link Build} entities in the database.
 * The main input is a {@link BuildCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BuildDTO} or a {@link Page} of {@link BuildDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BuildQueryService extends QueryService<Build> {

    private final Logger log = LoggerFactory.getLogger(BuildQueryService.class);

    private final BuildRepository buildRepository;

    private final BuildMapper buildMapper;

    public BuildQueryService(BuildRepository buildRepository, BuildMapper buildMapper) {
        this.buildRepository = buildRepository;
        this.buildMapper = buildMapper;
    }

    /**
     * Return a {@link List} of {@link BuildDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BuildDTO> findByCriteria(BuildCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Build> specification = createSpecification(criteria);
        return buildMapper.toDto(buildRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BuildDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildDTO> findByCriteria(BuildCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Build> specification = createSpecification(criteria);
        return buildRepository.findAll(specification, page)
            .map(buildMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BuildCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Build> specification = createSpecification(criteria);
        return buildRepository.count(specification);
    }

    /**
     * Function to convert {@link BuildCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Build> createSpecification(BuildCriteria criteria) {
        Specification<Build> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Build_.id));
            }
            if (criteria.getBuildUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBuildUrl(), Build_.buildUrl));
            }
            if (criteria.getBuildName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBuildName(), Build_.buildName));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Build_.status));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartTime(), Build_.startTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), Build_.endTime));
            }
            if (criteria.getBuildId() != null) {
                specification = specification.and(buildSpecification(criteria.getBuildId(),
                    root -> root.join(Build_.builds, JoinType.LEFT).get(Build_.id)));
            }
            if (criteria.getParentBuildId() != null) {
                specification = specification.and(buildSpecification(criteria.getParentBuildId(),
                    root -> root.join(Build_.parentBuild, JoinType.LEFT).get(Build_.id)));
            }
            if (criteria.getDeploymentId() != null) {
                specification = specification.and(buildSpecification(criteria.getDeploymentId(),
                    root -> root.join(Build_.deployments, JoinType.LEFT).get(Deployment_.id)));
            }
            if (criteria.getProductVersionId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductVersionId(),
                    root -> root.join(Build_.productVersions, JoinType.LEFT).get(ProductVersion_.id)));
            }
            if (criteria.getEnvironmentId() != null) {
                specification = specification.and(buildSpecification(criteria.getEnvironmentId(),
                                                                     root -> root.join(Build_.deployments, JoinType.LEFT)
                                                                                 .join(Deployment_.environment, JoinType.LEFT)
                                                                                 .get(Environment_.id)));
            }
        }
        return specification;
    }
}
