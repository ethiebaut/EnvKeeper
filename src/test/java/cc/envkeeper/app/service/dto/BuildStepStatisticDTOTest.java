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

public class BuildStepStatisticDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BuildStepStatisticDTO.class);
        BuildStepStatisticDTO buildStepStatisticDTO1 = new BuildStepStatisticDTO();
        buildStepStatisticDTO1.setId(1L);
        BuildStepStatisticDTO buildStepStatisticDTO2 = new BuildStepStatisticDTO();
        assertThat(buildStepStatisticDTO1).isNotEqualTo(buildStepStatisticDTO2);
        buildStepStatisticDTO2.setId(buildStepStatisticDTO1.getId());
        assertThat(buildStepStatisticDTO1).isEqualTo(buildStepStatisticDTO2);
        buildStepStatisticDTO2.setId(2L);
        assertThat(buildStepStatisticDTO1).isNotEqualTo(buildStepStatisticDTO2);
        buildStepStatisticDTO1.setId(null);
        assertThat(buildStepStatisticDTO1).isNotEqualTo(buildStepStatisticDTO2);
    }
}
