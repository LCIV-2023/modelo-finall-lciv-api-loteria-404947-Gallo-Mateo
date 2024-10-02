package ar.edu.utn.frc.tup.lciii.clients;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SorteoResponse {

    @Id
    private Integer numeroSorteo;

    @Column
    private String fecha;

    @Column
    private Double dineroTotalAcumulado;

    @Column
    private List<List<Integer>> numerosSorteados;
}
