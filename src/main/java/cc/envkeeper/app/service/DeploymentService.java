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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import cc.envkeeper.app.domain.Deployment;
import cc.envkeeper.app.domain.Environment;
import cc.envkeeper.app.domain.Environment_;
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.repository.DeploymentRepository;
import cc.envkeeper.app.service.dto.DeploymentDTO;
import cc.envkeeper.app.service.dto.EnvironmentCriteria;
import cc.envkeeper.app.service.dto.EnvironmentDTO;
import cc.envkeeper.app.service.dto.ProductCriteria;
import cc.envkeeper.app.service.dto.ProductDTO;
import cc.envkeeper.app.service.dto.ProductVersionCriteria;
import cc.envkeeper.app.service.dto.ProductVersionDTO;
import cc.envkeeper.app.service.mapper.DeploymentMapper;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Service Implementation for managing {@link Deployment}.
 */
@Service
@Transactional
public class DeploymentService {

    private final Logger log = LoggerFactory.getLogger(DeploymentService.class);

    private final EnvironmentQueryService environmentQueryService;

    private final ProductQueryService productQueryService;

    private final ProductVersionQueryService productVersionQueryService;

    private final ProductVersionService productVersionService;

    private final DeploymentRepository deploymentRepository;

    private final DeploymentMapper deploymentMapper;

    public DeploymentService(
        EnvironmentQueryService environmentQueryService,
        ProductQueryService productQueryService,
        ProductVersionQueryService productVersionQueryService,
        ProductVersionService productVersionService,
        DeploymentRepository deploymentRepository,
        DeploymentMapper deploymentMapper) {
        this.environmentQueryService = environmentQueryService;
        this.productQueryService = productQueryService;
        this.productVersionQueryService = productVersionQueryService;
        this.productVersionService = productVersionService;
        this.deploymentRepository = deploymentRepository;
        this.deploymentMapper = deploymentMapper;
    }

    /**
     * Save a deployment.
     *
     * @param deploymentDTO the entity to save.
     * @return the persisted entity.
     */
    public DeploymentDTO save(DeploymentDTO deploymentDTO) {
        log.debug("Request to save Deployment : {}", deploymentDTO);
        if (deploymentDTO.getEnvironmentId() == null && !StringUtils.isBlank(deploymentDTO.getEnvironmentShortName())) {
            // Find environment from Id
            EnvironmentCriteria criteria = new EnvironmentCriteria();
            StringFilter filter = new StringFilter();
            filter.setEquals(deploymentDTO.getEnvironmentShortName());
            criteria.setShortName(filter);
            List<EnvironmentDTO> environments = environmentQueryService.findByCriteria(criteria);
            if (environments.size() != 1) {
                throw new RuntimeException("Environment " + deploymentDTO.getEnvironmentShortName() + " not found");
            }
            deploymentDTO.setEnvironmentId(environments.get(0).getId());
        }
        if (deploymentDTO.getProductVersionId() == null
            && !StringUtils.isBlank(deploymentDTO.getProductVersionVersion())
            && deploymentDTO.getProduct() != null
            && !StringUtils.isBlank(deploymentDTO.getProduct().getShortName())
        ) {
            // Find product version from product shortname and version number

            // First, find product
            ProductCriteria productCriteria = new ProductCriteria();
            StringFilter productFilter = new StringFilter();
            productFilter.setEquals(deploymentDTO.getProduct().getShortName());
            productCriteria.setShortName(productFilter);
            List<ProductDTO> products = productQueryService.findByCriteria(productCriteria);
            if (products.size() != 1) {
                throw new RuntimeException("Product " + deploymentDTO.getProduct().getShortName() + " not found");
            }

            // Then the product version
            ProductVersionCriteria criteria = new ProductVersionCriteria();
            LongFilter productIdFilter = new LongFilter();
            productIdFilter.setEquals(products.get(0).getId());
            criteria.setProductId(productIdFilter);
            StringFilter filter = new StringFilter();
            filter.setEquals(deploymentDTO.getProductVersionVersion());
            criteria.setVersion(filter);
            List<ProductVersionDTO> productVersions = productVersionQueryService.findByCriteria(criteria);
            if (productVersions.size() == 0) {
                // Version doesn't exist, create it
                ProductVersionDTO productVersionDTO = new ProductVersionDTO();
                productVersionDTO.setProductShortName(products.get(0).getShortName());
                productVersionDTO.setProductId(products.get(0).getId());
                productVersionDTO.setVersion(deploymentDTO.getProductVersionVersion());
                productVersionDTO.setBuildId(deploymentDTO.getBuildId());
                deploymentDTO.setProductVersionId(productVersionService.save(productVersionDTO).getId());
            } else {
                // Version exists, use it
                deploymentDTO.setProductVersionId(productVersions.get(0).getId());
            }
        }
        Deployment deployment = deploymentMapper.toEntity(deploymentDTO);
        deployment = deploymentRepository.save(deployment);
        return deploymentMapper.toDto(deployment);
    }

    /**
     * Get all the deployments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DeploymentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Deployments");
        return deploymentRepository.findAll(pageable)
            .map(deploymentMapper::toDto);
    }


    /**
     * Get one deployment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DeploymentDTO> findOne(Long id) {
        log.debug("Request to get Deployment : {}", id);
        return deploymentRepository.findById(id)
            .map(deploymentMapper::toDto);
    }

    /**
     * Delete the deployment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Deployment : {}", id);
        deploymentRepository.deleteById(id);
    }
}
