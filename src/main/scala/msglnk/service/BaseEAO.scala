/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msglnk.service

import javax.ejb.Stateless
import javax.annotation.security.RolesAllowed
import javax.persistence.{NoResultException, Query, PersistenceContext, EntityManager}
import msglnk.data.BaseEntity
import collection.JavaConversions._

@Stateless(name = "msglnk-BaseEAO")
@RolesAllowed(Array("solution-admin"))
class BaseEAO {

    @PersistenceContext(unitName = "msglnkPU")
    var em: EntityManager = _

    def findAll[T](cls: Class[T]): Set[T] = {
        val queryStr = "SELECT e FROM %s e".format(cls.getName)
        val query = em.createQuery(queryStr)
        query.getResultList.asInstanceOf[java.util.List[T]].toSet
    }

    def findUniqueBy[T, E](cls: Class[T], name: String, value: E): Option[T] = {
        val queryStr = "SELECT e FROM %s e WHERE e.%s = :pValue".format(cls.getName, name)
        val query = em.createQuery(queryStr)
        query.setParameter("pValue", value)
        findUnique(cls, query)
    }

    def findById[T, E](cls: Class[T], value: E): Option[T] = value match {
        case e: E => em.find(cls, e) match {
            case t: T => Option(t)
            case null=> None
        }
        case null => None
    }

    def delete[T](cls: Class[T], id: Long) {
        findById(cls, id) match {
            case Some(bean) => em.remove(bean)
            case None => // ignore
        }
    }

    def findUnique[T](cls: Class[T], query: Query): Option[T] = {
        try {
            Some(cls.cast(query.getSingleResult))
        }
        catch {
            case nre: NoResultException => None
        }
    }

    def create[T <: BaseEntity](bean: T): T = {
        if (bean.getUid == null) {
            em.persist(bean)
            em.flush()
        } else {
            // We don't need to do anything.
            // Changes made to the entity will be persisted once the transaction is committed.
        }
        bean
    }

}
