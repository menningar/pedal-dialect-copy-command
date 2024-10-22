/**
 * Copyright (c) 2014-2015 Eclectic Logic LLC
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
package com.eclecticlogic.pedal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.eclecticlogic.pedal.dialect.postgresql.CopyCommand;
import com.eclecticlogic.pedal.dialect.postgresql.CopyList;
import com.eclecticlogic.pedal.dm.Color;
import com.eclecticlogic.pedal.dm.EmbedOverride;
import com.eclecticlogic.pedal.dm.EmbedSimple;
import com.eclecticlogic.pedal.dm.ExoticTypes;
import com.eclecticlogic.pedal.dm.Planet;
import com.eclecticlogic.pedal.dm.PlanetId;
import com.eclecticlogic.pedal.dm.Status;
import com.eclecticlogic.pedal.dm.Student;
import com.eclecticlogic.pedal.dm.VehicleIdentifier;
import com.eclecticlogic.pedal.provider.ProviderAccess;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author kabram.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JpaConfiguration.class)
@Sql(scripts = "/schema.sql")
class TestPedalDialect extends SpringWithJNDIRunner5 {

    @Autowired
    private ProviderAccess providerAccess;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CopyCommand copyCommand;

    @Test
    void testSchemaName() {
        assertEquals("dialect", providerAccess.getSchemaName());
        assertEquals("dialect.exotic_types", providerAccess.getTableName(ExoticTypes.class));
        // The student class mapping has been modified via META-INF/pedal-test-orm.xml
        assertEquals("dialect.graduate_student", providerAccess.getTableName(Student.class));
    }

    @Test
    @Transactional
    void insertTestForCustomTypes() {

        Student student = new Student();
        student.setGpa(4.0f);
        student.setIdBase("custom_student");
        student.setMiddleName("joe");
        student.setName("schmoe");
        student.setZone("z'");
        student.setInsertedOn(new Date());
        entityManager.persist(student);

        ExoticTypes et = new ExoticTypes();
        et.setLogin("inserter");

        et.setStatus(Status.ACTIVE);
        et.setCustom("abc");
        et.setStudent(student);

        entityManager.persist(et);
        entityManager.flush();

        ExoticTypes loaded = entityManager.find(ExoticTypes.class, "inserter");
        assertNotNull(loaded);
        assertEquals("inserter", loaded.getLogin());
        assertEquals(Status.ACTIVE, loaded.getStatus());
    }

    @Test
    @Transactional
    void testInsertOfRenamedTable() {
        Student student = new Student();
        student.setIdBase("abc");
        student.setGpa(3.9f);
        student.setInsertedOn(new Date());
        student.setName("Joe Schmoe");
        student.setMiddleName("Que");
        student.setZone("d");

        entityManager.persist(student);

        Student s = entityManager.find(Student.class, "abc");
        assertEquals("abc", s.getIdBase());
        assertEquals(3.9f, s.getGpa(), 0.001);
        assertEquals("Joe Schmoe", s.getName());
    }

    @Test
    @Transactional
    void testCopyCommand() {
        CopyList<ExoticTypes> list = new CopyList<>();

        Student student = new Student();
        student.setGpa(4.0f);
        student.setIdBase("exotic_student");
        student.setMiddleName("joe1");
        student.setName("schmoe1");
        student.setZone("z");
        student.setInsertedOn(new Date());
        entityManager.persist(student);

        // The copy-command can insert 100k of these per second.
        for (int i = 0; i < 10; i++) {
            ExoticTypes et = new ExoticTypes();
            et.setLogin("copyCommand" + i);
            et.setStatus(Status.ACTIVE);
            et.setCustom("niks bijzonders");
            et.setColor(Color.BLACK); // Black is converted to null. This is to test and ensure null value is
            // conversion is properly handled.
            et.setTotal(i * 10);
            et.setStudent(student);
            list.add(et);
        }

        copyCommand.insert(entityManager, list);
        assertNotNull(entityManager.find(ExoticTypes.class, "copyCommand0"));
        assertEquals("niks bijzonders", entityManager.find(ExoticTypes.class, "copyCommand0").getCustom());
        assertNotNull(entityManager.find(ExoticTypes.class, "copyCommand1"));

        // Nullable converted value should be written as null.
        assertNull(entityManager.find(ExoticTypes.class, "copyCommand0").getColor());
    }

    @Test
    @Transactional
    void testBulkCopyCommand() {
        CopyList<ExoticTypes> list = new CopyList<>();

        Student student = new Student();
        student.setGpa(4.0f);
        student.setIdBase("exotic_student");
        student.setMiddleName("joe1");
        student.setName("schmoe1");
        student.setZone("z");
        student.setInsertedOn(new Date());
        entityManager.persist(student);

        int power = 0;
        double limit = Math.pow(2, 20);
        for (int i = 0; i <= limit; i++) {
            ExoticTypes et = new ExoticTypes();
            et.setLogin("copyCommand" + i);
            et.setStatus(Status.ACTIVE);
            et.setCustom("niks bijzonders");
            et.setColor(Color.BLACK); // Black is converted to null. This is to test and ensure null value is
            // conversion is properly handled.
            et.setTotal(i * 10);
            et.setStudent(student);
            list.add(et);

            if (list.size() % Math.pow(2, power) == 0) {
                copyCommand.insert(entityManager, list);
                list.clear();
                power++;
            }
        }
        assertNotNull(entityManager.find(ExoticTypes.class, "copyCommand0"));
    }

    @Test
    @Transactional
    void testAttributeOverrideWithCopyCommand() {
        CopyList<Student> list = new CopyList<>();
        {
            Student student = new Student();
            student.setGpa(4.0f);
            student.setIdBase("attrib");
            student.setMiddleName("joe1");
            student.setName("schmoe1");
            student.setZone("z");
            student.setInsertedOn(new Date());
            list.add(student);
        }
        copyCommand.insert(entityManager, list);
        assertNotNull(entityManager.find(Student.class, "attrib"));
    }

    @Test
    @Transactional
    void testCopyCommandWithEmbeddedId() {
        CopyList<Planet> list = new CopyList<>();
        {
            Planet p = new Planet();
            PlanetId id = new PlanetId();
            id.setName("jupiter");
            id.setPosition(6);
            p.setId(id);
            p.setDistance(100);
            list.add(p);
        }
        copyCommand.insert(entityManager, list);
        assertNotNull(entityManager.find(Planet.class, new PlanetId("jupiter", 6)));
    }

    @Test
    @Transactional
    void testCopyCommandEmbedSimple() {
        CopyList<EmbedSimple> list = new CopyList<>();
        EmbedSimple simple = new EmbedSimple();
        simple.setOwner("joe");
        VehicleIdentifier vi = new VehicleIdentifier();
        vi.setMake("Toyota");
        vi.setModel("corolla");
        vi.setYear(1990);
        simple.setIdentifier(vi);
        list.add(simple);
        copyCommand.insert(entityManager, list);
        assertNotNull(entityManager.find(EmbedSimple.class, "joe"));
    }

    @Test
    @Transactional
    void testCopyCommandEmbedOverride() {
        CopyList<EmbedOverride> list = new CopyList<>();
        EmbedOverride embed = new EmbedOverride();
        embed.setOwner("joe");
        VehicleIdentifier vi = new VehicleIdentifier();
        vi.setMake("Toyota");
        vi.setModel("corolla");
        vi.setYear(1990);
        embed.setIdentifier(vi);
        list.add(embed);
        copyCommand.insert(entityManager, list);
        assertNotNull(entityManager.find(EmbedOverride.class, "joe"));
    }
}
