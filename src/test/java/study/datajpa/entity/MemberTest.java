package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("Member1", 10, teamA);
        Member member2 = new Member("Member2", 20, teamA);
        Member member3 = new Member("Member3", 30, teamB);
        Member member4 = new Member("Member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 데이터 반영 및 초기화
        em.flush();
        em.clear();

        // 데이터 확인
        List<Member> members = em.createQuery(
                "select m from Member m"
                , Member.class
        ).getResultList();

        members.stream().forEach(n -> {
            System.out.println("Member = " + n);
            System.out.println("--> Member.team" + n.getTeam());
        });
    }

    @Test
    public void jpaEvnetBaseEntity() throws Exception {
        // given
        Member member = new Member("member1");
        memberRepository.save(member);   // @PrePersist

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("findMember.getUpdatedDate = " + findMember.getUpdatedDate());
    }
}