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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cc.envkeeper.app.service.dto.DeploymentTableCellDTO;
import cc.envkeeper.app.service.dto.DeploymentTableRowDTO;
import cc.envkeeper.app.service.dto.ProductDTO;
import cc.envkeeper.app.service.mapper.DeploymentTableMapper;
import cc.envkeeper.app.service.mapper.ProductMapper;
import cc.envkeeper.app.web.rest.DeploymentResource;
import cc.envkeeper.app.service.exception.BadRequestAlertException;
import io.github.jhipster.service.QueryService;

import cc.envkeeper.app.domain.Deployment;
import cc.envkeeper.app.domain.*; // for static metamodels
import cc.envkeeper.app.repository.DeploymentRepository;
import cc.envkeeper.app.service.dto.DeploymentCriteria;
import cc.envkeeper.app.service.dto.DeploymentDTO;
import cc.envkeeper.app.service.mapper.DeploymentMapper;

/**
 * Service for executing complex queries for {@link Deployment} entities in the database.
 * The main input is a {@link DeploymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DeploymentDTO} or a {@link Page} of {@link DeploymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeploymentQueryService extends QueryService<Deployment> {

    private final Logger log = LoggerFactory.getLogger(DeploymentQueryService.class);

    private final DeploymentRepository deploymentRepository;

    private final DeploymentMapper deploymentMapper;

    private final DeploymentTableMapper deploymentTableMapper;

    private final ProductMapper productMapper;

    public DeploymentQueryService(
        DeploymentRepository deploymentRepository,
        DeploymentMapper deploymentMapper,
        DeploymentTableMapper deploymentTableMapper,
        ProductMapper productMapper) {
        this.deploymentRepository = deploymentRepository;
        this.deploymentMapper = deploymentMapper;
        this.deploymentTableMapper = deploymentTableMapper;
        this.productMapper = productMapper;
    }

    /**
     * Return a {@link List} of the latest deployments {@link DeploymentDTO}
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DeploymentDTO> findLatest(Instant asOf) {
        log.debug("find latest");
        if (asOf == null) {
            return deploymentMapper.toDto(deploymentRepository.findLatest());
        } else {
            return deploymentMapper.toDto(deploymentRepository.findLatestAsOf(asOf));
        }
    }

    /**
     * Return a {@link List} of {@link DeploymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DeploymentDTO> findByCriteria(DeploymentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Deployment> specification = createSpecification(criteria);
        return deploymentMapper.toDto(deploymentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DeploymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeploymentDTO> findByCriteria(DeploymentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Deployment> specification = createSpecification(criteria);
        return deploymentRepository.findAll(specification, page)
            .map(deploymentMapper::toDto);
    }

    /**
     * Return a {@link List} of {@link DeploymentTableRowDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeploymentTableRowDTO> findTableByCriteria(DeploymentCriteria criteria, Pageable page) {
        log.debug("find table by criteria : {}", criteria);

        // Check sort order, we only support startTime DESC
        if (page.getSort() == null
            || page.getSort().stream().count() != 1
            || page.getSort().getOrderFor("startTime").getDirection() != Sort.Direction.DESC
        ) {
            throw new BadRequestAlertException("Invalid Sort Order", DeploymentResource.ENTITY_NAME, "invalidSortOrder");
        }

        final Specification<Deployment> specification = createSpecification(criteria);
        List<Deployment> deployments = deploymentRepository.findAll(specification);

        // Sort deployments by time DESC
        deployments.sort(Comparator.comparing(Deployment::getStartTime).reversed());

        // Extract all products
        Map<Long, ProductDTO> products = new HashMap<>();
        List<Long> productIds = new ArrayList<>();
        for (Deployment deployment: deployments) {
            long productId = deployment.getProductVersion().getProduct().getId();
            if (!products.containsKey(productId)) {
                products.put(productId, productMapper.toDto(deployment.getProductVersion().getProduct()));
                productIds.add(productId);
            }
        }
        productIds.sort(Comparator.comparing(a -> products.get(a).getShortName()));

        // Convert all deployments to a table by going from the last one to the most recent one
        Map<Long, ProductVersion> productVersions = new HashMap<>();
        List<DeploymentTableRowDTO> deploymentTable = new ArrayList<>();
        for (int i = deployments.size() - 1; i >= 0; i--) {
            Deployment deployment = deployments.get(i);
            long upgradedProductId = deployment.getProductVersion().getProduct().getId();
            List<DeploymentTableCellDTO> deploymentTableCellDTOs = new ArrayList<>();
            for (long productId: productIds) {

                // Create new cell
                DeploymentTableCellDTO deploymentTableCellDTO = new DeploymentTableCellDTO();
                deploymentTableCellDTO.setProduct(products.get(productId));

                // Add previous version
                ProductVersion productVersion = productVersions.get(productId);
                if (productVersion == null) {
                    deploymentTableCellDTO.setPreviousProductVersionId(null);
                    deploymentTableCellDTO.setPreviousProductVersionVersion(null);
                } else {
                    deploymentTableCellDTO.setPreviousProductVersionId(productVersion.getId());
                    deploymentTableCellDTO.setPreviousProductVersionVersion(productVersion.getVersion());
                }

                // Upgrading this product?
                if (productId == upgradedProductId) {
                    switch (deployment.getStatus()) {
                        case DELETED:
                            productVersion = null;
                            productVersions.put(productId, productVersion);
                            break;
                        case FAILED_ROLLED_BACK:
                            // Nothing to do
                            break;
                        default:
                            productVersion = deployment.getProductVersion();
                            productVersions.put(productId, productVersion);
                    }
                }

                // Add new version
                if (productVersion == null) {
                    deploymentTableCellDTO.setProductVersionId(null);
                    deploymentTableCellDTO.setProductVersionVersion(null);
                } else {
                    deploymentTableCellDTO.setProductVersionId(productVersion.getId());
                    deploymentTableCellDTO.setProductVersionVersion(productVersion.getVersion());
                }
                deploymentTableCellDTOs.add(deploymentTableCellDTO);
            }

            // Add row
            DeploymentTableRowDTO deploymentTableRowDTO = deploymentTableMapper.toDto(deployment);
            deploymentTableRowDTO.setDeploymentTableCells(deploymentTableCellDTOs);
            deploymentTable.add(0, deploymentTableRowDTO);
        }

        int from = page.getPageNumber() * page.getPageSize();
        int nextOne = (page.getPageNumber() + 1 ) * page.getPageSize();
        if (nextOne > deploymentTable.size()) {
            nextOne = deploymentTable.size();
        }
        if (from > nextOne) {
            from = nextOne;
        }
        if (from >= deploymentTable.size()) {
            return new PageImpl(new ArrayList<>(), page, deploymentTable.size());
        } else {
            return new PageImpl(deploymentTable.subList(from, nextOne), page, deploymentTable.size());
        }
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeploymentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Deployment> specification = createSpecification(criteria);
        return deploymentRepository.count(specification);
    }

    /**
     * Function to convert {@link DeploymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Deployment> createSpecification(DeploymentCriteria criteria) {
        Specification<Deployment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Deployment_.id));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartTime(), Deployment_.startTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), Deployment_.endTime));
            }
            if (criteria.getUser() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUser(), Deployment_.user));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Deployment_.status));
            }
            if (criteria.getNamespace() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNamespace(), Deployment_.namespace));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Deployment_.url));
            }
            if (criteria.getTestUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTestUrl(), Deployment_.testUrl));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductId(),
                                                                     root -> root.join(Deployment_.productVersion, JoinType.LEFT)
                                                                                 .join(ProductVersion_.product, JoinType.LEFT)
                                                                                 .get(Product_.id)));
            }
            if (criteria.getProductVersionId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductVersionId(),
                    root -> root.join(Deployment_.productVersion, JoinType.LEFT).get(ProductVersion_.id)));
            }
            if (criteria.getEnvironmentId() != null) {
                specification = specification.and(buildSpecification(criteria.getEnvironmentId(),
                    root -> root.join(Deployment_.environment, JoinType.LEFT).get(Environment_.id)));
            }
            if (criteria.getBuildId() != null) {
                specification = specification.and(buildSpecification(criteria.getBuildId(),
                    root -> root.join(Deployment_.build, JoinType.LEFT).get(Build_.id)));
            }
        }
        return specification;
    }
}
