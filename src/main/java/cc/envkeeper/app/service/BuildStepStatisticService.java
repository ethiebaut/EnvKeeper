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

import cc.envkeeper.app.domain.BuildStepStatistic;
import cc.envkeeper.app.repository.BuildStepStatisticRepository;
import cc.envkeeper.app.service.dto.BuildStepStatisticDTO;
import cc.envkeeper.app.service.mapper.BuildStepStatisticMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link BuildStepStatistic}.
 */
@Service
@Transactional
public class BuildStepStatisticService {

    private final Logger log = LoggerFactory.getLogger(BuildStepStatisticService.class);

    private final BuildStepStatisticRepository buildStepStatisticRepository;

    private final BuildStepStatisticMapper buildStepStatisticMapper;

    public BuildStepStatisticService(BuildStepStatisticRepository buildStepStatisticRepository, BuildStepStatisticMapper buildStepStatisticMapper) {
        this.buildStepStatisticRepository = buildStepStatisticRepository;
        this.buildStepStatisticMapper = buildStepStatisticMapper;
    }

    /**
     * Save a buildStepStatistic.
     *
     * @param buildStepStatisticDTO the entity to save.
     * @return the persisted entity.
     */
    public BuildStepStatisticDTO save(BuildStepStatisticDTO buildStepStatisticDTO) {
        log.debug("Request to save BuildStepStatistic : {}", buildStepStatisticDTO);
        BuildStepStatistic buildStepStatistic = buildStepStatisticMapper.toEntity(buildStepStatisticDTO);
        buildStepStatistic = buildStepStatisticRepository.save(buildStepStatistic);
        return buildStepStatisticMapper.toDto(buildStepStatistic);
    }

    /**
     * Get all the buildStepStatistics.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildStepStatisticDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BuildStepStatistics");
        return buildStepStatisticRepository.findAll(pageable)
            .map(buildStepStatisticMapper::toDto);
    }


    /**
     * Get one buildStepStatistic by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BuildStepStatisticDTO> findOne(Long id) {
        log.debug("Request to get BuildStepStatistic : {}", id);
        return buildStepStatisticRepository.findById(id)
            .map(buildStepStatisticMapper::toDto);
    }

    /**
     * Delete the buildStepStatistic by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BuildStepStatistic : {}", id);
        buildStepStatisticRepository.deleteById(id);
    }
}
