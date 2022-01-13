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

import cc.envkeeper.app.domain.ProductComponent;
import cc.envkeeper.app.repository.ProductComponentRepository;
import cc.envkeeper.app.service.dto.ProductComponentDTO;
import cc.envkeeper.app.service.mapper.ProductComponentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link ProductComponent}.
 */
@Service
@Transactional
public class ProductComponentService {

    private final Logger log = LoggerFactory.getLogger(ProductComponentService.class);

    private final ProductComponentRepository productComponentRepository;

    private final ProductComponentMapper productComponentMapper;

    public ProductComponentService(ProductComponentRepository productComponentRepository, ProductComponentMapper productComponentMapper) {
        this.productComponentRepository = productComponentRepository;
        this.productComponentMapper = productComponentMapper;
    }

    /**
     * Save a productComponent.
     *
     * @param productComponentDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductComponentDTO save(ProductComponentDTO productComponentDTO) {
        log.debug("Request to save ProductComponent : {}", productComponentDTO);
        ProductComponent productComponent = productComponentMapper.toEntity(productComponentDTO);
        productComponent = productComponentRepository.save(productComponent);
        return productComponentMapper.toDto(productComponent);
    }

    /**
     * Get all the productComponents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductComponentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductComponents");
        return productComponentRepository.findAll(pageable)
            .map(productComponentMapper::toDto);
    }


    /**
     * Get one productComponent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductComponentDTO> findOne(Long id) {
        log.debug("Request to get ProductComponent : {}", id);
        return productComponentRepository.findById(id)
            .map(productComponentMapper::toDto);
    }

    /**
     * Delete the productComponent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductComponent : {}", id);
        productComponentRepository.deleteById(id);
    }
}
