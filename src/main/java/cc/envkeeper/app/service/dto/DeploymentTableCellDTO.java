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

public class DeploymentTableCellDTO {

    private ProductDTO product;

    private Long productVersionId;

    private String productVersionVersion;

    private Long previousProductVersionId;

    private String previousProductVersionVersion;

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(final ProductDTO product) {
        this.product = product;
    }

    public Long getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(final Long productVersionId) {
        this.productVersionId = productVersionId;
    }

    public String getProductVersionVersion() {
        return productVersionVersion;
    }

    public void setProductVersionVersion(final String productVersionVersion) {
        this.productVersionVersion = productVersionVersion;
    }

    public Long getPreviousProductVersionId() {
        return previousProductVersionId;
    }

    public void setPreviousProductVersionId(final Long previousProductVersionId) {
        this.previousProductVersionId = previousProductVersionId;
    }

    public String getPreviousProductVersionVersion() {
        return previousProductVersionVersion;
    }

    public void setPreviousProductVersionVersion(final String previousProductVersionVersion) {
        this.previousProductVersionVersion = previousProductVersionVersion;
    }

    @Override
    public String toString() {
        return "DeploymentTableCellDTO{" +
               "product=" + product +
               ", productVersionId=" + productVersionId +
               ", productVersionVersion='" + productVersionVersion + '\'' +
               ", previousProductVersionId=" + previousProductVersionId +
               ", previousProductVersionVersion='" + previousProductVersionVersion + '\'' +
               '}';
    }
}
