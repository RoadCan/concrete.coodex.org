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

package org.coodex.concrete.common.bytecode.javassist;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by davidoff shen on 2016-11-24.
 */
public class JavassistHelper {

    private final static Logger log = LoggerFactory.getLogger(JavassistHelper.class);

//    public static String getGenericSignature(ParameterizedType type){
//        List<String> arguments = new ArrayList<String>();
//        for(Type t : type.getActualTypeArguments()){
//            if(t instanceof ParameterizedType){
//                arguments.add(getGenericSignature((ParameterizedType) t));
//            } else if(t instanceof Class){
//                if(((Class) t).isArray())
//                arguments.add(((Class) t).getName())
//            }
//        }
//        return null;
//    }

    public static SignatureAttribute.ClassType classType(String className, String... arguments) {
        Collection<SignatureAttribute.TypeArgument> args = new ArrayList<SignatureAttribute.TypeArgument>();
        for (String arg : arguments) {
            args.add(new SignatureAttribute.TypeArgument(new SignatureAttribute.ClassType(arg)));
        }
        return new SignatureAttribute.ClassType(className, args.toArray(new SignatureAttribute.TypeArgument[0]));
    }

    public static String getTypeName(Class<?> c) {
        return c.getName();
    }

    public static String getSignature(SignatureAttribute.Type type) {
        if (type instanceof SignatureAttribute.ObjectType) {
            return ((SignatureAttribute.ObjectType) type).encode();
        }
        return null;
    }

    public static SignatureAttribute.Type classType(java.lang.reflect.Type t,
                                                    Class<?> contextClass) {
        if (t instanceof Class) {
            Class<?> c = (Class<?>) t;
            if (c.isArray()) {
                // 数组，建议使用List或者Set代替
//                log.warn("Suggestion: using List or Set instead Array Type. ");
                // return new ClassType(List.class.getName(),
                // new TypeArgument[] { new TypeArgument(classType(
                // c.getComponentType(), contextClass)) });
                int dim = 0;
                while (c.isArray()) {
                    dim++;
                    c = c.getComponentType();
                }
                return new SignatureAttribute.ArrayType(dim,
                        c.isPrimitive() ?
                                new SignatureAttribute.BaseType(getTypeName(c)) :
                                new SignatureAttribute.ClassType(getTypeName(c)));
            } else {
                if (c.isPrimitive()) {
                    return new SignatureAttribute.BaseType(c.getName());
                } else
                    return new SignatureAttribute.ClassType(getTypeName(c));
            }
        } else if (t instanceof ParameterizedType) {
            java.lang.reflect.Type[] types = ((ParameterizedType) t)
                    .getActualTypeArguments();
            Collection<SignatureAttribute.TypeArgument> args = new ArrayList<SignatureAttribute.TypeArgument>();
            for (int i = 0; i < types.length; i++) {
                args.add(new SignatureAttribute.TypeArgument((SignatureAttribute.ObjectType) classType(types[i], contextClass)));
            }
            return new SignatureAttribute.ClassType(
                    ((Class<?>) ((ParameterizedType) t).getRawType()).getName(),
                    args.toArray(new SignatureAttribute.TypeArgument[0]));
        } else if (t instanceof TypeVariable) {
            @SuppressWarnings("unchecked")
            Type ttt = find(contextClass,
                    (TypeVariable<Class<?>>) t);
            if (ttt == null) {
                log.warn("WARN!! UnsupportedType: {}", t);
                return null;
            }
            return classType(ttt, contextClass);
        } else {
            log.warn("WARN!! UnsupportedType: {}", t);
            return null;
        }
    }

    private static Type find(Class<?> contextClass,
                             TypeVariable<Class<?>> t) {
        Class<?> clz = t.getGenericDeclaration();
        java.lang.reflect.Type[] supers = contextClass.getGenericInterfaces();
        for (java.lang.reflect.Type $ : supers) {
            if ($ instanceof Class) {
                if (clz.isAssignableFrom((Class<?>) $))
                    return find((Class<?>) $, t);
            } else if ($ instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) $;
                Class<?> rawType = (Class<?>) pt.getRawType();
                if (clz.isAssignableFrom(rawType)) {
                    java.lang.reflect.Type tt = find(rawType, t);
                    if (tt == null) {
                        return pt.getActualTypeArguments()[indexOf(t)];
                    } else
                        return tt;
                }
            }
        }
        return null;
    }

    private static int indexOf(TypeVariable<Class<?>> t) {
        Type[] params = t.getGenericDeclaration()
                .getTypeParameters();
        for (int i = 0; i < params.length; i++) {
            if (params[i] == t)
                return i;
        }
        return -1;
    }

    public static final AnnotationsAttribute aggregate(ConstPool cp, Annotation... anno) {
        AnnotationsAttribute attr = new AnnotationsAttribute(cp,
                AnnotationsAttribute.visibleTag);
        for (Annotation annotation : anno) {
            if (annotation != null)
                attr.addAnnotation(annotation);
        }
        return attr;
    }


}
