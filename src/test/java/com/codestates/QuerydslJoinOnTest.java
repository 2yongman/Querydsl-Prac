package com.codestates;

import com.codestates.entity.Member;
import com.codestates.entity.QMember;
import com.codestates.entity.QTeam;
import com.codestates.entity.Team;
import com.querydsl.core.Tuple;
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
public class QuerydslJoinOnTest {

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
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * left, right 조인은 외부조인이 정말 필요할때만 쓰고, 왠만하면 inner join을 쓰되 where을 병행하자.
    */
    @Test
    public void join_on_filtering(){
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team,team)
                .on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : result){
            System.out.println("tuple = " + tuple );
        }
    }

    /*
        연관관계가 없는 엔티티 외부조인
        회원의 이름이 팀 이름과 같은 대상 외부 조인
     */
    @Test
    public void join_on_no_relation(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        QMember member = QMember.member;
        QTeam team = QTeam.team;
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();
        for (Tuple tuple : result){
            System.out.println("tuple : " + tuple);
        }
    }
}