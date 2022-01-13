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

import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.repository.ProductVersionRepository;
import cc.envkeeper.app.service.dto.ProductCriteria;
import cc.envkeeper.app.service.dto.ProductDTO;
import cc.envkeeper.app.service.dto.ProductVersionCriteria;
import cc.envkeeper.app.service.dto.ProductVersionDTO;
import cc.envkeeper.app.service.mapper.ProductVersionMapper;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link ProductVersion}.
 */
@Service
@Transactional
public class ProductVersionService {

    private final Logger log = LoggerFactory.getLogger(ProductVersionService.class);

    private final ProductQueryService productQueryService;

    private final ProductVersionQueryService productVersionQueryService;

    private final ProductVersionRepository productVersionRepository;

    private final ProductVersionMapper productVersionMapper;

    public ProductVersionService(ProductQueryService productQueryService, ProductVersionQueryService productVersionQueryService, ProductVersionRepository productVersionRepository, ProductVersionMapper productVersionMapper) {
        this.productQueryService = productQueryService;
        this.productVersionQueryService = productVersionQueryService;
        this.productVersionRepository = productVersionRepository;
        this.productVersionMapper = productVersionMapper;
    }

    /**
     * Save a productVersion.
     *
     * @param productVersionDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductVersionDTO save(ProductVersionDTO productVersionDTO) {
        log.debug("Request to save ProductVersion : {}", productVersionDTO);
        if (productVersionDTO.getProductId() == null && !StringUtils.isBlank(productVersionDTO.getProductShortName())) {
            // Find product
            ProductCriteria productCriteria = new ProductCriteria();
            StringFilter productFilter = new StringFilter();
            productFilter.setEquals(productVersionDTO.getProductShortName());
            productCriteria.setShortName(productFilter);
            List<ProductDTO> products = productQueryService.findByCriteria(productCriteria);
            if (products.size() != 1) {
                throw new RuntimeException("Product " + productVersionDTO.getProductShortName() + " not found");
            }
            productVersionDTO.setProductId(products.get(0).getId());
        }

        // Check for dup on create & ignore
        if (productVersionDTO.getId() == null) {
            ProductVersionCriteria productVersionCriteria = new ProductVersionCriteria();
            LongFilter productFilter = new LongFilter();
            productFilter.setEquals(productVersionDTO.getProductId());
            productVersionCriteria.setProductId(productFilter);
            StringFilter versionFilter = new StringFilter();
            versionFilter.setEquals(productVersionDTO.getVersion());
            productVersionCriteria.setVersion(versionFilter);
            List<ProductVersionDTO> products = productVersionQueryService.findByCriteria(productVersionCriteria);
            if (products.size() > 0) {
                return products.get(0);
            }
        }

        ProductVersion productVersion = productVersionMapper.toEntity(productVersionDTO);
        productVersion = productVersionRepository.save(productVersion);
        return productVersionMapper.toDto(productVersion);
    }

    /**
     * Get all the productVersions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductVersionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductVersions");
        return productVersionRepository.findAll(pageable)
            .map(productVersionMapper::toDto);
    }


    /**
     * Get all the productVersions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductVersionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productVersionRepository.findAllWithEagerRelationships(pageable).map(productVersionMapper::toDto);
    }

    /**
     * Get one productVersion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductVersionDTO> findOne(Long id) {
        log.debug("Request to get ProductVersion : {}", id);
        return productVersionRepository.findOneWithEagerRelationships(id)
            .map(productVersionMapper::toDto);
    }

    /**
     * Delete the productVersion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductVersion : {}", id);
        productVersionRepository.deleteById(id);
    }
}
