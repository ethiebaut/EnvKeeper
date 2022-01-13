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
import cc.envkeeper.app.service.dto.EnvironmentGroupDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link EnvironmentGroup} and its DTO {@link EnvironmentGroupDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EnvironmentGroupMapper extends EntityMapper<EnvironmentGroupDTO, EnvironmentGroup> {



    default EnvironmentGroup fromId(Long id) {
        if (id == null) {
            return null;
        }
        EnvironmentGroup environmentGroup = new EnvironmentGroup();
        environmentGroup.setId(id);
        return environmentGroup;
    }
}
