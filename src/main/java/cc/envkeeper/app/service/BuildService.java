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

import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.repository.BuildRepository;
import cc.envkeeper.app.service.dto.BuildDTO;
import cc.envkeeper.app.service.mapper.BuildMapper;
import cc.envkeeper.app.web.rest.BuildResource;
import cc.envkeeper.app.service.exception.BadRequestAlertException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Build}.
 */
@Service
@Transactional
public class BuildService {

    private final Logger log = LoggerFactory.getLogger(BuildService.class);

    private final BuildRepository buildRepository;

    private final BuildMapper buildMapper;

    public BuildService(BuildRepository buildRepository, BuildMapper buildMapper) {
        this.buildRepository = buildRepository;
        this.buildMapper = buildMapper;
    }

    /**
     * Save a build.
     *
     * @param buildDTO the entity to save.
     * @return the persisted entity.
     */
    public BuildDTO save(BuildDTO buildDTO) {
        log.debug("Request to save Build : {}", buildDTO);
        if (buildDTO.getParentBuildId() != null && buildDTO.getId() != null && buildDTO.getParentBuildId().equals(buildDTO.getId())) {
            throw new BadRequestAlertException("Can't set build as its own parent", BuildResource.ENTITY_NAME, "selfParent");
        }
        Build build = buildMapper.toEntity(buildDTO);
        build = buildRepository.save(build);
        return buildMapper.toDto(build);
    }

    /**
     * Get all the builds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Builds");
        return buildRepository.findAll(pageable)
            .map(buildMapper::toDto);
    }


    /**
     * Get one build by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BuildDTO> findOne(Long id) {
        log.debug("Request to get Build : {}", id);
        return buildRepository.findById(id)
            .map(buildMapper::toDto);
    }

    /**
     * Delete the build by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Build : {}", id);
        buildRepository.deleteById(id);
    }
}
