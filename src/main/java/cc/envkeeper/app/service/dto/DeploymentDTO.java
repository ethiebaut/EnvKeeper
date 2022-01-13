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

package cc.envkeeper.app.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import cc.envkeeper.app.domain.enumeration.DeploymentStatus;

/**
 * A DTO for the {@link cc.envkeeper.app.domain.Deployment} entity.
 */
public class DeploymentDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    private String user;

    @NotNull
    private DeploymentStatus status;

    private String namespace;

    private String url;

    private String testUrl;


    private Long productVersionId;

    private String productVersionVersion;

    private Long environmentId;

    private String environmentShortName;

    private ProductDTO product;

    private EnvironmentDTO environment;

    private Long buildId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public Long getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(Long productVersionId) {
        this.productVersionId = productVersionId;
    }

    public String getProductVersionVersion() {
        return productVersionVersion;
    }

    public void setProductVersionVersion(String productVersionVersion) {
        this.productVersionVersion = productVersionVersion;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public String getEnvironmentShortName() {
        return environmentShortName;
    }

    public void setEnvironmentShortName(String environmentShortName) {
        this.environmentShortName = environmentShortName;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(final ProductDTO product) {
        this.product = product;
    }

    public EnvironmentDTO getEnvironment() {
        return environment;
    }

    public void setEnvironment(final EnvironmentDTO environment) {
        this.environment = environment;
    }

    public Long getBuildId() {
        return buildId;
    }

    public void setBuildId(Long buildId) {
        this.buildId = buildId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeploymentDTO)) {
            return false;
        }

        return id != null && id.equals(((DeploymentDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeploymentDTO{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", user='" + getUser() + "'" +
            ", status='" + getStatus() + "'" +
            ", namespace='" + getNamespace() + "'" +
            ", url='" + getUrl() + "'" +
            ", testUrl='" + getTestUrl() + "'" +
            ", productVersionId=" + getProductVersionId() +
            ", productVersionVersion='" + getProductVersionVersion() + "'" +
            ", environmentId=" + getEnvironmentId() +
            ", environmentShortName='" + getEnvironmentShortName() + "'" +
            ", buildId=" + getBuildId() +
            "}";
    }
}
