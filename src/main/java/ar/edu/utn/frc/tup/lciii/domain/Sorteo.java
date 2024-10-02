package ar.edu.utn.frc.tup.lciii.domain;

import ar.edu.utn.frc.tup.lciii.Entities.ApuestasDelSorteo;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sorteo {
        private Integer idSorteo;
        private String fechaSorteo;
        private List<ApuestasDelSorteo> apuestas;
        private Double totalEnReserva;
}
//package ar.edu.utn.frc.tup.lciii.Entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "sorteos")
//public class SorteoEntity {
//
//    @Id
//    private Integer idSorteo;
//
//    @Column
//    private String fechaSorteo;
//
//    private Double totalDeApuestas;
//    private Double totalPagado;
//    private Double totalEnReserva;
//}