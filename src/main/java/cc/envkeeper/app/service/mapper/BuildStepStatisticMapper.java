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

package cc.envkeeper.app.service.mapper;


import cc.envkeeper.app.domain.*;
import cc.envkeeper.app.service.dto.BuildStepStatisticDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link BuildStepStatistic} and its DTO {@link BuildStepStatisticDTO}.
 */
@Mapper(componentModel = "spring", uses = {BuildStepMapper.class})
public interface BuildStepStatisticMapper extends EntityMapper<BuildStepStatisticDTO, BuildStepStatistic> {

    @Mapping(source = "buildStep.id", target = "buildStepId")
    @Mapping(source = "buildStep.step", target = "buildStepStep")
    BuildStepStatisticDTO toDto(BuildStepStatistic buildStepStatistic);

    @Mapping(source = "buildStepId", target = "buildStep")
    BuildStepStatistic toEntity(BuildStepStatisticDTO buildStepStatisticDTO);

    default BuildStepStatistic fromId(Long id) {
        if (id == null) {
            return null;
        }
        BuildStepStatistic buildStepStatistic = new BuildStepStatistic();
        buildStepStatistic.setId(id);
        return buildStepStatistic;
    }
}
