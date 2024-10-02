package ar.edu.utn.frc.tup.lciii.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ApuestasDelSorteo {
    //ESTA CLASE SIRVE PARA EL ARRAY DE SORTEO ENTITY

    private String idCliente;
    private String numero;
    private String resultado;
    private Double montoApostado;
    private Double premio;

}
