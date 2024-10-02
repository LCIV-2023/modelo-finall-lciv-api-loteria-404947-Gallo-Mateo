package ar.edu.utn.frc.tup.lciii.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "info_apuestas")
public class InfoApuestasEntity {

    @Id
    private Integer idSorteo;
    @Column
    private String fechaSorteo;
    @Column
    private Double totalDeApuestas;
    @Column
    private Double totalPagado;
    @Column
    private Double totalEnReserva;

}

//package ar.edu.utn.frc.tup.lciii.domain;
//
//import lombok.*;
//
//import java.util.Date;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class InfoApuestas {
//
//    private Integer idSorteo;
//    private String fechaSorteo;
//    private Double totalDeApuestas;
//    private Double totalPagado;
//    private Double totalEnReserva;
//}
