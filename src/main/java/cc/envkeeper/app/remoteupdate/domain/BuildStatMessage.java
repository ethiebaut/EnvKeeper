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
public class BuildStatMessage extends UpdateMessage {

    public static final String MESSAGE_TYPE = "BuildStat";

    private String buildUrl;
    private Instant buildStartTime;
    private String key;
    private long value;

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

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BuildStatMessage{" +
               "messageType='" + messageType + '\'' +
               ", buildUrl='" + buildUrl + '\'' +
               ", buildStartTime=" + buildStartTime +
               ", key='" + key + '\'' +
               ", value=" + value +
               '}';
    }
}
