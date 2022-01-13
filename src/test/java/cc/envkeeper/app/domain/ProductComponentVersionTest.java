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

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import cc.envkeeper.app.web.rest.TestUtil;

public class ProductComponentVersionTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductComponentVersion.class);
        ProductComponentVersion productComponentVersion1 = new ProductComponentVersion();
        productComponentVersion1.setId(1L);
        ProductComponentVersion productComponentVersion2 = new ProductComponentVersion();
        productComponentVersion2.setId(productComponentVersion1.getId());
        assertThat(productComponentVersion1).isEqualTo(productComponentVersion2);
        productComponentVersion2.setId(2L);
        assertThat(productComponentVersion1).isNotEqualTo(productComponentVersion2);
        productComponentVersion1.setId(null);
        assertThat(productComponentVersion1).isNotEqualTo(productComponentVersion2);
    }
}
