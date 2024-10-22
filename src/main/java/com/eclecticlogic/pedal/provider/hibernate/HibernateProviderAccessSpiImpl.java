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
package com.eclecticlogic.pedal.provider.hibernate;

import com.eclecticlogic.pedal.provider.Consumer;
import com.eclecticlogic.pedal.provider.Function;
import com.eclecticlogic.pedal.provider.ProviderAccessSpi;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.sql.Connection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;

/**
 * Get provider specific information.
 * @author kabram.
 */
public class HibernateProviderAccessSpiImpl implements ProviderAccessSpi {

    private EntityManagerFactory emf;

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public String getSchemaName() {
        final String schema = (String) emf.unwrap(SessionFactory.class).getProperties().get("hibernate.default_schema");
        return schema == null ? "" : schema;
    }

    /**
     * @param entityClass Entity class for which the table name is required.
     * @return Table name if the entity class is a single table.
     */
    @Override
    public String getTableName(Class<? extends Serializable> entityClass) {
        SessionFactory sf = emf.unwrap(SessionFactory.class);
        AbstractEntityPersister persister = ((AbstractEntityPersister) ((MappingMetamodel) sf.getMetamodel()).getEntityDescriptor(entityClass));
        if (persister instanceof final SingleTableEntityPersister step) {
            return step.getTableName();
        } else {
            return null;
        }
    }

    @Override
    public void run(EntityManager entityManager, final Consumer<Connection> work) {
        Session session = entityManager.unwrap(Session.class);
        session.doWork(work::accept);
    }

    @Override
    public <R> R exec(EntityManager entityManager, final Function<Connection, R> work) {
        Session session = entityManager.unwrap(Session.class);
        return session.doReturningWork(work::apply);
    }
}
