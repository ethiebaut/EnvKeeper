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
 * Criteria class for the {@link cc.envkeeper.app.domain.ProductComponent} entity. This class is used
 * in {@link cc.envkeeper.app.web.rest.ProductComponentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /product-components?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductComponentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter shortName;

    private StringFilter fullName;

    private StringFilter description;

    public ProductComponentCriteria() {
    }

    public ProductComponentCriteria(ProductComponentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.shortName = other.shortName == null ? null : other.shortName.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.description = other.description == null ? null : other.description.copy();
    }

    @Override
    public ProductComponentCriteria copy() {
        return new ProductComponentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getShortName() {
        return shortName;
    }

    public void setShortName(StringFilter shortName) {
        this.shortName = shortName;
    }

    public StringFilter getFullName() {
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductComponentCriteria that = (ProductComponentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(shortName, that.shortName) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        shortName,
        fullName,
        description
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductComponentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (shortName != null ? "shortName=" + shortName + ", " : "") +
                (fullName != null ? "fullName=" + fullName + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
            "}";
    }

}
