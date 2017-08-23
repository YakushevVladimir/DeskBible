/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: SimpleSearchAlgorithm.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.search.algorithm;

class SimpleSearchAlgorithm implements SearchAlgorithm {

    private final String target;

    SimpleSearchAlgorithm(String target) {
        this.target = target;
    }

    @Override
    public int indexOf(String source) {
        return indexOf(source, 0, source.length());
    }

    @Override
    public int indexOf(String source, int fromIndex) {
        return indexOf(source, fromIndex, source.length());
    }

    @Override
    public int indexOf(String source, int fromIndex, int toIndex) {
        if (fromIndex >= source.length()) {
            return (target.length() == 0 ? source.length() : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (toIndex < 0) {
            toIndex = source.length();
        }
        if (target.length() == 0) {
            return fromIndex;
        }

        char first = Character.toLowerCase(target.charAt(0));
        int max = (toIndex - target.length());

        for (int i = fromIndex; i <= max; i++) {
                /* Look for first character. */
            if (Character.toLowerCase(source.charAt(i)) != first) {
                while (++i <= max && Character.toLowerCase(source.charAt(i)) != first) {
                }
            }

                /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + target.length() - 1;
                for (int k = 1;
                        j < end && Character.toLowerCase(source.charAt(j)) == Character.toLowerCase(target.charAt(k));
                        j++, k++) {
                }

                if (j == end) {
                        /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
    }
}
