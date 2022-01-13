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
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link cc.envkeeper.app.domain.BuildStepStatistic} entity. This class is used
 * in {@link cc.envkeeper.app.web.rest.BuildStepStatisticResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /build-step-statistics?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BuildStepStatisticCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter key;

    private LongFilter value;

    private LongFilter buildStepId;

    public BuildStepStatisticCriteria() {
    }

    public BuildStepStatisticCriteria(BuildStepStatisticCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.key = other.key == null ? null : other.key.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.buildStepId = other.buildStepId == null ? null : other.buildStepId.copy();
    }

    @Override
    public BuildStepStatisticCriteria copy() {
        return new BuildStepStatisticCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getKey() {
        return key;
    }

    public void setKey(StringFilter key) {
        this.key = key;
    }

    public LongFilter getValue() {
        return value;
    }

    public void setValue(LongFilter value) {
        this.value = value;
    }

    public LongFilter getBuildStepId() {
        return buildStepId;
    }

    public void setBuildStepId(LongFilter buildStepId) {
        this.buildStepId = buildStepId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BuildStepStatisticCriteria that = (BuildStepStatisticCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(key, that.key) &&
            Objects.equals(value, that.value) &&
            Objects.equals(buildStepId, that.buildStepId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        key,
        value,
        buildStepId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BuildStepStatisticCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (key != null ? "key=" + key + ", " : "") +
                (value != null ? "value=" + value + ", " : "") +
                (buildStepId != null ? "buildStepId=" + buildStepId + ", " : "") +
            "}";
    }

}
