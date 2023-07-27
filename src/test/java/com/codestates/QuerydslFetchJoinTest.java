package com.codestates;

import com.codestates.entity.Member;
import com.codestates.entity.QMember;
import com.codestates.entity.QTeam;
import com.codestates.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.graphql.rsocket.RSocketGraphQlClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class QuerydslFetchJoinTest {

//페치 조인 : SQL 조인을 활용해서 연관된 엔티티를 SQL 한번에 조회하는 기능. -> 성능 최적화에 사용하는 방법

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

    @PersistenceUnit
    EntityManagerFactory emf; // // entity 매니저를 만드는 팩토리

    @Test
    public void fetchJoinNo(){
        QMember member = QMember.member;
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam()); //이미 로딩된 엔티티인지 아닌지 확인시켜줌
        System.out.println(loaded);

    }

    @Test
    public void fetchJoin(){
        QMember member = QMember.member;
        QTeam team = QTeam.team;
        em.flush();
        em.clear();
        queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin() //멤버를 조회할 때 멤버 안의 팀 엔티티를 한번에 집어넣어주는게 fetchJoin 이다.
                .where(member.username.eq("member1"))
                .fetchOne();
    }

}
