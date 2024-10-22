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
package com.eclecticlogic.pedal.provider;

import java.io.Serializable;

/**
 * Access to provider specific implementations of features that are not part of standard JPA.
 *
 * @author kabram.
 *
 */
public interface ProviderAccess {

    /**
     * @return Name of schema connected to. Empty string is the schema name is the default one.
     */
    String getSchemaName();


    /**
     * @param entityClass Entity class
     * @return Name of table.
     */
    String getTableName(Class<? extends Serializable> entityClass);

}
