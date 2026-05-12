package lk.ijse.seranity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "therapy_session")
public class TherapySession {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "status")
    private String status; // "SCHEDULED", "COMPLETED", "CANCELLED"

    // Owning side — FK = patient_id
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Owning side — FK = therapist_id
    @ManyToOne
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;
}