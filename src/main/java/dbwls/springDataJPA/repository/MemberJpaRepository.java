package dbwls.springDataJPA.repository;

import dbwls.springDataJPA.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Repository는 컴포넌트 스캔의 대상이 되도록 @Component를 포함
 * 각종 오류 발생 시 spring의 오류로 변환해서 던짐 <--!!
 */
@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }
}
