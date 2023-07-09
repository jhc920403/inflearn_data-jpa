package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
)
@ToString(of = {"id", "username", "age"})       // 주의점 ! - 연관관계 맵핑된 컬렉션(필드)은 무한 참조루프되기 때문에 넣지 않을 것
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends JpaBaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        this.team = team;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
