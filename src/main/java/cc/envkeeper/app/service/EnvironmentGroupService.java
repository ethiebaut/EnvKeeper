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

import cc.envkeeper.app.domain.EnvironmentGroup;
import cc.envkeeper.app.repository.EnvironmentGroupRepository;
import cc.envkeeper.app.service.dto.EnvironmentGroupDTO;
import cc.envkeeper.app.service.mapper.EnvironmentGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link EnvironmentGroup}.
 */
@Service
@Transactional
public class EnvironmentGroupService {

    private final Logger log = LoggerFactory.getLogger(EnvironmentGroupService.class);

    private final EnvironmentGroupRepository environmentGroupRepository;

    private final EnvironmentGroupMapper environmentGroupMapper;

    public EnvironmentGroupService(EnvironmentGroupRepository environmentGroupRepository, EnvironmentGroupMapper environmentGroupMapper) {
        this.environmentGroupRepository = environmentGroupRepository;
        this.environmentGroupMapper = environmentGroupMapper;
    }

    /**
     * Save a environmentGroup.
     *
     * @param environmentGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public EnvironmentGroupDTO save(EnvironmentGroupDTO environmentGroupDTO) {
        log.debug("Request to save EnvironmentGroup : {}", environmentGroupDTO);
        EnvironmentGroup environmentGroup = environmentGroupMapper.toEntity(environmentGroupDTO);
        environmentGroup = environmentGroupRepository.save(environmentGroup);
        return environmentGroupMapper.toDto(environmentGroup);
    }

    /**
     * Get all the environmentGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EnvironmentGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EnvironmentGroups");
        return environmentGroupRepository.findAll(pageable)
            .map(environmentGroupMapper::toDto);
    }


    /**
     * Get one environmentGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EnvironmentGroupDTO> findOne(Long id) {
        log.debug("Request to get EnvironmentGroup : {}", id);
        return environmentGroupRepository.findById(id)
            .map(environmentGroupMapper::toDto);
    }

    /**
     * Delete the environmentGroup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete EnvironmentGroup : {}", id);
        environmentGroupRepository.deleteById(id);
    }
}
