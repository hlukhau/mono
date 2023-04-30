package by.psu.vs.mono.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long groupId;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private List<Student> students;
}
