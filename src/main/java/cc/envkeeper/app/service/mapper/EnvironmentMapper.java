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
import cc.envkeeper.app.service.dto.EnvironmentDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Environment} and its DTO {@link EnvironmentDTO}.
 */
@Mapper(componentModel = "spring", uses = {EnvironmentGroupMapper.class})
public interface EnvironmentMapper extends EntityMapper<EnvironmentDTO, Environment> {

    @Mapping(source = "environmentGroup.id", target = "environmentGroupId")
    @Mapping(source = "environmentGroup.shortName", target = "environmentGroupShortName")
    @Mapping(source = "environmentGroup.sortOrder", target = "environmentGroupSortOrder")
    EnvironmentDTO toDto(Environment environment);

    @Mapping(source = "environmentGroupId", target = "environmentGroup")
    Environment toEntity(EnvironmentDTO environmentDTO);

    default Environment fromId(Long id) {
        if (id == null) {
            return null;
        }
        Environment environment = new Environment();
        environment.setId(id);
        return environment;
    }
}
