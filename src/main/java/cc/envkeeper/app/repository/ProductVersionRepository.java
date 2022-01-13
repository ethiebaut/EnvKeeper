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

package cc.envkeeper.app.repository;

import cc.envkeeper.app.domain.ProductVersion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the ProductVersion entity.
 */
@Repository
public interface ProductVersionRepository extends JpaRepository<ProductVersion, Long>, JpaSpecificationExecutor<ProductVersion> {

    @Query(value = "select distinct productVersion from ProductVersion productVersion left join fetch productVersion.components",
        countQuery = "select count(distinct productVersion) from ProductVersion productVersion")
    Page<ProductVersion> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct productVersion from ProductVersion productVersion left join fetch productVersion.components")
    List<ProductVersion> findAllWithEagerRelationships();

    @Query("select productVersion from ProductVersion productVersion left join fetch productVersion.components where productVersion.id =:id")
    Optional<ProductVersion> findOneWithEagerRelationships(@Param("id") Long id);
}
