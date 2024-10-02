package ar.edu.utn.frc.tup.lciii.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sorteos")
public class SorteoEntity {

    @Id
    private Integer idSorteo;

    @Column
    private String fechaSorteo;

    private Double totalDeApuestas;
    private Double totalPagado;
    private Double totalEnReserva;
}
