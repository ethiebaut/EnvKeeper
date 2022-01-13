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
import cc.envkeeper.app.service.dto.ProductComponentVersionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductComponentVersion} and its DTO {@link ProductComponentVersionDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProductComponentMapper.class})
public interface ProductComponentVersionMapper extends EntityMapper<ProductComponentVersionDTO, ProductComponentVersion> {

    @Mapping(source = "component.id", target = "componentId")
    @Mapping(source = "component.shortName", target = "componentShortName")
    ProductComponentVersionDTO toDto(ProductComponentVersion productComponentVersion);

    @Mapping(source = "componentId", target = "component")
    @Mapping(target = "productVersions", ignore = true)
    @Mapping(target = "removeProductVersion", ignore = true)
    ProductComponentVersion toEntity(ProductComponentVersionDTO productComponentVersionDTO);

    default ProductComponentVersion fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProductComponentVersion productComponentVersion = new ProductComponentVersion();
        productComponentVersion.setId(id);
        return productComponentVersion;
    }
}
