package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// JpaRepository<[Entity], [Entity 식별자 데이터 타입]> 형태로 사용
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String name, int age);
    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query(
            "select m from Member m " +
            "where m.username = :username and m.age = :age"
    )
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query(
            "select m.username from Member m"
    )
    List<String> findUsernameList();

    @Query(
            "select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m " +
            "join m.team t"
    )
    List<MemberDto> findByMemberDto();

    @Query(
            "select m from Member m " +
            "where m.username in :names"
    )
    List<Member> findByNames(@Param("names") Collection<String> names);

    Member findMemberByUsername(String username);
    List<Member> findListByUsername(String username);
    Optional<Member> findMemberOptionalByUsername(String username);
    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    /**
     * ** 벌크성 수정 쿼리 **
     * - 벌크란? 데이터 update/delete 등 JPA에서 다량의 데이터에 대한 조작하여 처리하는 것을 의미한다.
     * - 주의점- 벌크성 수정쿼리를 사용하여 실행하게 되면 벌크성 쿼리 실행 결과가 Entity 영속성에는 반영되지 않는다.
     *        -> 해결 방법으로는 2가지로 확인된다.
     *            1. @Modifying(clearAutomatically = true) - @Modifying은 벌크성 쿼리에 필요하며, clearAutomatically가 Entity 영속성에도 반영되도록 영속성을 초기화 시킨다.
     *            2. 직접 [EntityManager].clear()을 실행한다.
     */
    @Modifying(clearAutomatically = true)
    @Query(
            "update Member m " +
            "set m.age = m.age + 1 " +
            "where m.age >= :age"
    )
    int bulkAgePlus(@Param("age") int age);

    @Query(
            "select m from Member m " +
            "left join fetch m.team"
    )
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query(
            "select m from Member m "
    )
    List<Member> findMemberEntityGraph();

    // @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
