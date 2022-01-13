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

package cc.envkeeper.app.remoteupdate.exception;

public class CorruptedMessageException extends Exception {
    public CorruptedMessageException() {
    }

    public CorruptedMessageException(final String message) {
        super(message);
    }

    public CorruptedMessageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CorruptedMessageException(final Throwable cause) {
        super(cause);
    }

    public CorruptedMessageException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
