package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.Entities.InfoApuestasEntity;
import ar.edu.utn.frc.tup.lciii.clients.SorteoResponse;
import ar.edu.utn.frc.tup.lciii.domain.Sorteo;
import ar.edu.utn.frc.tup.lciii.dtos.common.ApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.dtos.common.ResponseApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.services.LoteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/loteria")
public class LoteriaController {

    @Autowired
    private LoteriaService loteriaService;

    //url q consulta:
    // http://localhost:8080/loteria/apuesta
    @PostMapping("/apuesta")
    public ResponseEntity<ResponseApuestaDtoPost> postApuesta(
            @RequestBody ApuestaDtoPost apuestaDtoPost){

        //fecha hardcodeada, para simplificar el uso del endpoint momentaneamente (hay un sorteo en esa fecha)
        //apuestaDtoPost.setFechaSorteo(LocalDate.of(2024, 1, 16));

        ResponseApuestaDtoPost response = loteriaService.postApuesta(apuestaDtoPost);
        if (response == null){
            throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY,"No se pudo crear la Apuesta");
        }
        return ResponseEntity.ok(response);
    }

    //url q consulta:
    // http://localhost:8080/loteria/sorteo/?fecha=2024-01-16
    @GetMapping("/sorteo/")
    public ResponseEntity<List<SorteoResponse>> getSorteosByFecha(
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> fecha) {

        List<SorteoResponse> response = loteriaService.getSorteosByFecha(fecha.orElse(null));

        if (response.isEmpty()){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"No existe sorteo en la fecha " + fecha.get());
        }
        return ResponseEntity.ok(response);
    }

    //url q consulta:
    // http://localhost:8080/loteria/sorteo/123
    @GetMapping("/sorteo/{id_sorteo}")
    public ResponseEntity<Sorteo> getSorteoById(
            @PathVariable(name = "id_sorteo", required = true)
            int id_sorteo) {

       Sorteo response = loteriaService.getSorteoById(id_sorteo);

        if (response == null){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"No existe sorteo con id " + id_sorteo);
        }
        return ResponseEntity.ok(response);
    }

    //url q consulta:
    // http://localhost:8080/loteria/total/123
    @GetMapping("/total/{id_sorteo}")
    public ResponseEntity<InfoApuestasEntity> getInfoSorteoById(
            @PathVariable(name = "id_sorteo", required = true)
            int id_sorteo) {

        InfoApuestasEntity response = loteriaService.getInfoByIdSorteo(id_sorteo);

        if (response == null){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"No existe sorteo con id " + id_sorteo);
        }
        return ResponseEntity.ok(response);
    }

}
