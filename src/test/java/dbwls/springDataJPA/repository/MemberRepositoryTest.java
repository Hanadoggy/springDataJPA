package dbwls.springDataJPA.repository;

import dbwls.springDataJPA.dto.MemberDto;
import dbwls.springDataJPA.entity.Member;
import dbwls.springDataJPA.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;


    @Test
    void testMember() {
        Member member = new Member("yujin");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findUser("memberA", 10);
        assertThat(result.get(0)).isEqualTo(memberA);
    }

    @Test
    void findUsernameList() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void findMemberDto() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);
        Member memberA = new Member("memberA", 10);
        memberA.setTeam(teamA);
        memberRepository.save(memberA);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findByNames() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // springDataJPA는 list로 반환시 결과가 없으면 empty list 반환
        List<Member> memberList = memberRepository.findListByUsername("memberA");
        Member memberB2 = memberRepository.findMemberByUsername("memberB");
        Optional<Member> optionalA = memberRepository.findOptionalByUsername("memberA");
    }

    @Test
    void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> pages = memberRepository.findByAge(age, pageRequest);
        List<Member> content = pages.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(pages.getTotalElements()).isEqualTo(5);
        assertThat(pages.getNumber()).isEqualTo(0);
        assertThat(pages.getTotalPages()).isEqualTo(2);
        assertThat(pages.isFirst()).isTrue();
        assertThat(pages.hasNext()).isTrue();

        Slice<Member> slices = memberRepository.findSliceByAge(age, pageRequest);
        List<Member> contentSlice = slices.getContent();

        assertThat(contentSlice.size()).isEqualTo(3);
        assertThat(slices.getNumber()).isEqualTo(0);
        assertThat(slices.isFirst()).isTrue();
        assertThat(slices.hasNext()).isTrue();

        // entity가 저장된 page를 간단하게 dto로 변환
        Page<MemberDto> dtoPages = pages.map(m -> new MemberDto(m.getId(), m.getUsername()));
    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 12));
        memberRepository.save(new Member("member3", 13));
        memberRepository.save(new Member("member4", 14));
        memberRepository.save(new Member("member5", 15));

        int resultCount = memberRepository.bulkAgePlus(13);
        // 같은 트랜잭션이면 같은 엔티티 매니저 사용, @Modifying에서 자동으로 clear 호출 가능
//        em.clear();

        Member result = memberRepository.findMemberByUsername("member5");
        System.out.println("result = " + result);

        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 20, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        em.flush();
        em.clear();

//        List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.Team = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadonlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
    }

    @Test
    void lock() {
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // lock 얻고 조회
        List<Member> members = memberRepository.findLockByUsername("member1");
    }

    @Test
    void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();

    }
}