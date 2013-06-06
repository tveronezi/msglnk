package msglnk.service

import javax.ejb.Stateless
import javax.annotation.security.RolesAllowed
import javax.persistence.{NoResultException, Query, PersistenceContext, EntityManager}
import msglnk.data.BaseEntity
import collection.JavaConversions._

@Stateless
@RolesAllowed(Array("solution-admin"))
class BaseEAO {

    @PersistenceContext(unitName = "mailPU")
    var em: EntityManager = _

    def findAll[T](cls: Class[T]): Set[T] = {
        val queryStr = "SELECT e FROM %s e".format(cls.getName)
        val query = em.createQuery(queryStr)
        val list = query.getResultList.asInstanceOf[java.util.List[T]]
        list.toSet
    }

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
