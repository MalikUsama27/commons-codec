/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(value = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class CodecBenchmark {

    private Base64 base64;
    private Hex hex;
    private byte[] data;

    @State(Scope.Thread)
    public static class BenchmarkState {
        byte[] smallData = "Hello World!".getBytes(StandardCharsets.UTF_8);
        byte[] largeData = new byte[1024 * 1024]; // 1MB of data
        {
            for (int i = 0; i < largeData.length; i++) {
                largeData[i] = (byte) (i % 256);
            }
        }
    }

    @Setup
    public void setup() {
        base64 = new Base64();
        hex = new Hex();
    }

    @Benchmark
    public byte[] encodeBase64(BenchmarkState state) {
        return base64.encode(state.smallData);
    }

    @Benchmark
    public byte[] decodeBase64(BenchmarkState state) {
        return base64.decode(base64.encode(state.smallData));
    }

    @Benchmark
    public byte[] encodeHex(BenchmarkState state) {
        return hex.encode(state.smallData);
    }

    @Benchmark
    public byte[] decodeHex(BenchmarkState state) throws DecoderException {
        return hex.decode(hex.encode(state.smallData));
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.runner.options.Options opt = new org.openjdk.jmh.runner.options.OptionsBuilder()
                .include(CodecBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new org.openjdk.jmh.runner.Runner(opt).run();
    }
}