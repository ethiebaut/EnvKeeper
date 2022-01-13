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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.criteria.JoinType;

import cc.envkeeper.app.domain.Build_;
import cc.envkeeper.app.domain.ProductComponentVersion_;
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.domain.ProductVersion_;
import cc.envkeeper.app.domain.Product_;
import cc.envkeeper.app.domain.Ticket;
import cc.envkeeper.app.repository.ProductVersionRepository;
import cc.envkeeper.app.service.dto.ProductVersionCriteria;
import cc.envkeeper.app.service.dto.ProductVersionDTO;
import cc.envkeeper.app.service.dto.TicketCriteria;
import cc.envkeeper.app.service.mapper.ProductVersionMapper;
import io.github.jhipster.service.QueryService;
import io.github.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link ProductVersion} entities in the database.
 * The main input is a {@link ProductVersionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductVersionDTO} or a {@link Page} of {@link ProductVersionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductVersionQueryService extends QueryService<ProductVersion> {

    private final Logger log = LoggerFactory.getLogger(ProductVersionQueryService.class);

    private final ProductVersionRepository productVersionRepository;

    private final ProductVersionMapper productVersionMapper;

    private final TicketQueryService ticketQueryService;

    public ProductVersionQueryService(
        ProductVersionRepository productVersionRepository,
        ProductVersionMapper productVersionMapper,
        TicketQueryService ticketQueryService) {
        this.productVersionRepository = productVersionRepository;
        this.productVersionMapper = productVersionMapper;
        this.ticketQueryService = ticketQueryService;
    }

    /**
     * Return a {@link List} of {@link ProductVersionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductVersionDTO> findByCriteria(ProductVersionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductVersion> specification = createSpecification(criteria);
        return productVersionMapper.toDto(productVersionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductVersionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductVersionDTO> findByCriteria(ProductVersionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProductVersion> specification = createSpecification(criteria);
        return productVersionRepository.findAll(specification, page)
            .map(productVersionMapper::toDto)
            .map(productVersionDTO -> {
                TicketCriteria ticketCriteria = new TicketCriteria();
                LongFilter fixedInFilter = new LongFilter();
                fixedInFilter.setEquals(productVersionDTO.getId());
                ticketCriteria.setFixedInId(fixedInFilter);
                productVersionDTO.setFixedTickets(ticketQueryService.findByCriteria(ticketCriteria));
                return productVersionDTO;
            });
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductVersionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductVersion> specification = createSpecification(criteria);
        return productVersionRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductVersionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductVersion> createSpecification(ProductVersionCriteria criteria) {
        Specification<ProductVersion> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProductVersion_.id));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVersion(), ProductVersion_.version));
            }
            if (criteria.getReleaseNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReleaseNotes(), ProductVersion_.releaseNotes));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductId(),
                    root -> root.join(ProductVersion_.product, JoinType.LEFT).get(Product_.id)));
            }
            if (criteria.getBuildId() != null) {
                specification = specification.and(buildSpecification(criteria.getBuildId(),
                    root -> root.join(ProductVersion_.build, JoinType.LEFT).get(Build_.id)));
            }
            if (criteria.getComponentId() != null) {
                specification = specification.and(buildSpecification(criteria.getComponentId(),
                    root -> root.join(ProductVersion_.components, JoinType.LEFT).get(ProductComponentVersion_.id)));
            }
        }
        return specification;
    }
}
