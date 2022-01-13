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
 * A DTO for the {@link cc.envkeeper.app.domain.Environment} entity.
 */
public class EnvironmentDTO implements Serializable {

    private Long id;

    @NotNull
    private String shortName;

    @NotNull
    private String fullName;

    @NotNull
    private String description;

    private Integer sortOrder;


    private Long environmentGroupId;

    private String environmentGroupShortName;

    private Integer environmentGroupSortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getEnvironmentGroupId() {
        return environmentGroupId;
    }

    public void setEnvironmentGroupId(Long environmentGroupId) {
        this.environmentGroupId = environmentGroupId;
    }

    public String getEnvironmentGroupShortName() {
        return environmentGroupShortName;
    }

    public void setEnvironmentGroupShortName(String environmentGroupShortName) {
        this.environmentGroupShortName = environmentGroupShortName;
    }

    public Integer getEnvironmentGroupSortOrder() {
        return environmentGroupSortOrder;
    }

    public void setEnvironmentGroupSortOrder(final Integer environmentGroupSortOrder) {
        this.environmentGroupSortOrder = environmentGroupSortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentDTO)) {
            return false;
        }

        return id != null && id.equals(((EnvironmentDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvironmentDTO{" +
            "id=" + getId() +
            ", shortName='" + getShortName() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", description='" + getDescription() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", environmentGroupId=" + getEnvironmentGroupId() +
            ", environmentGroupShortName='" + getEnvironmentGroupShortName() + "'" +
            ", environmentGroupSortOrder=" + getEnvironmentGroupSortOrder() +
            "}";
    }
}
