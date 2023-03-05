package by.psu.vs.mono.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Students")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
