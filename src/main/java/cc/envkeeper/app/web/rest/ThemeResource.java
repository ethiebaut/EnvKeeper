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

package cc.envkeeper.app.web.rest;

import cc.envkeeper.app.config.ApplicationProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * REST controller for custom styling.
 * <p>
 * This class uses the {@link cc.envkeeper.app.config.ApplicationProperties} to get custom theming information
 */
@RestController
@RequestMapping("/api")
public class ThemeResource {
    private final ApplicationProperties applicationProperties;

    public ThemeResource(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    /**
     * {@code GET /custom-style.css} : get custom stylesheet.
     *
     * @return the custom stylesheet.
     */
    @GetMapping("/style/custom-style.css")
    public ResponseEntity<String> getAllUsers(Pageable pageable) {
        String style = applicationProperties.getStyle().getStylesheet();
        if (StringUtils.isEmpty(style)) {
            return ResponseEntity.notFound().build();
        }
        try (Stream<String> stream = Files.lines(Paths.get(style), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            stream.forEach(s -> sb.append(s).append("\n"));
            return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("text/css"))
                .body(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
