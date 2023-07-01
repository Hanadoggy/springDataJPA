package dbwls.springDataJPA.controller;

import dbwls.springDataJPA.entity.Member;
import dbwls.springDataJPA.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 조회용으로만 사용해야함, 사용이 제한적
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
