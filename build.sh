#!/usr/bin/env bash

#
# Copyright (c) 2022 Eric Thiebaut-George.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#

set -e

VERSION=`xq -r .project.version pom.xml`

echo "Building version ${VERSION}..."
./mvnw -Pprod,swagger,kafka -Dmaven.test.skip=true compile jib:dockerBuild

echo "Pushing ethiebaut/envkeeper:${VERSION} ..."
docker tag ethiebaut/envkeeper:latest ethiebaut/envkeeper:${VERSION}
docker push ethiebaut/envkeeper:${VERSION}
docker push ethiebaut/envkeeper:latest
