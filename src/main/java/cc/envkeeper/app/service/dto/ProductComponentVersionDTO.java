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
import cc.envkeeper.app.domain.enumeration.BuildStatus;

/**
 * A DTO for the {@link cc.envkeeper.app.domain.ProductComponentVersion} entity.
 */
public class ProductComponentVersionDTO implements Serializable {

    private Long id;

    @NotNull
    private String version;

    @NotNull
    private BuildStatus buildStatus;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    private String buildUrl;

    private String releaseNotes;

    private String releaseNotesBase64;

    private Long componentId;

    private String componentShortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BuildStatus getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(BuildStatus buildStatus) {
        this.buildStatus = buildStatus;
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

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getReleaseNotesBase64() {
        return releaseNotesBase64;
    }

    public void setReleaseNotesBase64(final String releaseNotesBase64) {
        this.releaseNotesBase64 = releaseNotesBase64;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long productComponentId) {
        this.componentId = productComponentId;
    }

    public String getComponentShortName() {
        return componentShortName;
    }

    public void setComponentShortName(String productComponentShortName) {
        this.componentShortName = productComponentShortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductComponentVersionDTO)) {
            return false;
        }

        return id != null && id.equals(((ProductComponentVersionDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductComponentVersionDTO{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", buildStatus='" + getBuildStatus() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", buildUrl='" + getBuildUrl() + "'" +
            ", releaseNotes='" + getReleaseNotes() + "'" +
            ", componentId=" + getComponentId() +
            ", componentShortName='" + getComponentShortName() + "'" +
            "}";
    }
}
