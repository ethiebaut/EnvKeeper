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

export const prettyPrintIntervalMS = (interval: number): string  => {

  const twoDigit = (num: number): string => {
    if (num >= 10) {
      return "" + num;
    } else {
      return "0" + num;
    }
  };

  const secs = Math.trunc(interval / 1000) % 60;
  const mins = Math.trunc(interval / 60000) % 60;
  const hours = Math.trunc(interval / 3600000);
  if (hours > 0) {
    return hours + ":" + twoDigit(mins) + ":" + twoDigit(secs);
  } else {
    return mins + ":" + twoDigit(secs);
  }
}
