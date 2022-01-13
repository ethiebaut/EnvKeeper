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

import cc.envkeeper.app.domain.BuildStatistic;
import cc.envkeeper.app.repository.BuildStatisticRepository;
import cc.envkeeper.app.service.dto.BuildStatisticDTO;
import cc.envkeeper.app.service.mapper.BuildStatisticMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link BuildStatistic}.
 */
@Service
@Transactional
public class BuildStatisticService {

    private final Logger log = LoggerFactory.getLogger(BuildStatisticService.class);

    private final BuildStatisticRepository buildStatisticRepository;

    private final BuildStatisticMapper buildStatisticMapper;

    public BuildStatisticService(BuildStatisticRepository buildStatisticRepository, BuildStatisticMapper buildStatisticMapper) {
        this.buildStatisticRepository = buildStatisticRepository;
        this.buildStatisticMapper = buildStatisticMapper;
    }

    /**
     * Save a buildStatistic.
     *
     * @param buildStatisticDTO the entity to save.
     * @return the persisted entity.
     */
    public BuildStatisticDTO save(BuildStatisticDTO buildStatisticDTO) {
        log.debug("Request to save BuildStatistic : {}", buildStatisticDTO);
        BuildStatistic buildStatistic = buildStatisticMapper.toEntity(buildStatisticDTO);
        buildStatistic = buildStatisticRepository.save(buildStatistic);
        return buildStatisticMapper.toDto(buildStatistic);
    }

    /**
     * Get all the buildStatistics.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildStatisticDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BuildStatistics");
        return buildStatisticRepository.findAll(pageable)
            .map(buildStatisticMapper::toDto);
    }


    /**
     * Get one buildStatistic by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BuildStatisticDTO> findOne(Long id) {
        log.debug("Request to get BuildStatistic : {}", id);
        return buildStatisticRepository.findById(id)
            .map(buildStatisticMapper::toDto);
    }

    /**
     * Delete the buildStatistic by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BuildStatistic : {}", id);
        buildStatisticRepository.deleteById(id);
    }
}
