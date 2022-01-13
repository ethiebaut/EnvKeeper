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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A DTO for the {@link cc.envkeeper.app.domain.ProductVersion} entity.
 */
public class ProductVersionDTO implements Serializable {

    private Long id;

    @NotNull
    private String version;

    private String releaseNotes;

    private String releaseNotesBase64;

    private Long productId;

    private String productShortName;

    private Long buildId;
    private Set<ProductComponentVersionDTO> components = new HashSet<>();

    private List<TicketDTO> fixedTickets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getReleaseNotesBase64() {
        return releaseNotesBase64;
    }

    public void setReleaseNotesBase64(final String releaseNotesBase64) {
        this.releaseNotesBase64 = releaseNotesBase64;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }

    public Long getBuildId() {
        return buildId;
    }

    public void setBuildId(Long buildId) {
        this.buildId = buildId;
    }

    public Set<ProductComponentVersionDTO> getComponents() {
        return components;
    }

    public void setComponents(Set<ProductComponentVersionDTO> productComponentVersions) {
        this.components = productComponentVersions;
    }

    public List<TicketDTO> getFixedTickets() {
        return fixedTickets;
    }

    public void setFixedTickets(final List<TicketDTO> fixedTickets) {
        this.fixedTickets = fixedTickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVersionDTO)) {
            return false;
        }

        return id != null && id.equals(((ProductVersionDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVersionDTO{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", releaseNotes='" + getReleaseNotes() + "'" +
            ", productId=" + getProductId() +
            ", productShortName='" + getProductShortName() + "'" +
            ", buildId=" + getBuildId() +
            ", components='" + getComponents() + "'" +
            ", fixedTickets='" + getFixedTickets() + "'" +
            "}";
    }
}
