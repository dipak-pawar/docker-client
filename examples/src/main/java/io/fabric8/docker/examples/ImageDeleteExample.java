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

import io.fabric8.docker.api.model.ImageDelete;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.client.utils.Utils;
import io.fabric8.docker.dsl.EventListener;
import io.fabric8.docker.dsl.OutputHandle;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ImageDeleteExample {

    private static final String DEFAULT_IMAGE = "image1";

    public static void main(String args[]) throws InterruptedException, IOException {

        if (args.length == 0) {
            System.err.println("Usage: ImagePushExample <docker url>");
            System.err.println("Optionally: ImageDeleteExample <docker url> <image name>");
            return;
        }

        String dockerUrl = args[0];
        String image = args.length >= 2 ? args[1] : DEFAULT_IMAGE;

        Config config = new ConfigBuilder()
                .withDockerUrl(dockerUrl)
                .build();

        DockerClient client = new DefaultDockerClient(config);
        List<ImageDelete> imageDeleteList = client.image().withName(image).delete().force().andPrune();
        for (ImageDelete imageDelete : imageDeleteList) {
            if (Utils.isNotNullOrEmpty(imageDelete.getDeleted())) {
                System.out.println("Deleted:"+imageDelete.getDeleted());
            }
            if (Utils.isNotNullOrEmpty(imageDelete.getUntagged())) {
                System.out.println("Untagged:"+imageDelete.getUntagged());
            }
        }
        client.close();
    }
}
