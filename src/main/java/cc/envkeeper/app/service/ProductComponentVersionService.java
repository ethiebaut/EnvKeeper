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

import cc.envkeeper.app.domain.ProductComponentVersion;
import cc.envkeeper.app.repository.ProductComponentVersionRepository;
import cc.envkeeper.app.service.dto.ProductComponentVersionDTO;
import cc.envkeeper.app.service.mapper.ProductComponentVersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link ProductComponentVersion}.
 */
@Service
@Transactional
public class ProductComponentVersionService {

    private final Logger log = LoggerFactory.getLogger(ProductComponentVersionService.class);

    private final ProductComponentVersionRepository productComponentVersionRepository;

    private final ProductComponentVersionMapper productComponentVersionMapper;

    public ProductComponentVersionService(ProductComponentVersionRepository productComponentVersionRepository, ProductComponentVersionMapper productComponentVersionMapper) {
        this.productComponentVersionRepository = productComponentVersionRepository;
        this.productComponentVersionMapper = productComponentVersionMapper;
    }

    /**
     * Save a productComponentVersion.
     *
     * @param productComponentVersionDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductComponentVersionDTO save(ProductComponentVersionDTO productComponentVersionDTO) {
        log.debug("Request to save ProductComponentVersion : {}", productComponentVersionDTO);
        ProductComponentVersion productComponentVersion = productComponentVersionMapper.toEntity(productComponentVersionDTO);
        productComponentVersion = productComponentVersionRepository.save(productComponentVersion);
        return productComponentVersionMapper.toDto(productComponentVersion);
    }

    /**
     * Get all the productComponentVersions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductComponentVersionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductComponentVersions");
        return productComponentVersionRepository.findAll(pageable)
            .map(productComponentVersionMapper::toDto);
    }


    /**
     * Get one productComponentVersion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductComponentVersionDTO> findOne(Long id) {
        log.debug("Request to get ProductComponentVersion : {}", id);
        return productComponentVersionRepository.findById(id)
            .map(productComponentVersionMapper::toDto);
    }

    /**
     * Delete the productComponentVersion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductComponentVersion : {}", id);
        productComponentVersionRepository.deleteById(id);
    }
}
