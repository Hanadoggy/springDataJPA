package dbwls.springDataJPA.repository;

import dbwls.springDataJPA.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findMemberCustom();
}
