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

import org.coodex.concrete.api.Description;
import org.coodex.util.ReflectHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.coodex.util.Common.camelCase;

/**
 * Created by davidoff shen on 2016-11-30.
 */
public abstract class AbstractParam implements Annotated {

    private final ReflectHelper.MethodParameter parameter;
    private final Description description;

    public AbstractParam(Method method, int index) {
        this.parameter = new ReflectHelper.MethodParameter(method, index);
        this.description = getDeclaredAnnotation(Description.class);
    }



    public String getLabel() {
        return description == null ? "" : description.name();
//        return Common.isBlank(s) ? "　" : s;
    }

    public String getDescription() {
        return description == null ? "" : description.description();
//        return Common.isBlank(s) ? "　" : s;
    }
    /**
     * 参数类型
     *
     * @return
     */
    public Class<?> getType() {
        return parameter.getType();
    }



    @Override
    public Annotation[] getAnnotations() {
        return parameter.getAnnotations();
    }
    /**
     * 参数泛型类型
     *
     * @return
     */
    public Type getGenericType() {
        return parameter.getGenericType();
    }


    /**
     * 参数名
     *
     * @return
     */
    public String getName() {
        return camelCase(parameter.getName());
    }


    /**
     * 参数索引号
     *
     * @return
     */
    public int getIndex() {
        return parameter.getIndex();
    }

    @Override
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

}
