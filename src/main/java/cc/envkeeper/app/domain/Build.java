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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import cc.envkeeper.app.domain.enumeration.BuildStatus;

/**
 * A Build.
 */
@Entity
@Table(name = "build")
public class Build implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "build_url", nullable = false)
    private String buildUrl;

    @Column(name = "build_name")
    private String buildName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BuildStatus status;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @OneToMany(mappedBy = "parentBuild")
    private Set<Build> builds = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "builds", allowSetters = true)
    private Build parentBuild;

    @OneToMany(mappedBy = "build")
    private Set<Deployment> deployments = new HashSet<>();

    @OneToMany(mappedBy = "build")
    private Set<ProductVersion> productVersions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public Build buildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
        return this;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public String getBuildName() {
        return buildName;
    }

    public Build buildName(String buildName) {
        this.buildName = buildName;
        return this;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public Build status(BuildStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(BuildStatus status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Build startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Build endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Set<Build> getBuilds() {
        return builds;
    }

    public Build builds(Set<Build> builds) {
        this.builds = builds;
        return this;
    }

    public Build addBuild(Build build) {
        this.builds.add(build);
        build.setParentBuild(this);
        return this;
    }

    public Build removeBuild(Build build) {
        this.builds.remove(build);
        build.setParentBuild(null);
        return this;
    }

    public void setBuilds(Set<Build> builds) {
        this.builds = builds;
    }

    public Build getParentBuild() {
        return parentBuild;
    }

    public Build parentBuild(Build build) {
        this.parentBuild = build;
        return this;
    }

    public void setParentBuild(Build build) {
        this.parentBuild = build;
    }

    public Set<Deployment> getDeployments() {
        return deployments;
    }

    public Build deployments(Set<Deployment> deployments) {
        this.deployments = deployments;
        return this;
    }

    public Build addDeployment(Deployment deployment) {
        this.deployments.add(deployment);
        deployment.setBuild(this);
        return this;
    }

    public Build removeDeployment(Deployment deployment) {
        this.deployments.remove(deployment);
        deployment.setBuild(null);
        return this;
    }

    public void setDeployments(Set<Deployment> deployments) {
        this.deployments = deployments;
    }

    public Set<ProductVersion> getProductVersions() {
        return productVersions;
    }

    public Build productVersions(Set<ProductVersion> productVersions) {
        this.productVersions = productVersions;
        return this;
    }

    public Build addProductVersion(ProductVersion productVersion) {
        this.productVersions.add(productVersion);
        productVersion.setBuild(this);
        return this;
    }

    public Build removeProductVersion(ProductVersion productVersion) {
        this.productVersions.remove(productVersion);
        productVersion.setBuild(null);
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
        if (!(o instanceof Build)) {
            return false;
        }
        return id != null && id.equals(((Build) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Build{" +
            "id=" + getId() +
            ", buildUrl='" + getBuildUrl() + "'" +
            ", buildName='" + getBuildName() + "'" +
            ", status='" + getStatus() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
