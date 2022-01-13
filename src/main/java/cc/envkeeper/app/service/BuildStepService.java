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

import cc.envkeeper.app.domain.BuildStep;
import cc.envkeeper.app.repository.BuildStepRepository;
import cc.envkeeper.app.service.dto.BuildStepDTO;
import cc.envkeeper.app.service.mapper.BuildStepMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link BuildStep}.
 */
@Service
@Transactional
public class BuildStepService {

    private final Logger log = LoggerFactory.getLogger(BuildStepService.class);

    private final BuildStepRepository buildStepRepository;

    private final BuildStepMapper buildStepMapper;

    public BuildStepService(BuildStepRepository buildStepRepository, BuildStepMapper buildStepMapper) {
        this.buildStepRepository = buildStepRepository;
        this.buildStepMapper = buildStepMapper;
    }

    /**
     * Save a buildStep.
     *
     * @param buildStepDTO the entity to save.
     * @return the persisted entity.
     */
    public BuildStepDTO save(BuildStepDTO buildStepDTO) {
        log.debug("Request to save BuildStep : {}", buildStepDTO);
        BuildStep buildStep = buildStepMapper.toEntity(buildStepDTO);
        buildStep = buildStepRepository.save(buildStep);
        return buildStepMapper.toDto(buildStep);
    }

    /**
     * Get all the buildSteps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildStepDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BuildSteps");
        return buildStepRepository.findAll(pageable)
            .map(buildStepMapper::toDto);
    }


    /**
     * Get one buildStep by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BuildStepDTO> findOne(Long id) {
        log.debug("Request to get BuildStep : {}", id);
        return buildStepRepository.findById(id)
            .map(buildStepMapper::toDto);
    }

    /**
     * Delete the buildStep by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BuildStep : {}", id);
        buildStepRepository.deleteById(id);
    }
}
