package com.lms.generic.repository;


import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GenericRepository<T, PK extends Serializable> {

    void setEm(EntityManager em);

    EntityManager getEm();

    /**
     * Get the Class of the entity.
     *
     * @return the class
     */
    Class<T> getEntityClass();

    /**
     * Find an entity by its primary key
     *
     * @param id the primary key
     * @return the entity
     */
    T findById(final PK id);


    T findById(final PK id, String graphName);

    /**
     * Get reference of
     * the entity whose state may be lazily fetched.
     *
     * @param id
     * @return
     */
    T getReference(final PK id);

    /**
     * Load all entities.
     *
     * @return the list of entities
     */
    List<T> findAll();

    List<T> findAll(String fieldList);

    List<T> findAllByIdIn(Class<T> entityClass, List<Long> ids);

    List<T> findAll(String fieldList, final List<String> criterion);

    /**
     * Find entities based on an example.
     *
     * @param exampleInstance the example
     * @return the list of entities
     */
    List<T> findByExample(final T exampleInstance);

    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params    the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQuery(final String queryName, Object... params);

    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params    the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQuery(final String queryName, String graphName, Object... params);

    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params    the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQuery(final String queryName, final Map<String, ? extends Object> params);

    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params    the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQuery(final String queryName, final String graphName, final Map<String, ? extends Object> params);


    /**
     * Count all entities.
     *
     * @return the number of entities
     */
    int countAll();

    /**
     * Count entities based on an example.
     *
     * @param exampleInstance the search criteria
     * @return the number of entities
     */
    int countByExample(final T exampleInstance);


    /**
     * save an entity. This can be either a INSERT or UPDATE in the database.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(final T entity);

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    <S extends T> S saveEntity(S entity);


    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    <S extends T> List<S> saveAll(Iterable<S> entities) throws Exception;

    /**
     * Saves all entities and flushes changes instantly.
     *
     * @param entities entities to be saved. Must not be {@literal null}.
     * @return the saved entities
     * @since 2.5
     */
    <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) throws Exception;

    /**
     * Flushes all pending changes to the database.
     */
    void flush();

    /**
     * Flush and clear memory
     */
    void flushAndClear();

    /**
     * delete an entity from the database.
     *
     * @param entity the entity to delete
     */
    void delete(final T entity) throws Exception;

    /**
     * delete an entity by its primary key
     *
     * @param id the primary key of the entity to delete
     */
    void deleteById(final PK id) throws Exception;

    /**
     * delete batch entities by their primary keys array
     *
     * @param ids [] the primary key of entities to delete
     */
    void delete(PK[] ids) throws Exception;


    void deleteAll(List<T> entities);

    int countByCriteria(List<String>... criterion);

}
