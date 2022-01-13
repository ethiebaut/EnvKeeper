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

public class ProductComponentDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductComponentDTO.class);
        ProductComponentDTO productComponentDTO1 = new ProductComponentDTO();
        productComponentDTO1.setId(1L);
        ProductComponentDTO productComponentDTO2 = new ProductComponentDTO();
        assertThat(productComponentDTO1).isNotEqualTo(productComponentDTO2);
        productComponentDTO2.setId(productComponentDTO1.getId());
        assertThat(productComponentDTO1).isEqualTo(productComponentDTO2);
        productComponentDTO2.setId(2L);
        assertThat(productComponentDTO1).isNotEqualTo(productComponentDTO2);
        productComponentDTO1.setId(null);
        assertThat(productComponentDTO1).isNotEqualTo(productComponentDTO2);
    }
}
