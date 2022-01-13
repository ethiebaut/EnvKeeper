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
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductVersionMessage extends UpdateMessage {

    public static final String MESSAGE_TYPE = "ProductVersion";

    private String productShortName;
    private String productVersion;
    private String releaseNotes;
    private String releaseNotesBase64;
    private String buildUrl;
    private Instant buildStartTime;
    private Set<ProductComponentVersionMessage> components = new HashSet<>();

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

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(final String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getReleaseNotesBase64() {
        return releaseNotesBase64;
    }

    public void setReleaseNotesBase64(final String releaseNotesBase64) {
        this.releaseNotesBase64 = releaseNotesBase64;
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

    public Set<ProductComponentVersionMessage> getComponents() {
        return components;
    }

    public void setComponents(final Set<ProductComponentVersionMessage> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "ProductVersionMessage{" +
               "messageType='" + messageType + '\'' +
               ", productShortName='" + productShortName + '\'' +
               ", productVersion='" + productVersion + '\'' +
               ", releaseNotes='" + releaseNotes + '\'' +
               ", releaseNotesBase64='" + releaseNotesBase64 + '\'' +
               ", buildUrl='" + buildUrl + '\'' +
               ", buildStartTime=" + buildStartTime +
               ", components=" + components +
               '}';
    }
}
