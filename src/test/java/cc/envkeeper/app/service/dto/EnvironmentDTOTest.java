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

public class EnvironmentDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvironmentDTO.class);
        EnvironmentDTO environmentDTO1 = new EnvironmentDTO();
        environmentDTO1.setId(1L);
        EnvironmentDTO environmentDTO2 = new EnvironmentDTO();
        assertThat(environmentDTO1).isNotEqualTo(environmentDTO2);
        environmentDTO2.setId(environmentDTO1.getId());
        assertThat(environmentDTO1).isEqualTo(environmentDTO2);
        environmentDTO2.setId(2L);
        assertThat(environmentDTO1).isNotEqualTo(environmentDTO2);
        environmentDTO1.setId(null);
        assertThat(environmentDTO1).isNotEqualTo(environmentDTO2);
    }
}
