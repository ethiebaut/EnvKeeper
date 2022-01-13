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

public class ProductComponentVersionDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductComponentVersionDTO.class);
        ProductComponentVersionDTO productComponentVersionDTO1 = new ProductComponentVersionDTO();
        productComponentVersionDTO1.setId(1L);
        ProductComponentVersionDTO productComponentVersionDTO2 = new ProductComponentVersionDTO();
        assertThat(productComponentVersionDTO1).isNotEqualTo(productComponentVersionDTO2);
        productComponentVersionDTO2.setId(productComponentVersionDTO1.getId());
        assertThat(productComponentVersionDTO1).isEqualTo(productComponentVersionDTO2);
        productComponentVersionDTO2.setId(2L);
        assertThat(productComponentVersionDTO1).isNotEqualTo(productComponentVersionDTO2);
        productComponentVersionDTO1.setId(null);
        assertThat(productComponentVersionDTO1).isNotEqualTo(productComponentVersionDTO2);
    }
}
