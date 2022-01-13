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

public class BuildDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BuildDTO.class);
        BuildDTO buildDTO1 = new BuildDTO();
        buildDTO1.setId(1L);
        BuildDTO buildDTO2 = new BuildDTO();
        assertThat(buildDTO1).isNotEqualTo(buildDTO2);
        buildDTO2.setId(buildDTO1.getId());
        assertThat(buildDTO1).isEqualTo(buildDTO2);
        buildDTO2.setId(2L);
        assertThat(buildDTO1).isNotEqualTo(buildDTO2);
        buildDTO1.setId(null);
        assertThat(buildDTO1).isNotEqualTo(buildDTO2);
    }
}
