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
package io.fabric8.docker.client.utils;

import io.fabric8.docker.api.model.Callback;
import io.fabric8.docker.client.DockerStreamData;
import io.fabric8.docker.client.DockerStreamDataReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerStreamPumper implements Runnable, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputStreamReader.class);

    private final BufferedSource in;
    private final Callback<DockerStreamData, Void> callback;
    private final Callback<Boolean, Void> onFinish;
    private boolean keepReading = true;
    private Thread thread;

    public DockerStreamPumper(BufferedSource in, Callback<DockerStreamData, Void> callback) {
        this(in, callback, new Callback<Boolean, Void>() {
            @Override
            public Void call(Boolean input) {
                return null;
            }
        });
    }

    public DockerStreamPumper(BufferedSource in, Callback<DockerStreamData, Void> callback,
        Callback<Boolean, Void> onFinish) {
        this.in = in;
        this.callback = callback;
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        try {
            while (keepReading && !Thread.currentThread().isInterrupted() && !in.exhausted()) {
                final DockerStreamDataReader dockerStreamDataReader = new DockerStreamDataReader(in.inputStream());
                callback.call(dockerStreamDataReader.readStreamData());
            }

            //To indicate that the response has been fully read.
            onFinish.call(true);
        } catch (InterruptedIOException e) {
            LOGGER.debug("Interrupted while pumping stream.", e);
            onFinish.call(false);
        } catch (IOException e) {
            onFinish.call(false);
            if (!Thread.currentThread().isInterrupted()) {
                LOGGER.error("Error while pumping stream.", e);
            } else {
                LOGGER.debug("Interrupted while pumping stream.", e);
            }
        }
    }

    public void close() {
        keepReading = false;
        if (thread != null) {
            thread.interrupt();
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.warn("Error while closing buffered source:" + e.getMessage());
            }
        }
    }
}
