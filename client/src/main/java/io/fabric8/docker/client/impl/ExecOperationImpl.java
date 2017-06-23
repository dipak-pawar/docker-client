/*
 * Copyright (C) 2016 Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.fabric8.docker.client.impl;

import io.fabric8.docker.api.model.ContainerInspect;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.dsl.container.ContainerExecResourceOutputErrorUsingListenerInterface;
import io.fabric8.docker.dsl.container.ExecInterface;
import okhttp3.OkHttpClient;

public class ExecOperationImpl extends OperationSupport implements ExecInterface {

    public ExecOperationImpl(OkHttpClient client, Config config, String resourceType) {
        super(client, config, resourceType);
    }

    @Override
    public ContainerExecResourceOutputErrorUsingListenerInterface<Boolean, ContainerInspect> withName(String name) {
        return new ExecNamedOperationImpl(this.client, this.config, name, null, null, null, null, NULL_LISTENER);
    }
}
