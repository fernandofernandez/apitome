/*
 * Copyright (c) 2022. Fernando Fernandez.
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
 */

package org.apitome.core.metadata;

import java.io.InputStream;

public class TemplateInput {

    private final String templateName;

    private final InputStream inputStream;

    public TemplateInput(String templateName, InputStream inputStream) {
        this.templateName = templateName;
        this.inputStream = inputStream;
    }

    public String getTemplateName() {
        return templateName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
