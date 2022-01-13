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
import cc.envkeeper.app.service.dto.BuildStepDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link BuildStep} and its DTO {@link BuildStepDTO}.
 */
@Mapper(componentModel = "spring", uses = {BuildMapper.class})
public interface BuildStepMapper extends EntityMapper<BuildStepDTO, BuildStep> {

    @Mapping(source = "build.id", target = "buildId")
    BuildStepDTO toDto(BuildStep buildStep);

    @Mapping(source = "buildId", target = "build")
    BuildStep toEntity(BuildStepDTO buildStepDTO);

    default BuildStep fromId(Long id) {
        if (id == null) {
            return null;
        }
        BuildStep buildStep = new BuildStep();
        buildStep.setId(id);
        return buildStep;
    }
}
