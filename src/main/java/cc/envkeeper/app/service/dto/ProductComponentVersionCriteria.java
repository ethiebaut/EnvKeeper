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
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import cc.envkeeper.app.domain.enumeration.BuildStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link cc.envkeeper.app.domain.ProductComponentVersion} entity. This class is used
 * in {@link cc.envkeeper.app.web.rest.ProductComponentVersionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /product-component-versions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductComponentVersionCriteria implements Serializable, Criteria {
    /**
     * Class for filtering BuildStatus
     */
    public static class BuildStatusFilter extends Filter<BuildStatus> {

        public BuildStatusFilter() {
        }

        public BuildStatusFilter(BuildStatusFilter filter) {
            super(filter);
        }

        @Override
        public BuildStatusFilter copy() {
            return new BuildStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter version;

    private BuildStatusFilter buildStatus;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private StringFilter buildUrl;

    private StringFilter releaseNotes;

    private LongFilter componentId;

    private LongFilter productVersionId;

    public ProductComponentVersionCriteria() {
    }

    public ProductComponentVersionCriteria(ProductComponentVersionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.buildStatus = other.buildStatus == null ? null : other.buildStatus.copy();
        this.startTime = other.startTime == null ? null : other.startTime.copy();
        this.endTime = other.endTime == null ? null : other.endTime.copy();
        this.buildUrl = other.buildUrl == null ? null : other.buildUrl.copy();
        this.releaseNotes = other.releaseNotes == null ? null : other.releaseNotes.copy();
        this.componentId = other.componentId == null ? null : other.componentId.copy();
        this.productVersionId = other.productVersionId == null ? null : other.productVersionId.copy();
    }

    @Override
    public ProductComponentVersionCriteria copy() {
        return new ProductComponentVersionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getVersion() {
        return version;
    }

    public void setVersion(StringFilter version) {
        this.version = version;
    }

    public BuildStatusFilter getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(BuildStatusFilter buildStatus) {
        this.buildStatus = buildStatus;
    }

    public InstantFilter getStartTime() {
        return startTime;
    }

    public void setStartTime(InstantFilter startTime) {
        this.startTime = startTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
    }

    public StringFilter getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(StringFilter buildUrl) {
        this.buildUrl = buildUrl;
    }

    public StringFilter getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(StringFilter releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public LongFilter getComponentId() {
        return componentId;
    }

    public void setComponentId(LongFilter componentId) {
        this.componentId = componentId;
    }

    public LongFilter getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(LongFilter productVersionId) {
        this.productVersionId = productVersionId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductComponentVersionCriteria that = (ProductComponentVersionCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(version, that.version) &&
            Objects.equals(buildStatus, that.buildStatus) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(buildUrl, that.buildUrl) &&
            Objects.equals(releaseNotes, that.releaseNotes) &&
            Objects.equals(componentId, that.componentId) &&
            Objects.equals(productVersionId, that.productVersionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        version,
        buildStatus,
        startTime,
        endTime,
        buildUrl,
        releaseNotes,
        componentId,
        productVersionId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductComponentVersionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (version != null ? "version=" + version + ", " : "") +
                (buildStatus != null ? "buildStatus=" + buildStatus + ", " : "") +
                (startTime != null ? "startTime=" + startTime + ", " : "") +
                (endTime != null ? "endTime=" + endTime + ", " : "") +
                (buildUrl != null ? "buildUrl=" + buildUrl + ", " : "") +
                (releaseNotes != null ? "releaseNotes=" + releaseNotes + ", " : "") +
                (componentId != null ? "componentId=" + componentId + ", " : "") +
                (productVersionId != null ? "productVersionId=" + productVersionId + ", " : "") +
            "}";
    }

}
