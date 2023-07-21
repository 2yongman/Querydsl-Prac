package com.codestates;

import com.codestates.entity.Member;
import com.codestates.entity.QMember;
import com.codestates.entity.QTeam;
import com.codestates.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class QuerydslJoinTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        queryFactory = new JPAQueryFactory(em);
    }

    /*
     *팀 A에 소속된 모든 회원
     */

    @Test
    public void join(){
        QMember member = QMember.member;
        QTeam team = QTeam.team;
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team,team)
                .where(team.name.eq("teamA"))
                .fetch();

        System.out.println(result);
        System.out.println(result.get(0));

        int i = 0;
        assertThat(i).isEqualTo(0);
    }

    /*
     * 세타조인 -> 연관관계가 없는 필드로 조인
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Member> result = queryFactory
                .select(member)
                .from(member,team)
                .where(member.username.eq(team.name))
                .fetch();
    }
}
