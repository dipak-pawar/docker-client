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

package io.fabric8.docker.examples;

import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.EventListener;
import io.fabric8.docker.dsl.OutputHandle;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ContainerLogsExample {

    public static void main(String args[]) throws InterruptedException, IOException {

        if (args.length < 2) {
            System.err.println("Usage: ContainerLogsExample <docker url>");
            System.err.println("Optionally: ContainerLogsExample <docker url> <container id>");
            return;
        }

        String dockerUrl = args[0];
        String containerId = args[1];


        Config config = new ConfigBuilder()
                .withDockerUrl(dockerUrl)
                .build();

        DockerClient client = new DefaultDockerClient(config);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        OutputHandle handle = client.container().withName(containerId)
            .logs().writingOutput(System.out)
            .writingError(System.err).
                usingListener(new EventListener() {
                    @Override
                    public void onSuccess(String message) {
                        System.out.println(message);
                    }

                    @Override
                    public void onError(String message) {
                        System.out.println(message);

                    }

                    @Override
                    public void onEvent(String event) {
                        System.out.println(event);
                    }
                }).follow();

        countDownLatch.await(10, TimeUnit.SECONDS);

        handle.close();
        client.close();
    }
}
