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

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link cc.envkeeper.app.domain.BuildStepStatistic} entity.
 */
public class BuildStepStatisticDTO implements Serializable {

    private Long id;

    @NotNull
    private String key;

    @NotNull
    private Long value;


    private Long buildStepId;

    private String buildStepStep;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getBuildStepId() {
        return buildStepId;
    }

    public void setBuildStepId(Long buildStepId) {
        this.buildStepId = buildStepId;
    }

    public String getBuildStepStep() {
        return buildStepStep;
    }

    public void setBuildStepStep(String buildStepStep) {
        this.buildStepStep = buildStepStep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BuildStepStatisticDTO)) {
            return false;
        }

        return id != null && id.equals(((BuildStepStatisticDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BuildStepStatisticDTO{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value=" + getValue() +
            ", buildStepId=" + getBuildStepId() +
            ", buildStepStep='" + getBuildStepStep() + "'" +
            "}";
    }
}
