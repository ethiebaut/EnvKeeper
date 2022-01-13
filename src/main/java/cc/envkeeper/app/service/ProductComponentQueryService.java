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

import cc.envkeeper.app.domain.ProductComponent;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.ProductComponentRepository;
import cc.envkeeper.app.service.dto.ProductComponentCriteria;
import cc.envkeeper.app.service.dto.ProductComponentDTO;
import cc.envkeeper.app.service.mapper.ProductComponentMapper;

/**
 * Service for executing complex queries for {@link ProductComponent} entities in the database.
 * The main input is a {@link ProductComponentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductComponentDTO} or a {@link Page} of {@link ProductComponentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductComponentQueryService extends QueryService<ProductComponent> {

    private final Logger log = LoggerFactory.getLogger(ProductComponentQueryService.class);

    private final ProductComponentRepository productComponentRepository;

    private final ProductComponentMapper productComponentMapper;

    public ProductComponentQueryService(ProductComponentRepository productComponentRepository, ProductComponentMapper productComponentMapper) {
        this.productComponentRepository = productComponentRepository;
        this.productComponentMapper = productComponentMapper;
    }

    /**
     * Return a {@link List} of {@link ProductComponentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductComponentDTO> findByCriteria(ProductComponentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductComponent> specification = createSpecification(criteria);
        return productComponentMapper.toDto(productComponentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductComponentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductComponentDTO> findByCriteria(ProductComponentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProductComponent> specification = createSpecification(criteria);
        return productComponentRepository.findAll(specification, page)
            .map(productComponentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductComponentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductComponent> specification = createSpecification(criteria);
        return productComponentRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductComponentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductComponent> createSpecification(ProductComponentCriteria criteria) {
        Specification<ProductComponent> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProductComponent_.id));
            }
            if (criteria.getShortName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getShortName(), ProductComponent_.shortName));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), ProductComponent_.fullName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), ProductComponent_.description));
            }
        }
        return specification;
    }
}
