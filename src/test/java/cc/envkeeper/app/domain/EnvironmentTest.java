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

public class EnvironmentTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Environment.class);
        Environment environment1 = new Environment();
        environment1.setId(1L);
        Environment environment2 = new Environment();
        environment2.setId(environment1.getId());
        assertThat(environment1).isEqualTo(environment2);
        environment2.setId(2L);
        assertThat(environment1).isNotEqualTo(environment2);
        environment1.setId(null);
        assertThat(environment1).isNotEqualTo(environment2);
    }
}
