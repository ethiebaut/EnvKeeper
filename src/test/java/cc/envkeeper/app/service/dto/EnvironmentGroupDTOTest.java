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

public class EnvironmentGroupDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvironmentGroupDTO.class);
        EnvironmentGroupDTO environmentGroupDTO1 = new EnvironmentGroupDTO();
        environmentGroupDTO1.setId(1L);
        EnvironmentGroupDTO environmentGroupDTO2 = new EnvironmentGroupDTO();
        assertThat(environmentGroupDTO1).isNotEqualTo(environmentGroupDTO2);
        environmentGroupDTO2.setId(environmentGroupDTO1.getId());
        assertThat(environmentGroupDTO1).isEqualTo(environmentGroupDTO2);
        environmentGroupDTO2.setId(2L);
        assertThat(environmentGroupDTO1).isNotEqualTo(environmentGroupDTO2);
        environmentGroupDTO1.setId(null);
        assertThat(environmentGroupDTO1).isNotEqualTo(environmentGroupDTO2);
    }
}
