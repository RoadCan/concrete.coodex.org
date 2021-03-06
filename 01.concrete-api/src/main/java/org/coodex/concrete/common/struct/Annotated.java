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

package org.coodex.concrete.common.struct;

import java.lang.annotation.Annotation;

/**
 * Created by davidoff shen on 2016-11-30.
 */
public interface Annotated {
    /**
     * 获取某个注解
     *
     * @param annotationClass
     * @param <T>
     * @return
     */
    <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass);

    /**
     * 获取全部注解
     *
     * @return
     */
    Annotation[] getAnnotations();
}
