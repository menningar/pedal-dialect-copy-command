/*
 * Copyright (c) 2017 Eclectic Logic LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eclecticlogic.pedal.dialect.postgresql.eval;

import com.eclecticlogic.pedal.dialect.postgresql.CopyAttribute;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by kabram on 4/6/17.
 */
public class EmbeddedColumnEvaluator extends AbstractMethodEvaluator {

    @Override
    public void evaluate(Method method, EvaluatorChain chain) {
        if (method.getReturnType().isAnnotationPresent(Embeddable.class) &&
                !method.isAnnotationPresent(Transient.class)) {
            Map<String, AttributeOverride> overrides = getAttributeOverrides(method);

            Class<?> embeddedClz = method.getReturnType();
            for (Method embeddedMethod : Arrays.stream(embeddedClz.getMethods()).filter(it -> it.isAnnotationPresent(Column.class)).toList()) {
                String name = getPropertyName(embeddedMethod);
                String columnName;
                if (overrides.containsKey(name)) {
                    columnName = overrides.get(name).column().name();
                } else {
                    columnName = embeddedMethod.getAnnotation(Column.class).name();
                }
                CopyAttribute attribute = new CopyAttribute();
                attribute.getMethods().add(method);
                attribute.getMethods().add(embeddedMethod);
                attribute.setColumnName(columnName);
                chain.add(attribute);
            }
        } else {
            chain.doNext();
        }
    }
}
