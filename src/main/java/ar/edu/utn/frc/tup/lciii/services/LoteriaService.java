package ar.edu.utn.frc.tup.lciii.services;

import ar.edu.utn.frc.tup.lciii.Entities.InfoApuestasEntity;
import ar.edu.utn.frc.tup.lciii.clients.SorteoResponse;
//import ar.edu.utn.frc.tup.lciii.domain.InfoApuestas;
import ar.edu.utn.frc.tup.lciii.domain.Sorteo;
import ar.edu.utn.frc.tup.lciii.dtos.common.ApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.dtos.common.ResponseApuestaDtoPost;

import java.time.LocalDate;
import java.util.List;

public interface LoteriaService {

    ResponseApuestaDtoPost postApuesta(ApuestaDtoPost apuestaDtoPost);

    Sorteo getSorteoById(Integer id_sorteo);

    InfoApuestasEntity getInfoByIdSorteo(Integer id_sorteo);

    List<SorteoResponse> getSorteosByFecha(LocalDate fecha);
}
