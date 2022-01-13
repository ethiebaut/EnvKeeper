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

import static cc.envkeeper.app.remoteupdate.utils.TimeFormat.isoDateFormat;

@JsonIgnoreProperties(ignoreUnknown = false)
public class BuildMessage extends UpdateMessage {

    public static final String MESSAGE_TYPE = "Build";

    private String buildUrl;
    private String buildName;
    private BuildStatus status;
    private Instant startTime;
    private Instant endTime;
    private String parentBuildUrl;
    private Instant parentBuildStartTime;

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(final String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(final String buildName) {
        this.buildName = buildName;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public void setStatus(final BuildStatus status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(final Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(final Instant endTime) {
        this.endTime = endTime;
    }

    public String getParentBuildUrl() {
        return parentBuildUrl;
    }

    public void setParentBuildUrl(final String parentBuildUrl) {
        this.parentBuildUrl = parentBuildUrl;
    }

    public Instant getParentBuildStartTime() {
        return parentBuildStartTime;
    }

    public void setParentBuildStartTime(final Instant parentBuildStartTime) {
        this.parentBuildStartTime = parentBuildStartTime;
    }

    @Override
    public String toString() {
        return "BuildMessage{" +
               "messageType='" + messageType + '\'' +
               ", buildUrl='" + buildUrl + '\'' +
               ", buildName='" + buildName + '\'' +
               ", status=" + status +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", parentBuildUrl='" + parentBuildUrl + '\'' +
               ", parentBuildStartTime=" + parentBuildStartTime +
               '}';
    }
}
