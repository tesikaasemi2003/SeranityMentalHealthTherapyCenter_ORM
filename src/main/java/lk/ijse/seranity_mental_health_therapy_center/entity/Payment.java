package lk.ijse.seranity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "status")
    private String status; // "PAID", "PENDING"

    // Owning side — FK = registration_id
    @ManyToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;
}