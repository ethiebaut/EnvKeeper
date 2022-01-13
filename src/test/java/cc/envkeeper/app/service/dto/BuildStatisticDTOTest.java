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

public class BuildStatisticDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BuildStatisticDTO.class);
        BuildStatisticDTO buildStatisticDTO1 = new BuildStatisticDTO();
        buildStatisticDTO1.setId(1L);
        BuildStatisticDTO buildStatisticDTO2 = new BuildStatisticDTO();
        assertThat(buildStatisticDTO1).isNotEqualTo(buildStatisticDTO2);
        buildStatisticDTO2.setId(buildStatisticDTO1.getId());
        assertThat(buildStatisticDTO1).isEqualTo(buildStatisticDTO2);
        buildStatisticDTO2.setId(2L);
        assertThat(buildStatisticDTO1).isNotEqualTo(buildStatisticDTO2);
        buildStatisticDTO1.setId(null);
        assertThat(buildStatisticDTO1).isNotEqualTo(buildStatisticDTO2);
    }
}
