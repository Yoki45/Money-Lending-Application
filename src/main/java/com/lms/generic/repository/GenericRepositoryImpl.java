package com.lms.generic.repository;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;


@Transactional
@Slf4j
public abstract class GenericRepositoryImpl<T, PK extends Serializable> implements GenericRepository<T, PK> {

    private final Class<T> persistentClass;

    @PersistenceContext
    protected EntityManager em;

    @Override
    public void setEm(EntityManager em) {
        log.info("EM+++++++++++++++++++++++++++++ " + em);
        this.em = em;
    }

    @Override
    public EntityManager getEm() {
        final Session session = this.em.unwrap(Session.class);
//        session.enableFilter("filterByDeleted");
        return this.em;
    }

    @SuppressWarnings("unchecked")
    public GenericRepositoryImpl() {
        Type genericSuperClass = getClass().getGenericSuperclass();

        ParameterizedType parametrizedType = null;
        while (parametrizedType == null) {
            if ((genericSuperClass instanceof ParameterizedType)) {
                parametrizedType = (ParameterizedType) genericSuperClass;
            } else {
                genericSuperClass = ((Class<?>) genericSuperClass).getGenericSuperclass();
            }
        }
        this.persistentClass = (Class<T>) parametrizedType.getActualTypeArguments()[0];
    }

    public GenericRepositoryImpl(final Class<T> persistentClass) {
        super();
        this.persistentClass = persistentClass;
    }

    /**
     *
     */
    @Override
    public Class<T> getEntityClass() {
        return persistentClass;
    }

    /**
     * #save(java.lang.Object)
     */
    @Override
    @Transactional
    public T save(T entity)  {
        return em.merge(entity);
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
     */
    @Transactional
    @Override
    public <S extends T> S saveEntity(S entity) {

        Assert.notNull(entity, "Entity must not be null.");

        /*if (entityInformation.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }*/
        return em.merge(entity);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaRepository#save(java.lang.Iterable)
     */
    @Transactional
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) throws Exception {

        Assert.notNull(entities, "Entities must not be null!");

        List<S> result = new ArrayList<S>();

        for (S entity : entities) {
            result.add(saveEntity(entity));
        }

        return result;
    }

    @Transactional
    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) throws Exception {

        List<S> result = saveAll(entities);
        flush();

        return result;
    }

    @Transactional
    @Override
    public void flush() {
        em.flush();
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Override
    @Transactional
    public void delete(T entity) {
        em.remove(entity);
    }

    /**
     *
     */
    @Override
    @Transactional
    public void deleteById(final PK id) {
        T entity = em.find(persistentClass, id);
        if (entity != null) em.remove(entity);
    }

    @Override
    @Transactional
    public void delete(PK[] ids) {
        int size = ids.length;

        for (int idx = 0; idx < size; idx++) {
            T entity = em.find(persistentClass, ids[idx]);
            if (entity != null) em.remove(entity);
        }

    }

    @Override
    @Transactional
    public void deleteAll(List<T> entities) {

        Assert.notNull(entities, "Entities must not be null!");

        for (T entity : entities) {
            em.remove(entity);
        }

    }


    /**
     *
     */
    @Override
    @Transactional
    public T findById(final PK id) {
        final T result = getEm().find(persistentClass, id);
        return result;
    }

    @Override
    @Transactional
    public T findById(final PK id, String graphName) {
        EntityGraph graph = getEm().getEntityGraph(graphName);
        Map<String, Object> hints = new HashMap();
        hints.put("jakarta.persistence.fetchgraph", graph);
        return getEm().find(persistentClass, id, hints);
    }

    @Override
    @Transactional
    public T getReference(final PK id) {
        return em.getReference(persistentClass, id);
    }


    @Override
    @Transactional
    public List<T> findAllByIdIn(Class<T> entityClass, List<Long> ids) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        criteriaQuery.where(root.get("id").in(ids));
        return em.createQuery(criteriaQuery).getResultList();
    }




    /**
     *
     */
    @Override
    @Transactional
    public int countAll() {
        return countByCriteria();
    }

    /**
     *
     */
    @Override
    @Transactional
    public int countByExample(final T exampleInstance) {
        CriteriaBuilder qb = getEm().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(getEntityClass())));
        return em.createQuery(cq).getSingleResult().intValue();
    }

    /**
     *
     */
    @Override
    @Transactional
    public List<T> findByExample(final T exampleInstance) {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(getEntityClass());
        return em.createQuery(query).getResultList();

    }

    /**
     * #findByNamedQuery(java.lang.String, java.lang.Object[])
     */
    @Override
    @Transactional
    public List<T> findByNamedQuery(final String name, Object... params) {
        Query query = getEm().createNamedQuery(name);

        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }

        final List<T> result = query.getResultList();
        return result;
    }


    /**
     * #findByNamedQuery(java.lang.String, java.lang.Object[])
     */
    @Override
    @Transactional
    public List<T> findByNamedQuery(final String name, String graphName, Object... params) {
        Query query = getEm().createNamedQuery(name);
        query.setHint("jakarta.persistence.fetchgraph", em.getEntityGraph(graphName));
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }

        final List<T> result = query.getResultList();
        return result;
    }

    /**
     *
     */
    @Override
    @Transactional
    public List<T> findByNamedQuery(final String name,
                                    final Map<String, ? extends Object> params) {
        Query query = getEm().createNamedQuery(name);
        for (final Entry<String, ? extends Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }

        return query.getResultList();
    }


    /**
     * #findByNamedQuery(java.lang.String, java.lang.Object[])
     */
    @Override
    @Transactional
    public List<T> findByNamedQuery(final String name, String graphName, final Map<String, ? extends Object> params) {
        Query query = getEm().createNamedQuery(name);
        query.setHint("jakarta.persistence.fetchgraph", em.getEntityGraph(graphName));

        for (final Entry<String, ? extends Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        final List<T> result = query.getResultList();
        return result;
    }


    /**
     * @param criterion
     * @return
     */
    @Override
    @Transactional
    public int countByCriteria(List<String>... criterion) {
        CriteriaBuilder qb = getEm().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<T> root = cq.from(getEntityClass());
        cq.select(qb.count(root));

        Predicate[] predicates = null;
        if (criterion != null && criterion.length > 0 && criterion[0] != null) {
            predicates = extractPredicates(criterion[0], qb, root);
            cq.where(predicates);
        }
        return em.createQuery(cq).getSingleResult().intValue();
    }



    private static class Tentity {
    }




    private List<Order> getOrders(List<String> orderBy, CriteriaBuilder cb, Root<T> root, Boolean excludeDefaultOrder) {
        if (!excludeDefaultOrder) orderBy.add("createdAt");//add this to preserve the insertion order
        List<Order> od = new ArrayList<>(orderBy.size());
        for (String orderDesc : orderBy) {
            log.info("got order------------------------" + orderDesc);
            if (orderDesc.contains(":")) {
                String[] rels = orderDesc.split(":");
                //root.get(rels[0]).get(rels[1]);
                od.add(rels[0].startsWith("-") ? cb.desc(root.get(rels[0].substring(1)).get(rels[1])) :
                        cb.asc(root.get(rels[0]).get(rels[1])));
            } else {
                od.add(orderDesc.startsWith("-") ? cb.desc(root.get(orderDesc.substring(1))) : cb.asc(root.get(orderDesc)));
            }
        }
        return od;
    }


    protected Predicate[] extractPredicates(List<String> whereList, CriteriaBuilder criteriaBuilder, Root<T> root) {

        //List<String> whereList = queryParameters.get("where");
        if (whereList == null || whereList.isEmpty()) {
            log.info(" no where clause ...");
            return new Predicate[]{};
        }

        log.info(" processing where list ' " + whereList + "'");
        List<Predicate> predicates = new ArrayList<>();
        //?[where=<field>,<comparator>,<value>]*
        //?[where=<field>,<comparator>,<value>,|,<field>,<comparator>,<value>]
        for (String whereParam : whereList) {
            log.info(" the current where clause " + whereParam);
            String[] p = whereParam.split(",");
            log.info(" the fields ' " + Arrays.asList(p) + "' , size of p " + p.length);
            if (p.length % 3 != 0) {
                //throw new CustomWebApplicationException("Invalid number of params for where request, ' " + whereList + "'", HttpStatus.BAD_REQUEST);
            }
            String fieldPath = p[0];
            if (p.length == 3) {
                convertClausePredicate(criteriaBuilder, root, predicates, whereParam, p, fieldPath);
            } else {
                //[where=<field>,<comparator>,<value>,|,<field>,<comparator>,<value>]

                String[] orClauses = whereParam.split("\\|");
                log.info(orClauses.length + "==we have or clause ; " + whereParam + " clauses are " + orClauses[0] + " " + orClauses[1]);


                List<List<Predicate>> predObjList = new ArrayList<>();
                for (String orWhere : orClauses) {
                    List<Predicate> leftPredicates = new ArrayList<>();

                    String[] leftWheres = orWhere.split(";");
                    for (String lw : leftWheres) {
                        log.info(" LOAIDNG==== " + lw);
                        String[] p1 = lw.split(",");
                        convertClausePredicate(criteriaBuilder, root, leftPredicates, lw, p1, p1[0]);
                    }
                    predObjList.add(leftPredicates);

                }


                int size = predObjList.size();
                log.info(" SIZE PARAMS==== " + size);


                if (size == 2) {
                    predicates.add(criteriaBuilder.or(criteriaBuilder.and(predObjList.get(0).toArray(new Predicate[0])),
                            criteriaBuilder.and(predObjList.get(1).toArray(new Predicate[0]))));
                } else if (size > 2) {
                    log.info(" ELSE==== " + size);
                    List<Predicate> leftPredicates = new ArrayList<>();
                    for (int i = 0; i < size; ) {
                        log.info(" LOOP==== " + i);
                        if (i + 2 <= size) {
                            Predicate pl = criteriaBuilder.or(criteriaBuilder.and(predObjList.get(i).toArray(new Predicate[0])),
                                    criteriaBuilder.and(predObjList.get(i + 1).toArray(new Predicate[0])));
                            leftPredicates.add(pl);
                            i += 2;
                        } else if (i + 1 == size) {
                            leftPredicates.add(criteriaBuilder.and(predObjList.get(size - 1).toArray(new Predicate[0])));
                            i += 2;
                        }
                    }

                    while (leftPredicates.size() > 2) {
                        leftPredicates = reducePredicates(criteriaBuilder, leftPredicates);
                        log.info(" NEW PRED==== " + leftPredicates.size());
                    }

                    if (leftPredicates.size() == 2) {
                        log.info(" NEW PRED END NOW = === " + leftPredicates.size());
                        predicates.add(criteriaBuilder.or(criteriaBuilder.and(leftPredicates.get(0)),
                                criteriaBuilder.and(leftPredicates.get(1))));
                    }
                }

            }
        }
        log.info(" collected predicates " + Arrays.toString(predicates.toArray(new Predicate[0])));
        return predicates.toArray(new Predicate[0]);
    }


    private List<Predicate> reducePredicates(CriteriaBuilder criteriaBuilder, List<Predicate> myPredicates) {
        int size = myPredicates.size();
        List<Predicate> predicates = new ArrayList<>();
        for (int i = 0; i < size; ) {
            if (i + 2 <= size) {
                Predicate pl = criteriaBuilder.or(criteriaBuilder.and(myPredicates.get(i)),
                        criteriaBuilder.and(myPredicates.get(i + 1)));
                predicates.add(pl);
                i += 2;
            } else if (i + 1 == size) {
                predicates.add(criteriaBuilder.and(myPredicates.get(size - 1)));
                i += 2;
            }
        }
        return predicates;
    }


    private void convertClausePredicate(CriteriaBuilder criteriaBuilder, Root<T> root, List<Predicate> predicates, String whereParam, String[] p, String fieldPath) {

    }

    public static Path<?> getPath(Path<?> path, String propertyPath) {
        return null;

    }

    protected List<Selection<?>> getSelections(String[] fields, Root<T> root) {
        log.info(" the fields in selection " + Arrays.asList(fields) + " number of fields " + fields.length);
        List<Selection<?>> selections = new ArrayList<>(fields.length);
        Map<String, Join<Object, Object>> existingJoins = new HashMap<>();
        for (String field : fields) {
            log.info("field name " + field);
            if (field.contains(":")) {
                //joins
                //wfTask:owner<firstName;lastName>
                String[] rels = field.split(":");
                Join<Object, Object> currentJoin = existingJoins.containsKey(rels[0]) ? existingJoins.get(rels[0]) : root.join(rels[0], JoinType.LEFT);
                existingJoins.put(rels[0], currentJoin);
                String relationRest = rels[1].trim();
                if (relationRest.contains("<")) {
                    //owner<firstName;lastName>
                    final int startIndex = relationRest.indexOf("<");
                    final int endIndex = relationRest.indexOf(">");
                    String[] attributes = relationRest.substring(startIndex + 1, endIndex).split(";");
                    String relationName = relationRest.substring(0, startIndex);
                    log.info(" relation name " + relationName + " attributes " + Arrays.asList(attributes));
                    Path<Object> p = currentJoin.join(relationName, JoinType.LEFT);
                    //selections.add(p);
                    if (attributes.length == 0) {
                        String[] a2 = {"id"}; //re-initialize the array just with the id
                        attributes = a2;
                    }
                    for (String attributeName : attributes) {
                        selections.add(p.get(attributeName));
                    }
                } else {
                    Path<Object> p = currentJoin.get(relationRest);
                    selections.add(p);
                }
            } else {
                selections.add(root.get(field));
            }
        }
        return selections;
    }

    protected String[] makeDefaultSelectionFields(Class<?> entityType) {
        String[] fieldNames = new String[0];
        return fieldNames;
    }

    public Session getSession() {
        return (Session) em.getDelegate();
    }





}
