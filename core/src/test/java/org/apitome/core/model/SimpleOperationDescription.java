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

package org.apitome.core.model;

import org.apitome.core.metadata.OperationDescriptionTransformer;

public class SimpleOperationDescription extends _TestOperationDescription {

    private _OperationConfig operationConfig;

    public _OperationConfig getOperationConfig() {
        return operationConfig;
    }

    public void setOperationConfig(_OperationConfig operationConfig) {
        this.operationConfig = operationConfig;
    }

    @Override
    public TestOperationDescription accept(OperationDescriptionTransformer transformer) {
        return transformer.transform(this);
    }
}
