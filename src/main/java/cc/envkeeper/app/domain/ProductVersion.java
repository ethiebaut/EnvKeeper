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
import java.util.HashSet;
import java.util.Set;

/**
 * A ProductVersion.
 */
@Entity
@Table(name = "product_version")
public class ProductVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "release_notes")
    private String releaseNotes;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "productVersions", allowSetters = true)
    private Product product;

    @ManyToOne
    @JsonIgnoreProperties(value = "productVersions", allowSetters = true)
    private Build build;

    @ManyToMany
    @JoinTable(name = "product_version_component",
               joinColumns = @JoinColumn(name = "product_version_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "component_id", referencedColumnName = "id"))
    private Set<ProductComponentVersion> components = new HashSet<>();

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

    public ProductVersion version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public ProductVersion releaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
        return this;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public Product getProduct() {
        return product;
    }

    public ProductVersion product(Product product) {
        this.product = product;
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Build getBuild() {
        return build;
    }

    public ProductVersion build(Build build) {
        this.build = build;
        return this;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    public Set<ProductComponentVersion> getComponents() {
        return components;
    }

    public ProductVersion components(Set<ProductComponentVersion> productComponentVersions) {
        this.components = productComponentVersions;
        return this;
    }

    public ProductVersion addComponent(ProductComponentVersion productComponentVersion) {
        this.components.add(productComponentVersion);
        productComponentVersion.getProductVersions().add(this);
        return this;
    }

    public ProductVersion removeComponent(ProductComponentVersion productComponentVersion) {
        this.components.remove(productComponentVersion);
        productComponentVersion.getProductVersions().remove(this);
        return this;
    }

    public void setComponents(Set<ProductComponentVersion> productComponentVersions) {
        this.components = productComponentVersions;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVersion)) {
            return false;
        }
        return id != null && id.equals(((ProductVersion) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVersion{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", releaseNotes='" + getReleaseNotes() + "'" +
            "}";
    }
}
