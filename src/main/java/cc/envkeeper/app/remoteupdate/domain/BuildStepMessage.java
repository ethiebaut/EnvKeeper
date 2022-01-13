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
public class BuildStepMessage extends UpdateMessage {

    public static final String MESSAGE_TYPE = "BuildStep";

    private String buildUrl;
    private Instant buildStartTime;
    private String step;
    private BuildStatus status;
    private Instant startTime;
    private Instant endTime;

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(final String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public Instant getBuildStartTime() {
        return buildStartTime;
    }

    public void setBuildStartTime(final Instant buildStartTime) {
        this.buildStartTime = buildStartTime;
    }

    public String getStep() {
        return step;
    }

    public void setStep(final String step) {
        this.step = step;
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

    @Override
    public String toString() {
        return "BuildStepMessage{" +
               "messageType='" + messageType + '\'' +
               ", buildUrl='" + buildUrl + '\'' +
               ", buildStartTime=" + buildStartTime +
               ", step='" + step + '\'' +
               ", status=" + status +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               '}';
    }
}
