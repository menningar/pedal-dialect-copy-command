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

import java.io.Serializable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author kabram.
 *
 */
@SuppressWarnings("serial")
@Entity(name = "embed_override")
public class EmbedOverride implements Serializable {

    private VehicleIdentifier identifier;
    private String owner;
    private String state;

    @AttributeOverrides({ @AttributeOverride(name = "make", column = @Column(name = "my_make", nullable = false)),
            @AttributeOverride(name = "model", column = @Column(name = "my_model")),
            @AttributeOverride(name = "year", column = @Column(name = "my_year", nullable = false)) })
    public VehicleIdentifier getIdentifier() {
        return identifier;
    }


    public void setIdentifier(VehicleIdentifier identifier) {
        this.identifier = identifier;
    }


    @Id
    @Column(name = "owner", nullable = false, unique = true)
    public String getOwner() {
        return owner;
    }


    public void setOwner(String owner) {
        this.owner = owner;
    }


    @Column(name = "state")
    public String getState() {
        return state;
    }


    public void setState(String state) {
        this.state = state;
    }

}
