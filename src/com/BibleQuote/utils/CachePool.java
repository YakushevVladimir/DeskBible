/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class CachePool<T> extends LinkedHashMap<String, T> {
	private static final long serialVersionUID = -366362500655090729L;
	private static final int MAX_POOL_SIZE = 20;

	public CachePool() {
		super(MAX_POOL_SIZE, .75f, true);
	}
	
	public CachePool(int maxPoolSize) {
		super(maxPoolSize, .75f, true);
	}
	
    protected boolean removeEldestEntry(@SuppressWarnings("rawtypes") Map.Entry eldest) {
        return size() > MAX_POOL_SIZE;
    }
}
