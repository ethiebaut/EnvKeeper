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

package cc.envkeeper.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import cc.envkeeper.app.domain.enumeration.BuildStatus;

/**
 * A ProductComponentVersion.
 */
@Entity
@Table(name = "component_version")
public class ProductComponentVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "build_status", nullable = false)
    private BuildStatus buildStatus;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "build_url")
    private String buildUrl;

    @Column(name = "release_notes")
    private String releaseNotes;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "productComponentVersions", allowSetters = true)
    private ProductComponent component;

    @ManyToMany(mappedBy = "components")
    @JsonIgnore
    private Set<ProductVersion> productVersions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public ProductComponentVersion version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BuildStatus getBuildStatus() {
        return buildStatus;
    }

    public ProductComponentVersion buildStatus(BuildStatus buildStatus) {
        this.buildStatus = buildStatus;
        return this;
    }

    public void setBuildStatus(BuildStatus buildStatus) {
        this.buildStatus = buildStatus;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public ProductComponentVersion startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public ProductComponentVersion endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public ProductComponentVersion buildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
        return this;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public ProductComponentVersion releaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
        return this;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public ProductComponent getComponent() {
        return component;
    }

    public ProductComponentVersion component(ProductComponent productComponent) {
        this.component = productComponent;
        return this;
    }

    public void setComponent(ProductComponent productComponent) {
        this.component = productComponent;
    }

    public Set<ProductVersion> getProductVersions() {
        return productVersions;
    }

    public ProductComponentVersion productVersions(Set<ProductVersion> productVersions) {
        this.productVersions = productVersions;
        return this;
    }

    public ProductComponentVersion addProductVersion(ProductVersion productVersion) {
        this.productVersions.add(productVersion);
        productVersion.getComponents().add(this);
        return this;
    }

    public ProductComponentVersion removeProductVersion(ProductVersion productVersion) {
        this.productVersions.remove(productVersion);
        productVersion.getComponents().remove(this);
        return this;
    }

    public void setProductVersions(Set<ProductVersion> productVersions) {
        this.productVersions = productVersions;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductComponentVersion)) {
            return false;
        }
        return id != null && id.equals(((ProductComponentVersion) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductComponentVersion{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", buildStatus='" + getBuildStatus() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", buildUrl='" + getBuildUrl() + "'" +
            ", releaseNotes='" + getReleaseNotes() + "'" +
            "}";
    }
}
