package com.codestates;

import com.codestates.entity.Member;
import com.codestates.entity.QMember;
import com.codestates.entity.QTeam;
import com.codestates.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
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
public class QuerydslSubQueryTest {

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

    //서브쿼리 : 쿼리 안에 쿼리

    /*
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery() {
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        //서브쿼리를 사용할 때 바깥에 있는 alias랑 서브쿼리에서 사용하는 alias랑 달라야함
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        for (Member resultMember : result) {
            System.out.println(resultMember.getUsername());
        }
    }

    /*
     * 나이가 평균 이상인 회원
     */

    @Test
    public void subQuery2() {
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        for (Member resultMember : result){
            System.out.println(resultMember.getUsername());
        }
    }

    /*
     * 중요한 In
     */

    @Test
    public void subQueryIn(){
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        for (Member resultMember : result){
            System.out.println(resultMember.getUsername());
        }
    }

    @Test
    public void selectSubQuery(){
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();
        for (Tuple tuple : result){
            System.out.println("tuple = " + tuple);
        }

    }
}
