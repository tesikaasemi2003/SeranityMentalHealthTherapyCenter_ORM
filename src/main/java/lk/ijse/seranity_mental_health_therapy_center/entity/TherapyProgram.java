package lk.ijse.seranity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "therapy_program")
public class TherapyProgram {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_weeks", nullable = false)
    private int durationWeeks;

    @Column(name = "fee", nullable = false)
    private double fee;

    // Owning side — FK = therapist_id
    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    @OneToMany(mappedBy = "therapyProgram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations;
}