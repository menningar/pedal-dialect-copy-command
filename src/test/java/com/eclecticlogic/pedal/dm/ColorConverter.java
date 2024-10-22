/**
 * Copyright (c) 2014-2015 Eclectic Logic LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.eclecticlogic.pedal.dm;

import jakarta.persistence.AttributeConverter;

/**
 * @author kabram.
 *
 */
public class ColorConverter implements AttributeConverter<Color, String> {

    @Override
    public String convertToDatabaseColumn(Color attribute) {
        return attribute == null || attribute == Color.BLACK ? null : attribute.getCode();
    }


    @Override
    public Color convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Color.forCode(dbData);
    }

}
