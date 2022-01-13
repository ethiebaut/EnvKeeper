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

package cc.envkeeper.app.remoteupdate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

import cc.envkeeper.app.domain.enumeration.BuildStatus;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductComponentVersionMessage extends UpdateMessage {

    public static final String MESSAGE_TYPE = "ComponentVersion";

    private String componentShortName;
    private String componentVersion;
    private BuildStatus buildStatus;
    private Instant buildStartTime;
    private Instant buildEndTime;
    private String buildUrl;
    private String componentReleaseNotes;
    private String componentReleaseNotesBase64;

    public String getComponentShortName() {
        return componentShortName;
    }

    public void setComponentShortName(final String componentShortName) {
        this.componentShortName = componentShortName;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(final String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public BuildStatus getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(final BuildStatus buildStatus) {
        this.buildStatus = buildStatus;
    }

    public Instant getBuildStartTime() {
        return buildStartTime;
    }

    public void setBuildStartTime(final Instant buildStartTime) {
        this.buildStartTime = buildStartTime;
    }

    public Instant getBuildEndTime() {
        return buildEndTime;
    }

    public void setBuildEndTime(final Instant buildEndTime) {
        this.buildEndTime = buildEndTime;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(final String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public String getComponentReleaseNotes() {
        return componentReleaseNotes;
    }

    public void setComponentReleaseNotes(final String componentReleaseNotes) {
        this.componentReleaseNotes = componentReleaseNotes;
    }

    public String getComponentReleaseNotesBase64() {
        return componentReleaseNotesBase64;
    }

    public void setComponentReleaseNotesBase64(final String componentReleaseNotesBase64) {
        this.componentReleaseNotesBase64 = componentReleaseNotesBase64;
    }

    @Override
    public String toString() {
        return "ProductComponentVersionMessage{" +
               "componentShortName=" + componentShortName +
               ", componentVersion='" + componentVersion + '\'' +
               ", buildStatus=" + buildStatus +
               ", buildStartTime=" + buildStartTime +
               ", buildEndTime=" + buildEndTime +
               ", buildUrl='" + buildUrl + '\'' +
               ", componentReleaseNotes='" + componentReleaseNotes + '\'' +
               ", componentReleaseNotesBase64='" + componentReleaseNotesBase64 + '\'' +
               '}';
    }
}
