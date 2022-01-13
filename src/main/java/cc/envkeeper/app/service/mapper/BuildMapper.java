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
import cc.envkeeper.app.service.dto.BuildDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Build} and its DTO {@link BuildDTO}.
 */
@Mapper(componentModel = "spring", uses = {DeploymentMapper.class})
public interface BuildMapper extends EntityMapper<BuildDTO, Build> {

    @Mapping(source = "parentBuild.id", target = "parentBuildId")
    @Mapping(source = "deployments", target = "deployments")
    BuildDTO toDto(Build build);

    @Mapping(target = "builds", ignore = true)
    @Mapping(target = "removeBuild", ignore = true)
    @Mapping(source = "parentBuildId", target = "parentBuild")
    @Mapping(target = "deployments", ignore = true)
    @Mapping(target = "removeDeployment", ignore = true)
    @Mapping(target = "productVersions", ignore = true)
    @Mapping(target = "removeProductVersion", ignore = true)
    Build toEntity(BuildDTO buildDTO);

    default Build fromId(Long id) {
        if (id == null) {
            return null;
        }
        Build build = new Build();
        build.setId(id);
        return build;
    }
}
