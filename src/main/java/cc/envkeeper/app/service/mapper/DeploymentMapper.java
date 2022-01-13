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
import cc.envkeeper.app.service.dto.DeploymentDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Deployment} and its DTO {@link DeploymentDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProductVersionMapper.class, EnvironmentMapper.class, BuildMapper.class})
public interface DeploymentMapper extends EntityMapper<DeploymentDTO, Deployment> {

    @Mapping(source = "productVersion.id", target = "productVersionId")
    @Mapping(source = "productVersion.version", target = "productVersionVersion")
    @Mapping(source = "environment.id", target = "environmentId")
    @Mapping(source = "environment.shortName", target = "environmentShortName")
    @Mapping(source = "build.id", target = "buildId")
    @Mapping(source = "productVersion.product", target = "product")
    @Mapping(source = "environment", target = "environment")
    DeploymentDTO toDto(Deployment deployment);

    @Mapping(source = "productVersionId", target = "productVersion")
    @Mapping(source = "environmentId", target = "environment")
    @Mapping(source = "buildId", target = "build")
    Deployment toEntity(DeploymentDTO deploymentDTO);

    default Deployment fromId(Long id) {
        if (id == null) {
            return null;
        }
        Deployment deployment = new Deployment();
        deployment.setId(id);
        return deployment;
    }
}
