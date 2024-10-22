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

import java.util.Date;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * @author kabram.
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "student")
@AttributeOverrides(value = {@AttributeOverride(name = "idBase", column = @Column(name = "id", unique = true, nullable = false, length = 36))})
public class Student extends BaseStudent {

    private String name;
    private String zone;
    private float gpa;
    private Date insertedOn;


    @Transient
    public void setMiddleName(String value) {
        // This is to ensure that the copy command doesn't fail if there are setters without getters.
    }


    @Column(name = "name", length = 25, nullable = false)
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Column(name = "gpa", nullable = false)
    public float getGpa() {
        return gpa;
    }


    public void setGpa(float gpa) {
        this.gpa = gpa;
    }


    @Column(name = "zone", length = 5)
    public String getZone() {
        return zone;
    }


    public void setZone(String zone) {
        this.zone = zone;
    }


    @Column(name = "inserted_on", nullable = false)
    public Date getInsertedOn() {
        return insertedOn;
    }


    public void setInsertedOn(Date insertedOn) {
        this.insertedOn = insertedOn;
    }

}
