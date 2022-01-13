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

public class BuildStepStatisticTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BuildStepStatistic.class);
        BuildStepStatistic buildStepStatistic1 = new BuildStepStatistic();
        buildStepStatistic1.setId(1L);
        BuildStepStatistic buildStepStatistic2 = new BuildStepStatistic();
        buildStepStatistic2.setId(buildStepStatistic1.getId());
        assertThat(buildStepStatistic1).isEqualTo(buildStepStatistic2);
        buildStepStatistic2.setId(2L);
        assertThat(buildStepStatistic1).isNotEqualTo(buildStepStatistic2);
        buildStepStatistic1.setId(null);
        assertThat(buildStepStatistic1).isNotEqualTo(buildStepStatistic2);
    }
}
