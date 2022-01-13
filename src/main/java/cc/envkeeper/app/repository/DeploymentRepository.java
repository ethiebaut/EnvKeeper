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

import cc.envkeeper.app.domain.Deployment;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data  repository for the Deployment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long>, JpaSpecificationExecutor<Deployment> {

    @Query(value = "SELECT\n" +
                   "    CASE WHEN choice = 'D' THEN D_id ELSE V_id END id ,\n" +
                   "    CASE WHEN choice = 'D' THEN D_start_time ELSE V_start_time END start_time,\n" +
                   "    CASE WHEN choice = 'D' THEN D_end_time ELSE V_end_time END end_time,\n" +
                   "    CASE WHEN choice = 'D' THEN D_jhi_user ELSE V_jhi_user END jhi_user,\n" +
                   "    CASE WHEN choice = 'D' THEN D_status ELSE V_status END status,\n" +
                   "    CASE WHEN choice = 'D' THEN D_namespace ELSE V_namespace END \"namespace\",\n" +
                   "    CASE WHEN choice = 'D' THEN D_url ELSE V_url END url,\n" +
                   "    CASE WHEN choice = 'D' THEN D_test_url ELSE V_test_url END test_url,\n" +
                   "    CASE WHEN choice = 'D' THEN D_product_version_id ELSE V_product_version_id END product_version_id,\n" +
                   "    CASE WHEN choice = 'D' THEN D_environment_id ELSE V_environment_id END environment_id,\n" +
                   "    CASE WHEN choice = 'D' THEN D_build_id ELSE V_build_id END build_id\n" +
                   "FROM\n" +
                   "(\n" +
                   "SELECT\n" +
                   " CASE WHEN V.id IS NULL\n" +
                   "        OR (D.id IS NOT NULL AND D.product_version_id = V.product_version_id)\n" +
                   "        OR (D.id IS NOT NULL AND D.start_time >= V.start_time)\n" +
                   "      THEN 'D'\n" +
                   "      ELSE 'V'\n" +
                   " END choice,\n" +
                   " D.id D_id,\n" +
                   " D.start_time D_start_time,\n" +
                   " D.end_time D_end_time,\n" +
                   " D.jhi_user D_jhi_user,\n" +
                   " D.status D_status,\n" +
                   " D.namespace D_namespace,\n" +
                   " D.url D_url,\n" +
                   " D.test_url D_test_url,\n" +
                   " D.product_version_id D_product_version_id,\n" +
                   " D.environment_id D_environment_id,\n" +
                   " D.build_id D_build_id,\n" +
                   " V.id V_id,\n" +
                   " V.start_time V_start_time,\n" +
                   " V.end_time V_end_time,\n" +
                   " V.jhi_user V_jhi_user,\n" +
                   " V.status V_status,\n" +
                   " V.namespace V_namespace,\n" +
                   " V.url V_url,\n" +
                   " V.test_url V_test_url,\n" +
                   " V.product_version_id V_product_version_id,\n" +
                   " V.environment_id V_environment_id,\n" +
                   " V.build_id V_build_id\n" +
                   "FROM\n" +
                   "(\n" +
                   "SELECT D.*,PV.product_id\n" +
                   "FROM (\n" +
                   "SELECT D.*\n" +
                   "FROM (\n" +
                   "SELECT deployment.id deployment_id, rank() over (partition by environment_id, product_id order by start_time desc, deployment.id desc) rank\n" +
                   "FROM deployment\n" +
                   "JOIN product_version on product_version.id = deployment.product_version_id\n" +
                   "WHERE status in ('SUCCEEDED', 'FAILED_KEPT', 'DELETED')\n" +
                   ") T\n" +
                   "JOIN deployment D on T.deployment_id = D.id\n" +
                   "WHERE rank = 1\n" +
                   ") D JOIN product_version PV on D.product_version_id = PV.id\n" +
                   ") D\n" +
                   "FULL OUTER JOIN\n" +
                   "(\n" +
                   "SELECT D.*,PV.product_id from (\n" +
                   "SELECT D.*\n" +
                   "FROM (\n" +
                   "SELECT deployment.id deployment_id, rank() over (partition by environment_id, product_id order by start_time desc, deployment.id desc) rank\n" +
                   "FROM deployment\n" +
                   "JOIN product_version on product_version.id = deployment.product_version_id\n" +
                   "WHERE status in ('VERIFIED')\n" +
                   ") T\n" +
                   "JOIN deployment D on T.deployment_id = D.id\n" +
                   "WHERE rank = 1\n" +
                   ") D JOIN product_version PV on D.product_version_id = PV.id\n" +
                   ") V ON D.environment_id = V.environment_id AND D.product_id = V.product_id\n" +
                   ") T\n" +
                   "\n", nativeQuery = true)
    List<Deployment> findLatest();

    @Query(value = "SELECT\n" +
                   "    CASE WHEN choice = 'D' THEN D_id ELSE V_id END id ,\n" +
                   "    CASE WHEN choice = 'D' THEN D_start_time ELSE V_start_time END start_time,\n" +
                   "    CASE WHEN choice = 'D' THEN D_end_time ELSE V_end_time END end_time,\n" +
                   "    CASE WHEN choice = 'D' THEN D_jhi_user ELSE V_jhi_user END jhi_user,\n" +
                   "    CASE WHEN choice = 'D' THEN D_status ELSE V_status END status,\n" +
                   "    CASE WHEN choice = 'D' THEN D_namespace ELSE V_namespace END \"namespace\",\n" +
                   "    CASE WHEN choice = 'D' THEN D_url ELSE V_url END url,\n" +
                   "    CASE WHEN choice = 'D' THEN D_test_url ELSE V_test_url END test_url,\n" +
                   "    CASE WHEN choice = 'D' THEN D_product_version_id ELSE V_product_version_id END product_version_id,\n" +
                   "    CASE WHEN choice = 'D' THEN D_environment_id ELSE V_environment_id END environment_id,\n" +
                   "    CASE WHEN choice = 'D' THEN D_build_id ELSE V_build_id END build_id\n" +
                   "FROM\n" +
                   "(\n" +
                   "SELECT\n" +
                   " CASE WHEN V.id IS NULL\n" +
                   "        OR (D.id IS NOT NULL AND D.product_version_id = V.product_version_id)\n" +
                   "        OR (D.id IS NOT NULL AND D.start_time >= V.start_time)\n" +
                   "      THEN 'D'\n" +
                   "      ELSE 'V'\n" +
                   " END choice,\n" +
                   " D.id D_id,\n" +
                   " D.start_time D_start_time,\n" +
                   " D.end_time D_end_time,\n" +
                   " D.jhi_user D_jhi_user,\n" +
                   " D.status D_status,\n" +
                   " D.namespace D_namespace,\n" +
                   " D.url D_url,\n" +
                   " D.test_url D_test_url,\n" +
                   " D.product_version_id D_product_version_id,\n" +
                   " D.environment_id D_environment_id,\n" +
                   " D.build_id D_build_id,\n" +
                   " V.id V_id,\n" +
                   " V.start_time V_start_time,\n" +
                   " V.end_time V_end_time,\n" +
                   " V.jhi_user V_jhi_user,\n" +
                   " V.status V_status,\n" +
                   " V.namespace V_namespace,\n" +
                   " V.url V_url,\n" +
                   " V.test_url V_test_url,\n" +
                   " V.product_version_id V_product_version_id,\n" +
                   " V.environment_id V_environment_id,\n" +
                   " V.build_id V_build_id\n" +
                   "FROM\n" +
                   "(\n" +
                   "SELECT D.*,PV.product_id\n" +
                   "FROM (\n" +
                   "SELECT D.*\n" +
                   "FROM (\n" +
                   "SELECT deployment.id deployment_id, rank() over (partition by environment_id, product_id order by start_time desc, deployment.id desc) rank\n" +
                   "FROM deployment\n" +
                   "JOIN product_version on product_version.id = deployment.product_version_id\n" +
                   "WHERE status in ('SUCCEEDED', 'FAILED_KEPT', 'DELETED')\n" +
                   "  AND start_time < :asOf\n" +
                   ") T\n" +
                   "JOIN deployment D on T.deployment_id = D.id\n" +
                   "WHERE rank = 1\n" +
                   ") D JOIN product_version PV on D.product_version_id = PV.id\n" +
                   ") D\n" +
                   "FULL OUTER JOIN\n" +
                   "(\n" +
                   "SELECT D.*,PV.product_id from (\n" +
                   "SELECT D.*\n" +
                   "FROM (\n" +
                   "SELECT deployment.id deployment_id, rank() over (partition by environment_id, product_id order by start_time desc, deployment.id desc) rank\n" +
                   "FROM deployment\n" +
                   "JOIN product_version on product_version.id = deployment.product_version_id\n" +
                   "WHERE status in ('VERIFIED')\n" +
                   "  AND start_time < :asOf\n" +
                   ") T\n" +
                   "JOIN deployment D on T.deployment_id = D.id\n" +
                   "WHERE rank = 1\n" +
                   ") D JOIN product_version PV on D.product_version_id = PV.id\n" +
                   ") V ON D.environment_id = V.environment_id AND D.product_id = V.product_id\n" +
                   ") T\n" +
                   "\n", nativeQuery = true)
    List<Deployment> findLatestAsOf(@Param("asOf") Instant asOf);
}
