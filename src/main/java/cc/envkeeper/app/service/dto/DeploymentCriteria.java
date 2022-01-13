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
import cc.envkeeper.app.domain.enumeration.DeploymentStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link cc.envkeeper.app.domain.Deployment} entity. This class is used
 * in {@link cc.envkeeper.app.web.rest.DeploymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /deployments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class DeploymentCriteria implements Serializable, Criteria {
    /**
     * Class for filtering DeploymentStatus
     */
    public static class DeploymentStatusFilter extends Filter<DeploymentStatus> {

        public DeploymentStatusFilter() {
        }

        public DeploymentStatusFilter(DeploymentStatusFilter filter) {
            super(filter);
        }

        @Override
        public DeploymentStatusFilter copy() {
            return new DeploymentStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private StringFilter user;

    private DeploymentStatusFilter status;

    private StringFilter namespace;

    private StringFilter url;

    private StringFilter testUrl;

    private LongFilter productId;

    private LongFilter productVersionId;

    private LongFilter environmentId;

    private LongFilter buildId;

    public DeploymentCriteria() {
    }

    public DeploymentCriteria(DeploymentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.startTime = other.startTime == null ? null : other.startTime.copy();
        this.endTime = other.endTime == null ? null : other.endTime.copy();
        this.user = other.user == null ? null : other.user.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.namespace = other.namespace == null ? null : other.namespace.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.testUrl = other.testUrl == null ? null : other.testUrl.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.productVersionId = other.productVersionId == null ? null : other.productVersionId.copy();
        this.environmentId = other.environmentId == null ? null : other.environmentId.copy();
        this.buildId = other.buildId == null ? null : other.buildId.copy();
    }

    @Override
    public DeploymentCriteria copy() {
        return new DeploymentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
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

    public StringFilter getUser() {
        return user;
    }

    public void setUser(StringFilter user) {
        this.user = user;
    }

    public DeploymentStatusFilter getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatusFilter status) {
        this.status = status;
    }

    public StringFilter getNamespace() {
        return namespace;
    }

    public void setNamespace(StringFilter namespace) {
        this.namespace = namespace;
    }

    public StringFilter getUrl() {
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(StringFilter testUrl) {
        this.testUrl = testUrl;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(final LongFilter productId) {
        this.productId = productId;
    }

    public LongFilter getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(LongFilter productVersionId) {
        this.productVersionId = productVersionId;
    }

    public LongFilter getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(LongFilter environmentId) {
        this.environmentId = environmentId;
    }

    public LongFilter getBuildId() {
        return buildId;
    }

    public void setBuildId(LongFilter buildId) {
        this.buildId = buildId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeploymentCriteria that = (DeploymentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(user, that.user) &&
            Objects.equals(status, that.status) &&
            Objects.equals(namespace, that.namespace) &&
            Objects.equals(url, that.url) &&
            Objects.equals(testUrl, that.testUrl) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(productVersionId, that.productVersionId) &&
            Objects.equals(environmentId, that.environmentId) &&
            Objects.equals(buildId, that.buildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        startTime,
        endTime,
        user,
        status,
        namespace,
        url,
        testUrl,
        productId,
        productVersionId,
        environmentId,
        buildId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeploymentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (startTime != null ? "startTime=" + startTime + ", " : "") +
                (endTime != null ? "endTime=" + endTime + ", " : "") +
                (user != null ? "user=" + user + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (namespace != null ? "namespace=" + namespace + ", " : "") +
                (url != null ? "url=" + url + ", " : "") +
                (testUrl != null ? "testUrl=" + testUrl + ", " : "") +
                (productId != null ? "productId=" + productId + ", " : "") +
                (productVersionId != null ? "productVersionId=" + productVersionId + ", " : "") +
                (environmentId != null ? "environmentId=" + environmentId + ", " : "") +
                (buildId != null ? "buildId=" + buildId + ", " : "") +
            "}";
    }

}
