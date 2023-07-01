package dbwls.springDataJPA.repository;

import dbwls.springDataJPA.dto.MemberDto;
import dbwls.springDataJPA.entity.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new dbwls.springDataJPA.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    // 일반 쿼리와 카운트 쿼리 분리 가능, 카운트 쿼리의 경우 join 안해도 전체 개수에는 영향을 주지 않으므로 조인 제외
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findTempByAge(int age, Pageable pageable);

    /**
     * @Modifying 어노테이션을 사용하지 않으면 스프링 데이터 JPA는 getResultList 등을 호출
     * 벌크 연산을 위해선 업데이트 쿼리를 날려야 하므로 해당 어노테이션을 사용해서 executeUpdate 호출
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadonlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
