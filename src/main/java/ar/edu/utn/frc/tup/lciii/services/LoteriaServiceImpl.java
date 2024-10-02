package ar.edu.utn.frc.tup.lciii.services;

import ar.edu.utn.frc.tup.lciii.Entities.ApuestaEntity;
import ar.edu.utn.frc.tup.lciii.Entities.ApuestasDelSorteo;
import ar.edu.utn.frc.tup.lciii.Entities.InfoApuestasEntity;
import ar.edu.utn.frc.tup.lciii.Entities.SorteoEntity;
import ar.edu.utn.frc.tup.lciii.clients.SorteoClient;
import ar.edu.utn.frc.tup.lciii.clients.SorteoResponse;
//import ar.edu.utn.frc.tup.lciii.domain.InfoApuestas;
import ar.edu.utn.frc.tup.lciii.domain.Sorteo;
import ar.edu.utn.frc.tup.lciii.dtos.common.ApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.dtos.common.ResponseApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.repositories.ApuestaRepository;
import ar.edu.utn.frc.tup.lciii.repositories.InfoApuestasRepository;
import ar.edu.utn.frc.tup.lciii.repositories.SorteoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LoteriaServiceImpl implements LoteriaService{

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ApuestaRepository apuestaRepository;
    @Autowired
    private InfoApuestasRepository infoApuestasRepository;
    @Autowired
    private SorteoRepository sorteoRepository;
    @Autowired
    private SorteoClient sorteoClient;

    @Override
    public ResponseApuestaDtoPost postApuesta(ApuestaDtoPost apuestaDtoPost) {
        ResponseApuestaDtoPost response = null;

        //tiene los re problemas modelMapper, quiere pasar el atributo idCliente(String) a Integer sin ninguna razon.
        //en ApuestaEntity y en ApuestaDtoPost idCliente es String
        //ApuestaEntity apuestaEntity = modelMapper.map(apuestaDtoPost, ApuestaEntity.class);

        ApuestaEntity apuestaEntity = new ApuestaEntity();
        apuestaEntity.setFechaSorteo(apuestaDtoPost.getFechaSorteo());
        apuestaEntity.setIdCliente(apuestaDtoPost.getIdCliente());
        apuestaEntity.setNumero(apuestaDtoPost.getNumero());
        apuestaEntity.setMontoApostado(apuestaDtoPost.getMontoApostado());

        //busca el id_sorteo en base a la fecha
        List<SorteoResponse> sorteoByFecha = sorteoClient.getSorteosByFecha(apuestaDtoPost.getFechaSorteo());

        //sino existe, se devuelve null
        if (!sorteoByFecha.isEmpty()){

            //Monto máximo de apuesta (5 pts)
            //Las apuestas no podrán ser mayores al 1% del total en reserva del día del sorteo
            if (apuestaDtoPost.getMontoApostado() < sorteoByFecha.get(0).getDineroTotalAcumulado()/100){

                //se asigna el id_sorteo
                apuestaEntity.setIdSorteo(sorteoByFecha.get(0).getNumeroSorteo());

                // se verifica la cantidad de aciertos
                int aciertos = verificarCantAciertos(sorteoByFecha, apuestaDtoPost);

                //se calcula el premio
                Double premio = calcularPremio(aciertos,
                        apuestaDtoPost.getMontoApostado(),
                        sorteoByFecha.get(0).getDineroTotalAcumulado(),
                        getSorteoById(sorteoByFecha.get(0).getNumeroSorteo()).getApuestas().size()
                );

                // se asigna el resultado segun valor del premio
                if (premio > 0){
                    apuestaEntity.setResultado("Ganador");
                } else {
                    apuestaEntity.setResultado("Perdedor");
                }

                apuestaEntity.setPremio(premio);

                apuestaRepository.save(apuestaEntity);

                response = modelMapper.map(apuestaEntity, ResponseApuestaDtoPost.class);
            }
        }

        return response;
    }

    @Override
    public Sorteo getSorteoById(Integer id_sorteo) {
        //trae todos los sorteos al buscar sin parametro fecha
        List<SorteoResponse> sorteos = sorteoClient.getSorteosByFecha(null);

        Sorteo response = null;

        //verifica si hay sorteos
        if (!sorteos.isEmpty()){

            //se asignan los datos al Sorteo
            for(SorteoResponse sorteo : sorteos){
                if (Objects.equals(sorteo.getNumeroSorteo(), id_sorteo)){
                    response = new Sorteo();
                    response.setIdSorteo(sorteo.getNumeroSorteo());
                    response.setFechaSorteo(sorteo.getFecha());
                    response.setTotalEnReserva(sorteo.getDineroTotalAcumulado());
                    List<ApuestasDelSorteo> apuestas = new ArrayList<>();
                    response.setApuestas(apuestas);
                    break;
                }
            }

            //se asignan las apuestas del Sorteo
            if (response != null){

                List<ApuestasDelSorteo> apuestasDelSorteos = getApuestasByIdSorteo(response.getIdSorteo());

                if (!apuestasDelSorteos.isEmpty()){
                    response.setApuestas(apuestasDelSorteos);

                    //calculo de la reserva
                    //devuelve una list: index 0 = TotalDeApuestas / index 1 = TotalPagado / index 2 = TotalReserva
                    List<Double> calculos = calcularReservaDelSorteo(getApuestasByIdSorteo(response.getIdSorteo()), response.getTotalEnReserva());

                    response.setTotalEnReserva(calculos.get(2));
                }
            }
        }
        return response;
    }

    @Override
    public InfoApuestasEntity getInfoByIdSorteo(Integer id_sorteo) {

        InfoApuestasEntity infoApuesta = null;

        //trae todos los sorteos
        List<SorteoResponse> sorteos = sorteoClient.getSorteosByFecha(null);

        if (!sorteos.isEmpty()){
            for(SorteoResponse sorteo : sorteos){
                if (Objects.equals(sorteo.getNumeroSorteo(), id_sorteo)){

                    infoApuesta = new InfoApuestasEntity();
                    infoApuesta.setIdSorteo(sorteo.getNumeroSorteo());
                    infoApuesta.setFechaSorteo(sorteo.getFecha());
                    infoApuesta.setTotalEnReserva(sorteo.getDineroTotalAcumulado());
                    infoApuesta.setTotalDeApuestas((double)0);
                    infoApuesta.setTotalPagado((double)0);

                    break;
                }
            }

            if (infoApuesta != null){

                //devuelve una list: index 0 = TotalDeApuestas / index 1 = TotalPagado / index 2 = TotalReserva
                List<Double> calculos = calcularReservaDelSorteo(getApuestasByIdSorteo(infoApuesta.getIdSorteo()), infoApuesta.getTotalEnReserva());

                if (!calculos.isEmpty()){
                    infoApuesta.setTotalDeApuestas(calculos.get(0));
                    infoApuesta.setTotalPagado(calculos.get(1));
                    infoApuesta.setTotalEnReserva(calculos.get(2));
                }

                infoApuestasRepository.save(infoApuesta);
            }
        }

        return infoApuesta;
    }

    @Override
    public List<SorteoResponse> getSorteosByFecha(LocalDate fecha) {
        return sorteoClient.getSorteosByFecha(fecha);
    }

    //metodos extra
    public List<ApuestasDelSorteo> getApuestasByIdSorteo(int idSorteo){
        //response.getIdSorteo()
        Optional<List<ApuestaEntity>> apuestas = apuestaRepository.findAllByIdSorteo(idSorteo);

        List<ApuestasDelSorteo> apuestasDelSorteo  = new ArrayList<>();

        if (apuestas.isPresent()){

            for(ApuestaEntity apuesta : apuestas.get()){
                apuestasDelSorteo.add(modelMapper.map(apuesta, ApuestasDelSorteo.class));
            }
        }

        return apuestasDelSorteo;
    }

    public Double calcularPremio(int cantAciertos, Double montoApostado, Double reservaSorteo, Integer cantJugadores){

        //Premios a pagar segun cantidad de asiertos en terminación del numero (10 pts)

        double premio = switch (cantAciertos) {
            case 2 ->
                //2 cifras: 700 %
                    (double) montoApostado * 7;
            case 3 ->
                //3 cifras: 7000 %
                    (double) montoApostado * 70;
            case 4 ->
                //4 cifras: 60000 %
                    (double) montoApostado * 600;
            case 5 ->
                //5 cifras: 350000 %
                    (double) montoApostado * 3500;
            default -> (double) 0;
        };

        //switch normal
//        switch(cantAciertos) {
//            case 2:
//                //2 cifras: 700 %
//                premio = (double) montoApostado * 7;
//                break;
//
//            case 3:
//                //3 cifras: 7000 %
//                premio = montoApostado * 70;
//                break;
//
//            case 4:
//                //4 cifras: 60000 %
//                premio = montoApostado * 600;
//                break;
//
//            case 5:
//                //5 cifras: 350000 %
//                premio = montoApostado * 3500;
//                break;
//        }

        //Estimar pozo base de sorteo (10 pts)
        //El monto total del premio a entregar no podrá superar la reserva presente, si sucede este escenario aplicar
        // un ajuste del 25% sobre el premio por cada 5 jugadores presentes en dicho sorteo.
        if (premio > reservaSorteo){

            for (int j = Math.floorDiv(cantJugadores, 5); j >= 1 ; j--) {
                premio *= 0.75;
            }
        }

        return premio;
    }

    public int verificarCantAciertos(List<SorteoResponse> sorteoByFecha, ApuestaDtoPost apuestaDtoPost) {
        int cantAciertos = 0;
        String nroApostado = apuestaDtoPost.getNumero();

        for (List<Integer> nro : sorteoByFecha.get(0).getNumerosSorteados()) {
            String nroGanador = nro.get(1).toString();
            int cantAciertosAux = 0;

            //longitud minima para poder comparar
            int lenghtMinimo = Math.min(nroGanador.length(), nroApostado.length());

            // itera desde el ultimo caracter hasta el primero (usando la longitud mínima)
            int i = 1;
            while (i <= lenghtMinimo && nroGanador.charAt(nroGanador.length() - i) == nroApostado.charAt(nroApostado.length() - i)) {
                cantAciertosAux++;
                i++;
            }

            cantAciertos = Math.max(cantAciertos, cantAciertosAux);
        }

        return cantAciertos;
    }


    //Total acumulado en reserva (5 pts)
    //El monto acumulado por día es la resultante de la sumatoria de las apuestas más la reserva existente y la
    // posterior quita de los premios otorgados
    public List<Double> calcularReservaDelSorteo(List<ApuestasDelSorteo> apuestas, double reserva){

        double totalDeApuestas = 0;
        double totalPagado = 0;
        double totalReserva = reserva;

        for (ApuestasDelSorteo apuesta : apuestas){
            //calculo total de apuestas (en base a los montoApostado)
            totalDeApuestas += apuesta.getMontoApostado();

            //calculo total pagado (en base a premios entregados)
            totalPagado += apuesta.getPremio();

            //calculo total en reserva (sumando lo recaudado por apuestas y restando lo pagado en premios)
            totalReserva += totalDeApuestas;
            totalReserva -= totalPagado;

        }

        List<Double> calculos = new ArrayList<>();

        calculos.add(totalDeApuestas);
        calculos.add(totalPagado);
        calculos.add(totalReserva);

        return calculos;
    }
}
