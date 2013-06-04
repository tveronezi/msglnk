package msglnk.service

import javax.ejb.Stateless
import javax.annotation.security.RolesAllowed
import javax.persistence.{NoResultException, Query, PersistenceContext, EntityManager}

@Stateless
@RolesAllowed(Array("solution-admin"))
class BaseEAO {
    @PersistenceContext(unitName = "mailPU")
    var em: EntityManager = _

    def findUniqueBy[T, E](cls: Class[T], name: String, value: E): Option[T] = {
        val queryStr = "SELECT e FROM %s e WHERE e.%s = :pValue".format(cls.getName, name)
        val query = em.createQuery(queryStr)
        query.setParameter("pValue", value)
        findUnique(cls, query)
    }

    def findById[T, E](cls: Class[T], value: E): Option[T] = {
        val obj = em.find(cls, value)
        if (obj == null) {
            None
        } else {
            Some(cls.cast(obj))
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

    def create[T](bean: T): T = {
        em.persist(bean)
        bean
    }

}
