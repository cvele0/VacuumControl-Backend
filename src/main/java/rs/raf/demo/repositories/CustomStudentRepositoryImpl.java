//package rs.raf.demo.repositories;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.repository.support.JpaEntityInformation;
//import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
//import org.springframework.data.repository.NoRepositoryBean;
//import org.springframework.stereotype.Repository;
//import rs.raf.demo.model.Student;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//import java.util.ArrayList;
//import java.util.List;
//
////@Repository("studentRepository")
//public class CustomStudentRepositoryImpl extends SimpleJpaRepository<Student, Long> implements StudentRepository {
//
//    private final EntityManager entityManager;
//
//    @Autowired
//    public CustomStudentRepositoryImpl(EntityManager entityManager)
//    {
//        super(Student.class, entityManager);
//        this.entityManager = entityManager;
//    }
//
//    public CustomStudentRepositoryImpl(JpaEntityInformation<Student, ?> entityInformation, EntityManager entityManager) {
//        super(entityInformation, entityManager);
//        this.entityManager = entityManager;
//    }
//
//    public CustomStudentRepositoryImpl(Class<Student> domainClass, EntityManager em) {
//        super(domainClass, em);
//        this.entityManager = em;
//    }
//
//    public List<Student> searchStudents(String firstName, String lastName) {
//
////        return this.entityManager.createQuery("select s from Student s where s.firstName like :firstName and s.lastName like :lastName")
////                .setParameter("firstName", "%"+firstName+"%")
////                .setParameter("lastName", "%"+lastName+"%").getResultList();
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Student> query = criteriaBuilder.createQuery(Student.class);
//
//        Root<Student> root = query.from(Student.class);
//        List<Predicate> predicates = new ArrayList<>();
//
//        predicates.add(criteriaBuilder.like(root.get("firstName"), "%"+firstName+"%"));
//        predicates.add(criteriaBuilder.like(root.get("lastName"), "%"+lastName+"%"));
//
//        query.select(root).where(predicates.toArray(new Predicate[]{}));
//
//        return entityManager.createQuery(query).getResultList();
//    }
//}
