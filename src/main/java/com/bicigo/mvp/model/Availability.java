package com.bicigo.mvp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="availabilities")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="availability_type", nullable = false)
    private boolean availabilityType;

    @Column(name="availability_start_date", nullable = false)
    private LocalDate availabilityStartDate;

    @Column(name="availability_end_date", nullable = false)
    private LocalDate availabilityEndDate;

    @ManyToOne
    @JoinColumn(name = "bicycle_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_BICYCLE_ID"))
    private Bicycle bicycle;
}
