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
public class QuerydslGatherTest {

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

    @Test
    public void aggregation(){
        QMember member = QMember.member;
        //Tuple : 여러개의 요소를 저장하는 자료구조
        List<Tuple> result = queryFactory
                .select(
                        member.count(), // 개수
                        member.age.sum(), // 합
                        member.age.avg(), // 평균
                        member.age.max(), // 최대
                        member.age.min() // 최소
                )
                .from(member)
                .fetch();
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    //mission : 팀의 이름과 각 팀의 평균 연령을 구해라
    @Test
    public void group(){
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team,team) // member의 team과 team을 조인
                .groupBy(team.name) //팀 이름으로 그룹핑
                .having() // groupBy와 함께 사용된다. 그룹화된 데이터에 조건을 적용하는 역할 ex) item들을 가져오면 item의 가격이 천원 이상인 것들
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);
        System.out.println(teamA);
        System.out.println(teamB);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }


}
