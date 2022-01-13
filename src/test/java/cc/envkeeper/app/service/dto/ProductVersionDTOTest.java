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

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import cc.envkeeper.app.web.rest.TestUtil;

public class ProductVersionDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVersionDTO.class);
        ProductVersionDTO productVersionDTO1 = new ProductVersionDTO();
        productVersionDTO1.setId(1L);
        ProductVersionDTO productVersionDTO2 = new ProductVersionDTO();
        assertThat(productVersionDTO1).isNotEqualTo(productVersionDTO2);
        productVersionDTO2.setId(productVersionDTO1.getId());
        assertThat(productVersionDTO1).isEqualTo(productVersionDTO2);
        productVersionDTO2.setId(2L);
        assertThat(productVersionDTO1).isNotEqualTo(productVersionDTO2);
        productVersionDTO1.setId(null);
        assertThat(productVersionDTO1).isNotEqualTo(productVersionDTO2);
    }
}
