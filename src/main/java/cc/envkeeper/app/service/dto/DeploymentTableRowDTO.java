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

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotNull;

import cc.envkeeper.app.domain.enumeration.DeploymentStatus;

/**
 * A DTO for the {@link cc.envkeeper.app.domain.Deployment} entity.
 */
public class DeploymentTableRowDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    private String user;

    @NotNull
    private DeploymentStatus status;

    private Long environmentId;

    private String environmentShortName;

    private EnvironmentDTO environment;

    private Long buildId;

    @NotNull
    private List<DeploymentTableCellDTO> deploymentTableCells;

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

    public List<DeploymentTableCellDTO> getDeploymentTableCells() {
        return deploymentTableCells;
    }

    public void setDeploymentTableCells(final List<DeploymentTableCellDTO> deploymentTableCells) {
        this.deploymentTableCells = deploymentTableCells;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeploymentTableRowDTO)) {
            return false;
        }

        return id != null && id.equals(((DeploymentTableRowDTO) o).id);
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
            ", environmentId=" + getEnvironmentId() +
            ", environmentShortName='" + getEnvironmentShortName() + "'" +
            ", buildId=" + getBuildId() +
            "}";
    }
}
