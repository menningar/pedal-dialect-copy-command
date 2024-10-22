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

package com.eclecticlogic.pedal.dialect.postgresql;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by kabram on 4/6/17.
 */
public class CopyAttribute {

    private final List<Method> methods = new ArrayList<>();
    private final AtomicLong variableCounter = new AtomicLong();
    private final ThreadLocal<String> currentVariable = new ThreadLocal<>();
    private String columnName; // Name of db column.

    public List<Method> getMethods() {
        return methods;
    }

    public Method getEntityMethod() {
        return methods.get(0);
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getNewVariable() {
        currentVariable.set("v" + variableCounter.getAndIncrement());
        return getVariable();
    }

    public String getVariable() {
        return currentVariable.get();
    }

    public int getColumnLength() {
        return getEntityMethod().getAnnotation(Column.class).length();
    }

    public boolean isJpaConverter() {
        return getEntityMethod().isAnnotationPresent(Convert.class);
    }

    public Class<? extends AttributeConverter> getJpaConverterClass() {
        return getEntityMethod().getAnnotation(Convert.class).converter();
    }

    public boolean isCollection() {
        return Collection.class.isAssignableFrom(getEntityMethod().getReturnType());
    }

    public boolean isJoinColumn() {
        return getEntityMethod().isAnnotationPresent(JoinColumn.class);
    }

    public Method getJoinColumnIdMethod() {
        return Arrays.stream(getEntityMethod().getReturnType().getMethods())
                .filter(it -> it.isAnnotationPresent(Id.class)).findFirst().orElseThrow();
    }
}
