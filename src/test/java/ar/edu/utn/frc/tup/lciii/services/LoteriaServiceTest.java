package ar.edu.utn.frc.tup.lciii.services;

import ar.edu.utn.frc.tup.lciii.Entities.ApuestaEntity;
import ar.edu.utn.frc.tup.lciii.Entities.ApuestasDelSorteo;
import ar.edu.utn.frc.tup.lciii.Entities.InfoApuestasEntity;
import ar.edu.utn.frc.tup.lciii.clients.SorteoClient;
import ar.edu.utn.frc.tup.lciii.clients.SorteoResponse;
import ar.edu.utn.frc.tup.lciii.domain.Sorteo;
import ar.edu.utn.frc.tup.lciii.dtos.common.ApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.dtos.common.ResponseApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.repositories.ApuestaRepository;
import ar.edu.utn.frc.tup.lciii.repositories.InfoApuestasRepository;
import ar.edu.utn.frc.tup.lciii.repositories.SorteoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoteriaServiceTest {

    @InjectMocks
    @Spy
    private LoteriaServiceImpl loteriaService;
    @Spy
    private ModelMapper modelMapper;
    @Mock
    private ApuestaRepository apuestaRepository;
    @Mock
    private InfoApuestasRepository infoApuestasRepository;
    @Mock
    private SorteoRepository sorteoRepository;
    @Mock
    private SorteoClient sorteoClient;

    //elementos para testear
    private final List<SorteoResponse> EMPTY_SORTEOS_LIST = new ArrayList<>();
    private ApuestaDtoPost apuestaDtoPost = new ApuestaDtoPost();
    private List<SorteoResponse> sorteoResponses = new ArrayList<>();
    private Sorteo sorteo = new Sorteo();
    private final List<ApuestasDelSorteo> EMPTY_APUESTAS_LIST = new ArrayList<>();
    private List<ApuestasDelSorteo> apuestasDelSorteo = new ArrayList<>();
    private Optional<List<ApuestaEntity>> apuestasEntityOpt = Optional.of(new ArrayList<>());

    @BeforeEach
    public void setUp(){
        //ApuestaDtoPost apuestaDtoPost
        apuestaDtoPost = new ApuestaDtoPost(LocalDate.of(2024,10,10),
                                            "mateo", "45678", (double) 1000);

        //List<SorteoResponse> sorteoResponses
        List<Integer> nro = new ArrayList<>(); nro.add(45678); nro.add(12345);
        List<List<Integer>> numerosSorteados = new ArrayList<>(); numerosSorteados.add(nro);

        SorteoResponse sorteoResponse = new SorteoResponse(123, "2024-10-10", (double) 1000000, numerosSorteados);

        sorteoResponses.add(sorteoResponse);

        //List<ApuestasDelSorteo> apuestasDelSorteo
        ApuestasDelSorteo apuesta1, apuesta2, apuesta3, apuesta4, apuesta5;
        apuesta1 = new ApuestasDelSorteo(); apuesta2 = new ApuestasDelSorteo(); apuesta3 = new ApuestasDelSorteo(); apuesta4 = new ApuestasDelSorteo(); apuesta5 = new ApuestasDelSorteo();
        apuesta1.setMontoApostado((double)100); apuesta1.setPremio((double)0); apuesta2.setMontoApostado((double)500); apuesta2.setPremio((double)50000);
        apuesta3.setMontoApostado((double)1000); apuesta3.setPremio((double)0); apuesta4.setMontoApostado((double)10000); apuesta4.setPremio((double)0);
        apuesta5.setMontoApostado((double)20000); apuesta5.setPremio((double)0);

        apuestasDelSorteo.add(apuesta1); apuestasDelSorteo.add(apuesta2); apuestasDelSorteo.add(apuesta3); apuestasDelSorteo.add(apuesta4); apuestasDelSorteo.add(apuesta5);
        //Sorteo sorteo (con 5 apuestas, asi recorre entero el metodo calcularPremio)
        sorteo.setApuestas(apuestasDelSorteo);

        //creacion de Optional<List<ApuestaEntity>> para cuando: apuestaRepository.findAllByIdSorteo(123)
        ApuestaEntity apuestaEntity1, apuestaEntity2, apuestaEntity3;
        apuestaEntity1 = new ApuestaEntity(1, 123, LocalDate.of(2024,10,10),
                "mateo", "12345", "Ganador", (double)10, (double)10000);
        apuestaEntity2 = new ApuestaEntity(2, 123, LocalDate.of(2024,10,10),
                "jose", "54321", "Perdedor", (double)1000, (double)0);
        apuestaEntity3 = new ApuestaEntity(3, 123, LocalDate.of(2024,10,10),
                "manuel", "111", "Perdedor", (double)500, (double)0);

        List<ApuestaEntity> apuestas = new ArrayList<>();
        apuestas.add(apuestaEntity1); apuestas.add(apuestaEntity2); apuestas.add(apuestaEntity3);

        apuestasEntityOpt = Optional.of(apuestas);
    }

    @Test
    public void postApuesta_Null_sorteo_no_existe(){
        //no existe sorteo correspondiente a la fecha, se devuelve una list vacia
        when(sorteoClient.getSorteosByFecha(apuestaDtoPost.getFechaSorteo())).thenReturn(EMPTY_SORTEOS_LIST);

        //si no hay sorteo, el metodo postApuesta devuelve null
        assertNull(loteriaService.postApuesta(apuestaDtoPost));
        verify(sorteoClient, times(1)).getSorteosByFecha(apuestaDtoPost.getFechaSorteo());
    }

    @Test
    public void postApuesta_Null_montoApostado_mayor_que_reserva_del_sorteo(){
        //si existe un sorteo correspondiente a la fecha, se devuelve una list de SorteoResponse
        when(sorteoClient.getSorteosByFecha(apuestaDtoPost.getFechaSorteo())).thenReturn(sorteoResponses);

        //asigna un montoApostado mayor al 1% de la Reserva del Sorteo
        apuestaDtoPost.setMontoApostado((double)100000);

        //si hay sorteo en esa fecha, pero el montoApostado es mayor a la Reserva del Sorteo, entonces se devuelve null
        assertNull(loteriaService.postApuesta(apuestaDtoPost));
        verify(sorteoClient, times(1)).getSorteosByFecha(apuestaDtoPost.getFechaSorteo());
    }

    @Test
    public void postApuesta_Success_resultado_perdedor(){
        //fakes varios
        when(loteriaService.getSorteoById(sorteoResponses.get(0).getNumeroSorteo()))
                .thenReturn(sorteo);

        //si existe un sorteo correspondiente a la fecha, se devuelve una list de SorteoResponse
        when(sorteoClient.getSorteosByFecha(apuestaDtoPost.getFechaSorteo())).thenReturn(sorteoResponses);

        //asigna un numero que NO salio ganador en el Sorteo, y el montoApostado es menor al 1% de la reserva
        apuestaDtoPost.setNumero("000");

        ResponseApuestaDtoPost response = loteriaService.postApuesta(apuestaDtoPost);

        assertEquals("Perdedor", response.getResultado());
        verify(sorteoClient, times(1)).getSorteosByFecha(apuestaDtoPost.getFechaSorteo());
    }

    @Test
    public void postApuesta_Success_resultado_ganador(){
        //fakes varios
        when(loteriaService.getSorteoById(sorteoResponses.get(0).getNumeroSorteo()))
                .thenReturn(sorteo);

        //si existe un sorteo correspondiente a la fecha, se devuelve una list de SorteoResponse
        when(sorteoClient.getSorteosByFecha(apuestaDtoPost.getFechaSorteo())).thenReturn(sorteoResponses);

        //asigna un numero que SI salio ganador en el Sorteo, y el montoApostado es menor al 1% de la reserva
        apuestaDtoPost.setNumero("12345");

        ResponseApuestaDtoPost response = loteriaService.postApuesta(apuestaDtoPost);

        assertEquals("Ganador", response.getResultado());
        verify(sorteoClient, times(1)).getSorteosByFecha(apuestaDtoPost.getFechaSorteo());
    }

    @Test
    public void getSorteoById_Null_no_encuentra_sorteo_con_idSorteo(){
        //devuelve una list de SorteoResponse
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(sorteoResponses);

        //devuelve Null, porque no hay sorteos con idSorteo 1
        assertNull(loteriaService.getSorteoById(1));
        verify(sorteoClient, times(1)).getSorteosByFecha(null);
    }

    @Test
    public void getSorteoById_Success_sorteo_Sin_apuestas(){
        //fakes varios
        when(loteriaService.getApuestasByIdSorteo(123)).thenReturn(EMPTY_APUESTAS_LIST);

        //se devuelve una list de SorteoResponse
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(sorteoResponses);

        Sorteo sorteoResponse = loteriaService.getSorteoById(123);

        //se devuelve un sorteo, pero no tiene apuestas registradas
        assertEquals(123, sorteoResponse.getIdSorteo());
        assertTrue(sorteoResponse.getApuestas().isEmpty());
        verify(sorteoClient, times(1)).getSorteosByFecha(null);
    }

    @Test
    public void getSorteoById_Success_sorteo_Con_apuestas(){
        //fakes varios
        when(loteriaService.getApuestasByIdSorteo(123)).thenReturn(apuestasDelSorteo);

        //se devuelve una list de SorteoResponse
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(sorteoResponses);

        Sorteo sorteoResponse = loteriaService.getSorteoById(123);

        //se devuelve un sorteo con apuestas registradas
        assertEquals(123, sorteoResponse.getIdSorteo());
        assertFalse(sorteoResponse.getApuestas().isEmpty());
        verify(sorteoClient, times(1)).getSorteosByFecha(null);
    }

    @Test
    public void getInfoByIdSorteo_Null_no_hay_sorteos(){
        //cuando se buscan sorteos, se devuelve una list vacia
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(EMPTY_SORTEOS_LIST);

        //no hay sorteos, devuelve null
        assertNull(loteriaService.getInfoByIdSorteo(123));
        verify(sorteoClient, times(1)).getSorteosByFecha(null);
    }

    @Test
    public void getInfoByIdSorteo_Null_no_encuentra_sorteo_con_idSorteo(){
        //cuando se buscan sorteos, se devuelve una list con un SorteoResponse
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(sorteoResponses);

        //si hay sorteos, pero no con el idSorteo 1
        assertNull(loteriaService.getInfoByIdSorteo(1));
        verify(sorteoClient, times(1)).getSorteosByFecha(null);
    }

    @Test
    public void getInfoByIdSorteo_Success_sorteo_Sin_apuestas(){
        //cuando se buscan sorteos, se devuelve una list con un SorteoResponse
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(sorteoResponses);

        //se busca la info del Sorteo con idSorteo 123 (el cual existe)
        InfoApuestasEntity infoApuestasEntity = loteriaService.getInfoByIdSorteo(123);

        //hay sorteos, y coinciden con el idSorteo 123
        assertEquals(123, infoApuestasEntity.getIdSorteo());
        assertEquals(sorteoResponses.get(0).getFecha(), infoApuestasEntity.getFechaSorteo());

        //al NO haber apuestas registradas en el sorteo, sus reservas siguen igual
        assertEquals(sorteoResponses.get(0).getDineroTotalAcumulado(), infoApuestasEntity.getTotalEnReserva());

        //tampoco hay premios entregados ni apuestas recibidas
        assertEquals((double)0, infoApuestasEntity.getTotalPagado());
        assertEquals((double)0, infoApuestasEntity.getTotalDeApuestas());

        verify(sorteoClient, times(1)).getSorteosByFecha(null);
    }

    @Test
    public void getInfoByIdSorteo_Success_sorteo_Con_apuestas(){
        //devuelve uan lista de ApuestasEntity, para hacer el calculo de Reservas del Sorteo
        when(apuestaRepository.findAllByIdSorteo(123)).thenReturn(apuestasEntityOpt);

        //cuando se buscan sorteos, se devuelve una list con un SorteoResponse
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(sorteoResponses);

        //se busca la info del Sorteo con idSorteo 123 (el cual existe)
        InfoApuestasEntity infoApuestasEntity = loteriaService.getInfoByIdSorteo(123);

        //hay sorteos, y coinciden con el idSorteo 123
        assertEquals(123, infoApuestasEntity.getIdSorteo());
        assertEquals(sorteoResponses.get(0).getFecha(), infoApuestasEntity.getFechaSorteo());

        //al SI haber apuestas registradas en el sorteo, sus reservas cambiaron
        assertNotEquals(sorteoResponses.get(0).getDineroTotalAcumulado(), infoApuestasEntity.getTotalEnReserva());

        //se sumaron las apuestas recibidas y se restaron los premios entregados a la Reserva del Sorteo
        assertTrue((double)0 < infoApuestasEntity.getTotalPagado());
        assertTrue((double)0 < infoApuestasEntity.getTotalDeApuestas());

        verify(sorteoClient, times(1)).getSorteosByFecha(null);
        verify(apuestaRepository, times(1)).findAllByIdSorteo(123);
    }

    @Test
    public void getSorteosByFecha_Success_allSorteos(){
        when(sorteoClient.getSorteosByFecha(null)).thenReturn(sorteoResponses);

        List<SorteoResponse> sorteos = loteriaService.getSorteosByFecha(null);

        assertNotNull(sorteos);
        assertEquals(sorteos, sorteoResponses);
        verify(sorteoClient, times(1)).getSorteosByFecha(null);
    }

    @Test
    public void getSorteosByFecha_Success_SorteoByFechas(){
        LocalDate fecha = LocalDate.of(2024,10,10);

        when(sorteoClient.getSorteosByFecha(fecha)).thenReturn(sorteoResponses);

        List<SorteoResponse> sorteos = loteriaService.getSorteosByFecha(fecha);

        assertNotNull(sorteos);
        assertEquals(sorteos, sorteoResponses);
        verify(sorteoClient, times(1)).getSorteosByFecha(fecha);
    }
}


