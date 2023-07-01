package dbwls.springDataJPA.entity;

import dbwls.springDataJPA.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamA");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        Member member3 = new Member("member3", 10, teamB);
        Member member4 = new Member("member4", 10, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush();
        em.clear();

        // 확인
        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    void JpaEventBaseEntity() throws Exception {
        Member member1 = new Member("member1");
        memberRepository.save(member1);

        Thread.sleep(100);
        member1.setUsername("member2");

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member1.getId()).get();
        System.out.println("getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("getLastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("getLastModifiedBy = " + findMember.getLastModifiedBy());
    }
}