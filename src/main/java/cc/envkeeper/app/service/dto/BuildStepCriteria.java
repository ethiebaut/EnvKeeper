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
 * Criteria class for the {@link cc.envkeeper.app.domain.BuildStep} entity. This class is used
 * in {@link cc.envkeeper.app.web.rest.BuildStepResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /build-steps?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BuildStepCriteria implements Serializable, Criteria {
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

    private StringFilter step;

    private BuildStatusFilter status;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private LongFilter buildId;

    public BuildStepCriteria() {
    }

    public BuildStepCriteria(BuildStepCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.step = other.step == null ? null : other.step.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.startTime = other.startTime == null ? null : other.startTime.copy();
        this.endTime = other.endTime == null ? null : other.endTime.copy();
        this.buildId = other.buildId == null ? null : other.buildId.copy();
    }

    @Override
    public BuildStepCriteria copy() {
        return new BuildStepCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getStep() {
        return step;
    }

    public void setStep(StringFilter step) {
        this.step = step;
    }

    public BuildStatusFilter getStatus() {
        return status;
    }

    public void setStatus(BuildStatusFilter status) {
        this.status = status;
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
        final BuildStepCriteria that = (BuildStepCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(step, that.step) &&
            Objects.equals(status, that.status) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(buildId, that.buildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        step,
        status,
        startTime,
        endTime,
        buildId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BuildStepCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (step != null ? "step=" + step + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (startTime != null ? "startTime=" + startTime + ", " : "") +
                (endTime != null ? "endTime=" + endTime + ", " : "") +
                (buildId != null ? "buildId=" + buildId + ", " : "") +
            "}";
    }

}
