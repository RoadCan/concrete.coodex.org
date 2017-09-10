/*
 * Copyright (c) 2017 coodex.org (jujus.shen@126.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.coodex.concrete.common;

import org.coodex.concrete.common.Subjoin;
import org.coodex.util.Common;

import java.util.*;

public abstract class AbstractSubjoin implements Subjoin {

    private Map<String, List<String>> stringMap = new HashMap<String, List<String>>();
    private Locale locale = Locale.getDefault();

    @Override
    public Locale getLocale() {
        return locale;
    }

    protected void setLocale(Locale locale) {
        if (locale != null)
            this.locale = locale;
    }

    @Override
    public String get(String name) {
        return get(name, "; ");
    }

    @Override
    public String get(String name, String split) {
        return Common.concat(getList(name), split);
    }

    @Override
    public List<String> getList(String name) {
        return stringMap.get(name);
    }

    @Override
    public Set<String> keySet() {
        return stringMap.keySet();
    }

    @Override
    public void set(String name, List<String> values) {
        stringMap.put(name, values);
    }

    @Override
    public void add(String name, String value) {
        List<String> list = stringMap.get(name);
        if (list == null) {
            list = new ArrayList<String>();
            stringMap.put(name, list);
        }
        list.add(value);
    }
}
