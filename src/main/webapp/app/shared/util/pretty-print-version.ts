
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

export const prettyPrintVersion = (version?: String): String | undefined => {

  if (!version) {
    return version;
  }
  const count = (version.match(/\./g) || []).length;
  let pos = version.lastIndexOf("-");
  if (count === 2 && pos !== -1) {
    pos = version.lastIndexOf("-", pos - 1);
  }
  if (pos !== -1) {
    return version.substr(0, pos);
  } else {
    return version;
  }
}
