package ar.edu.utn.frc.tup.lciii.dtos.common;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApuestaDtoPost {

    private LocalDate fechaSorteo;
    private String idCliente;
    private String numero;
    private Double montoApostado;

    @Override
    public String toString() {
        return "{" +
                "\"fechaSorteo\":\"" + fechaSorteo + "\"," +
                "\"idCliente\":\"" + idCliente + "\"," +
                "\"numero\":\"" + numero + "\"," +
                "\"montoApostado\":" + montoApostado +
                "}";
    }
}
