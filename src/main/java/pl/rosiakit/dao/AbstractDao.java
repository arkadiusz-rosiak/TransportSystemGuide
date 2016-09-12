package pl.rosiakit.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.rosiakit.hibernate.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * To not write all the same things in all DAOs
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
abstract class AbstractDao<PK extends Serializable, T> {
     
    private final Class<T> persistentClass;
     
    Transaction tx;
    private CriteriaBuilder builder;

    @SuppressWarnings("unchecked")
    AbstractDao(){
        this.persistentClass =(Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private final EntityManager entityManager = HibernateUtil.getEntityManager();
    
    EntityManager getEM(){
        return this.entityManager;
    }
    
    Session getSession(){
        return this.getEM().unwrap(Session.class);
    }

    T getByKey(PK key) {
        T t = null;
        
        try{
            tx = getSession().beginTransaction();
            t = getSession().get(persistentClass, key);
            tx.commit();
        } catch(HibernateException e) {
            tx.rollback();
        }
        
        return t;
    }
    
    void persist(T entity) {
        try{
            tx = getSession().beginTransaction();
            getSession().persist(entity);
            tx.commit();
        } catch(HibernateException e) {
            tx.rollback();
        }
    }
 
    public void delete(T entity) {        
        try {
            tx = getSession().beginTransaction();
            getSession().delete(entity);
            tx.commit();
        } catch(HibernateException e) {
            tx.rollback();
        }
    }

    List<T> findAll(){
        List<T> objects = null;        
        try {            
            TypedQuery<T> query = getEM().createQuery(
            "SELECT this FROM "+ persistentClass.getSimpleName() +" this", persistentClass);
            objects = query.getResultList();
        } catch(HibernateException e){
            tx.rollback();
        }
        return objects;
    }

    public void saveAll(LinkedList<T> objectsToSave){
        getSession().setJdbcBatchSize(200);
        try{
            tx = getSession().beginTransaction();

            ListIterator<T> it = objectsToSave.listIterator();
            int i = 0;
            while (it.hasNext()) {
                T obj = it.next();
                getSession().persist(obj);

                if (i++ % 200 == 0 ) {
                    getSession().flush();
                    getSession().clear();
                }
            }

            tx.commit();
        }
        catch(HibernateException e){
            tx.rollback();
        }
        finally{
            getSession().setJdbcBatchSize(1);
        }
    }

    /**
     * Funkcja rozpoczyna nowa transakcje i zwraca criteriaQuery
     * @return criteriaQuery
     */
    CriteriaQuery<T> createCriteriaQuery(){
        tx = getSession().beginTransaction();               
        builder = this.getEM().getCriteriaBuilder();        
        return builder.createQuery(persistentClass);
    }
}