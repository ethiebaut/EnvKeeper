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


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cc.envkeeper.app.domain.Deployment;
import cc.envkeeper.app.service.dto.DeploymentDTO;
import cc.envkeeper.app.service.dto.DeploymentTableRowDTO;

/**
 * Mapper for the entity {@link Deployment} and its DTO {@link DeploymentDTO}.
 */
@Mapper(componentModel = "spring", uses = {EnvironmentMapper.class, BuildMapper.class})
public interface DeploymentTableMapper extends EntityMapper<DeploymentTableRowDTO, Deployment> {

    @Mapping(source = "environment.id", target = "environmentId")
    @Mapping(source = "environment.shortName", target = "environmentShortName")
    @Mapping(source = "build.id", target = "buildId")
    @Mapping(source = "environment", target = "environment")
    DeploymentTableRowDTO toDto(Deployment deployment);

}
