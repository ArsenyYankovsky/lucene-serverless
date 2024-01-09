/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.arseny.graalvm.substitutions;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

/**
 * Based on the original implementation in quarkus-amazon-services:
 *
 * https://github.com/quarkiverse/quarkus-amazon-services/blob/main/common/runtime/src/main/java/io/quarkus/amazon/common/runtime/CrtSubstitutions.java
 */
public class CrtSubstitutions {
    public static final String QUARKUS_AWSSDK_CONFIG_CLASS_NAME = "io.quarkus.amazon.common.runtime.SdkConfig";
    static final String SOFTWARE_AMAZON_AWSSDK_CRT_PACKAGE = "software.amazon.awssdk.crt";
    static final String SOFTWARE_AMAZON_AWSSDK_HTTP_AUTH_AWS_CRT_PACKAGE = "software.amazon.awssdk.http.auth.aws.crt";

    static final class IsCrtAbsent implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return isQuarkusAwsAbsent() && Arrays.stream(Package.getPackages())
                    .map(Package::getName)
                    .noneMatch(p -> p.equals(SOFTWARE_AMAZON_AWSSDK_CRT_PACKAGE));
        }
    }

    static final class IsHttpAuthAwsCrtAbsent implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return isQuarkusAwsAbsent() && Arrays.stream(Package.getPackages())
                    .map(Package::getName)
                    .noneMatch(p -> p.equals(SOFTWARE_AMAZON_AWSSDK_HTTP_AUTH_AWS_CRT_PACKAGE));
        }
    }

    static boolean isQuarkusAwsAbsent() {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(QUARKUS_AWSSDK_CONFIG_CLASS_NAME);
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }
}

