package com.codestates;

import com.codestates.entity.Member;
import com.codestates.entity.QMember;
import com.codestates.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.text.html.parser.Entity;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

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
    }

    @Test
    public void startJPQL() {
        //member1을 찾아라
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

    }

    @Test
    public void startQuerydsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where
                        (
                        member.username.eq("member1"),
                        member.age.eq(10)
                        )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void in(){
        QMember member = QMember.member;
        queryFactory
                .select(member)
                .from(member)
                .where(
                       member.age.goe(30),
                        member.age.gt(30),
                        member.age.loe(30),
                        member.age.lt(30)
                )
                .fetchOne();
    }

    @Test
    public void like(){
        QMember member = QMember.member;
        queryFactory
                .select(member)
                .from(member)
                .where(
                        member.username.like("member%"),
                        member.username.contains("member"),
                        member.username.startsWith("member%")
                )
                .fetchOne();
    }

    @Test
    public void resultFetchTest(){
        QMember member = QMember.member;
//        List<Member> members = queryFactory
//                .selectFrom(member)
//                .fetch();
//
//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne();
//
//        Member fetchFirst = queryFactory.selectFrom(member)
//                .fetchFirst();
          //페이징에서 사용
//        QueryResults<Member> results = queryFactory
//                .selectFrom(member)
//                .fetchResults();
//
//        //페이징 처리를 하기 위해 total 카운트를 가져옴 , 페이징용 쿼리를 가져올 수 있음
//        results.getTotal();
//        List<Member> content = results.getResults();

        //카운트용 쿼리 -> count
        long total = queryFactory
                .selectFrom(member)
                .fetch().size(); // fetchCount() 는 Deprecated가 되었다.
    }
}
