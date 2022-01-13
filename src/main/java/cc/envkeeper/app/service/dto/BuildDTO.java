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
import java.util.Set;

import cc.envkeeper.app.domain.enumeration.BuildStatus;

/**
 * A DTO for the {@link cc.envkeeper.app.domain.Build} entity.
 */
public class BuildDTO implements Serializable {

    private Long id;

    @NotNull
    private String buildUrl;

    private String buildName;

    @NotNull
    private BuildStatus status;

    @NotNull
    private Instant startTime;

    private Instant endTime;


    private Long parentBuildId;

    private Set<DeploymentDTO> deployments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public void setStatus(BuildStatus status) {
        this.status = status;
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

    public Long getParentBuildId() {
        return parentBuildId;
    }

    public void setParentBuildId(Long buildId) {
        this.parentBuildId = buildId;
    }

    public Set<DeploymentDTO> getDeployments() {
        return deployments;
    }

    public void setDeployments(final Set<DeploymentDTO> deployments) {
        this.deployments = deployments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BuildDTO)) {
            return false;
        }

        return id != null && id.equals(((BuildDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BuildDTO{" +
            "id=" + getId() +
            ", buildUrl='" + getBuildUrl() + "'" +
            ", buildName='" + getBuildName() + "'" +
            ", status='" + getStatus() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", parentBuildId=" + getParentBuildId() +
            "}";
    }
}
