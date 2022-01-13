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

public class EnvironmentGroupTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvironmentGroup.class);
        EnvironmentGroup environmentGroup1 = new EnvironmentGroup();
        environmentGroup1.setId(1L);
        EnvironmentGroup environmentGroup2 = new EnvironmentGroup();
        environmentGroup2.setId(environmentGroup1.getId());
        assertThat(environmentGroup1).isEqualTo(environmentGroup2);
        environmentGroup2.setId(2L);
        assertThat(environmentGroup1).isNotEqualTo(environmentGroup2);
        environmentGroup1.setId(null);
        assertThat(environmentGroup1).isNotEqualTo(environmentGroup2);
    }
}
