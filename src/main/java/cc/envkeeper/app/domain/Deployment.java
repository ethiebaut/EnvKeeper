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

import cc.envkeeper.app.domain.enumeration.DeploymentStatus;

/**
 * A Deployment.
 */
@Entity
@Table(name = "deployment")
public class Deployment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "jhi_user")
    private String user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeploymentStatus status;

    @Column(name = "namespace")
    private String namespace;

    @Column(name = "url")
    private String url;

    @Column(name = "test_url")
    private String testUrl;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "deployments", allowSetters = true)
    private ProductVersion productVersion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "deployments", allowSetters = true)
    private Environment environment;

    @ManyToOne
    @JsonIgnoreProperties(value = "deployments", allowSetters = true)
    private Build build;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Deployment startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Deployment endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getUser() {
        return user;
    }

    public Deployment user(String user) {
        this.user = user;
        return this;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public Deployment status(DeploymentStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public String getNamespace() {
        return namespace;
    }

    public Deployment namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUrl() {
        return url;
    }

    public Deployment url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public Deployment testUrl(String testUrl) {
        this.testUrl = testUrl;
        return this;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public ProductVersion getProductVersion() {
        return productVersion;
    }

    public Deployment productVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
        return this;
    }

    public void setProductVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Deployment environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Build getBuild() {
        return build;
    }

    public Deployment build(Build build) {
        this.build = build;
        return this;
    }

    public void setBuild(Build build) {
        this.build = build;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deployment)) {
            return false;
        }
        return id != null && id.equals(((Deployment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Deployment{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", user='" + getUser() + "'" +
            ", status='" + getStatus() + "'" +
            ", namespace='" + getNamespace() + "'" +
            ", url='" + getUrl() + "'" +
            ", testUrl='" + getTestUrl() + "'" +
            "}";
    }
}
