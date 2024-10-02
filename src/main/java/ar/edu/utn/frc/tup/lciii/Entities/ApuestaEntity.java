package ar.edu.utn.frc.tup.lciii.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "apuestas")
public class ApuestaEntity {

    //por lo q dice el profe, podemos guardar los datos como queramos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idApuesta; // se hace solo
    @Column
    private Integer idSorteo; // lo tendria q buscar con el client y asignarlo en base al sorteo encontrado por la fecha_sorteo
    @Column
    private LocalDate fechaSorteo; //DTO post
    @Column
    private String idCliente; //DTO post
    @Column
    private String numero; //DTO post
    @Column
    private String resultado; //asignar segun gano o no
    @Column
    private Double montoApostado; //DTO post
    @Column
    private Double premio; //asignar segun gano o no

}
