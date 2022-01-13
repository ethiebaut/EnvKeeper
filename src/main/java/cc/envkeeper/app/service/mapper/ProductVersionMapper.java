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
import cc.envkeeper.app.service.dto.ProductVersionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductVersion} and its DTO {@link ProductVersionDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProductMapper.class, BuildMapper.class, ProductComponentVersionMapper.class})
public interface ProductVersionMapper extends EntityMapper<ProductVersionDTO, ProductVersion> {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.shortName", target = "productShortName")
    @Mapping(source = "build.id", target = "buildId")
    ProductVersionDTO toDto(ProductVersion productVersion);

    @Mapping(source = "productId", target = "product")
    @Mapping(source = "buildId", target = "build")
    @Mapping(target = "removeComponent", ignore = true)
    ProductVersion toEntity(ProductVersionDTO productVersionDTO);

    default ProductVersion fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProductVersion productVersion = new ProductVersion();
        productVersion.setId(id);
        return productVersion;
    }
}
