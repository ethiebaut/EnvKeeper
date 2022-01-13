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

import cc.envkeeper.app.domain.enumeration.DeploymentStatus;

import static cc.envkeeper.app.remoteupdate.utils.TimeFormat.isoDateFormat;

@JsonIgnoreProperties(ignoreUnknown = false)
public class DeploymentMessage extends UpdateMessage {

    public static final String MESSAGE_TYPE = "Deployment";

    private String environmentShortName;
    private String namespace;
    private String productShortName;
    private String productVersion;
    private Instant startTime;
    private Instant endTime;
    private DeploymentStatus status;
    private String testUrl;
    private String url;
    private String user;
    private String buildUrl;
    private Instant buildStartTime;

    public String getEnvironmentShortName() {
        return environmentShortName;
    }

    public void setEnvironmentShortName(final String environmentShortName) {
        this.environmentShortName = environmentShortName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(final String productShortName) {
        this.productShortName = productShortName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(final String productVersion) {
        this.productVersion = productVersion;
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

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(final DeploymentStatus status) {
        this.status = status;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(final String testUrl) {
        this.testUrl = testUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

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

    @Override
    public String toString() {
        return "Deployment{" +
               "messageType='" + messageType + '\'' +
               ", environmentShortName='" + environmentShortName + '\'' +
               ", namespace='" + namespace + '\'' +
               ", productShortName='" + productShortName + '\'' +
               ", productVersionVersion='" + productVersion + '\'' +
               ", startTime=" + isoDateFormat.toString(startTime) +
               ", endTime=" + isoDateFormat.toString(endTime) +
               ", status='" + status + '\'' +
               ", testUrl='" + testUrl + '\'' +
               ", url='" + url + '\'' +
               ", user='" + user + '\'' +
               ", buildUrl='" + buildUrl + '\'' +
               ", buildStartTime=" + isoDateFormat.toString(buildStartTime) +
               '}';
    }
}
