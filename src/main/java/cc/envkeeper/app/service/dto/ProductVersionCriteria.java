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
 * Criteria class for the {@link cc.envkeeper.app.domain.ProductVersion} entity. This class is used
 * in {@link cc.envkeeper.app.web.rest.ProductVersionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /product-versions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductVersionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter version;

    private StringFilter releaseNotes;

    private LongFilter productId;

    private LongFilter buildId;

    private LongFilter componentId;

    public ProductVersionCriteria() {
    }

    public ProductVersionCriteria(ProductVersionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.releaseNotes = other.releaseNotes == null ? null : other.releaseNotes.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.buildId = other.buildId == null ? null : other.buildId.copy();
        this.componentId = other.componentId == null ? null : other.componentId.copy();
    }

    @Override
    public ProductVersionCriteria copy() {
        return new ProductVersionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getVersion() {
        return version;
    }

    public void setVersion(StringFilter version) {
        this.version = version;
    }

    public StringFilter getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(StringFilter releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public LongFilter getBuildId() {
        return buildId;
    }

    public void setBuildId(LongFilter buildId) {
        this.buildId = buildId;
    }

    public LongFilter getComponentId() {
        return componentId;
    }

    public void setComponentId(LongFilter componentId) {
        this.componentId = componentId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductVersionCriteria that = (ProductVersionCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(version, that.version) &&
            Objects.equals(releaseNotes, that.releaseNotes) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(buildId, that.buildId) &&
            Objects.equals(componentId, that.componentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        version,
        releaseNotes,
        productId,
        buildId,
        componentId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVersionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (version != null ? "version=" + version + ", " : "") +
                (releaseNotes != null ? "releaseNotes=" + releaseNotes + ", " : "") +
                (productId != null ? "productId=" + productId + ", " : "") +
                (buildId != null ? "buildId=" + buildId + ", " : "") +
                (componentId != null ? "componentId=" + componentId + ", " : "") +
            "}";
    }

}
