package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.Entities.InfoApuestasEntity;
import ar.edu.utn.frc.tup.lciii.clients.SorteoResponse;
//import ar.edu.utn.frc.tup.lciii.domain.InfoApuestas;
import ar.edu.utn.frc.tup.lciii.domain.Sorteo;
import ar.edu.utn.frc.tup.lciii.dtos.common.ApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.dtos.common.ResponseApuestaDtoPost;
import ar.edu.utn.frc.tup.lciii.services.LoteriaService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; //mockMvc perform post / get / put / etc

import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
public class LoteriaControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    private MockMvc mockMvc;
    @InjectMocks
    private LoteriaController loteriaController;

    @Mock
    private LoteriaService loteriaService;


    //urls
    private static final String URL_POST_Apuesta = "/loteria/apuesta";
    private static final String URL_GET_SorteosByFecha = "/loteria/sorteo/";
    private static final String URL_GET_SorteoById = "/loteria/sorteo/";
    private static final String URL_GET_InfoByIdSorteo = "/loteria/total/";

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(loteriaController).build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void postApuesta_Success_V2() {

        ApuestaDtoPost apuestaDtoPost = new ApuestaDtoPost();
        ResponseApuestaDtoPost response = new ResponseApuestaDtoPost();
        when(loteriaService.postApuesta(apuestaDtoPost)).thenReturn(response);

        ResponseEntity<ResponseApuestaDtoPost> responseEntity = loteriaController.postApuesta(apuestaDtoPost);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(loteriaService, times(1)).postApuesta(apuestaDtoPost);
    }
    @Test
    void postApuesta_BadGateway_V2() {

        ApuestaDtoPost apuestaDtoPost = new ApuestaDtoPost();
        when(loteriaService.postApuesta(apuestaDtoPost)).thenReturn(null);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            loteriaController.postApuesta(apuestaDtoPost);
        });

        assertEquals(BAD_GATEWAY.value(), exception.getStatusCode().value());
        verify(loteriaService, times(1)).postApuesta(apuestaDtoPost);
    }

    @Test
    void getSorteos_Success() throws Exception {

        //lista de sorteos NO vacia
        List<SorteoResponse> sorteos = new ArrayList<>();
        sorteos.add(new SorteoResponse());

        when(loteriaService.getSorteosByFecha(null)).thenReturn(sorteos);

        MockHttpServletResponse response = this.mockMvc.perform(get(URL_GET_SorteosByFecha)
                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(loteriaService, times(1)).getSorteosByFecha(null);

    }
    @Test
    void getSorteos_BadRequest() throws Exception {

        //lista de sorteos vacia
        List<SorteoResponse> sorteos = new ArrayList<>();

        //fecha q no tiene ningun sorteo asignado
        LocalDate fecha = LocalDate.of(2020, 10, 10);
        Optional<LocalDate> fechaOpt = Optional.of(LocalDate.of(2020, 10, 10));


        when(loteriaService.getSorteosByFecha(fecha)).thenReturn(sorteos);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            loteriaController.getSorteosByFecha(fechaOpt);
        });

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        verify(loteriaService, times(1)).getSorteosByFecha(fecha);
    }

    @Test
    void getSorteoById_Success() throws Exception {
        //idSorteo
        int idSorteo = 0;

        Sorteo sorteo = new Sorteo();

        when(loteriaService.getSorteoById(idSorteo)).thenReturn(sorteo);

        MockHttpServletResponse response = this.mockMvc.perform(get(URL_GET_SorteoById + idSorteo)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(loteriaService, times(1)).getSorteoById(idSorteo);
    }
    @Test
    void getSorteoById_BadRequest() throws Exception {
        //idSorteo
        int idSorteo = 0;

        when(loteriaService.getSorteoById(idSorteo)).thenReturn(null);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            loteriaController.getSorteoById(idSorteo);
        });

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        verify(loteriaService, times(1)).getSorteoById(idSorteo);
    }

    @Test
    void getInfoSorteoById_Success() throws Exception {
        //idSorteo
        int idSorteo = 0;

        InfoApuestasEntity infoSorteo = new InfoApuestasEntity();

        when(loteriaService.getInfoByIdSorteo(idSorteo)).thenReturn(infoSorteo);

        MockHttpServletResponse response = this.mockMvc.perform(get(URL_GET_InfoByIdSorteo + idSorteo)
                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(loteriaService, times(1)).getInfoByIdSorteo(idSorteo);
    }
    @Test
    void getInfoSorteoById_BadRequest(){
        //idSorteo
        int idSorteo = 0;

        when(loteriaService.getInfoByIdSorteo(idSorteo)).thenReturn(null);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            loteriaController.getInfoSorteoById(idSorteo);
        });

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        verify(loteriaService, times(1)).getInfoByIdSorteo(idSorteo);
    }

    @Ignore
    @Test
    void postApuesta_Success() throws Exception{
        ApuestaDtoPost apuestaDtoPost = new ApuestaDtoPost(LocalDate.of(2024, 1, 16),
                "mateo", "56789", (double)100);

        ResponseApuestaDtoPost responseApuestaDtoPost = new ResponseApuestaDtoPost(123,
                LocalDate.of(2024, 1, 16),
                "mateo", "56789", "Ganador");

        when(loteriaService.postApuesta(apuestaDtoPost)).thenReturn(responseApuestaDtoPost);

        //no me funca esto de pasar el objeto Java a un Strong JSON
        //String jsonContent = objectMapper.writeValueAsString(apuestaDtoPost);

        //tampoco funca
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("fechaSorteo", apuestaDtoPost.getFechaSorteo());
//        jsonObject.put("idCliente", apuestaDtoPost.getIdCliente());
//        jsonObject.put("numero", apuestaDtoPost.getNumero());
//        jsonObject.put("montoApostado", apuestaDtoPost.getMontoApostado());
//
//        String jsonString = jsonObject.toString();

        MockHttpServletResponse response = this.mockMvc.perform(post(URL_POST_Apuesta)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(apuestaDtoPost.toString()))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

//    @Ignore
//    @Test
//    void postApuesta_BadGateway() throws Exception {
//        ApuestaDtoPost apuestaDtoPost = new ApuestaDtoPost();
//
//        when(loteriaService.postApuesta(apuestaDtoPost)).thenReturn(null);
//
//        //String jsonContent = objectMapper.writeValueAsString(apuestaDtoPost);
//
//        MockHttpServletResponse response = this.mockMvc.perform(post("/loteria/apuesta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(apuestaDtoPost.toString()))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(BAD_GATEWAY.value());
//    }

    //    @Test
//    void postApuesta_Success() throws Exception{
//        ApuestaDtoPost apuestaDtoPost = new ApuestaDtoPost((LocalDate.of(2024, 1, 16)),
//                                                            "mateo", "56789", (double)1000);
//
//        ResponseApuestaDtoPost responseApuestaDtoPost = new ResponseApuestaDtoPost(123,
//                                                            LocalDate.of(2024, 1, 16),
//                                                            "mateo", "56789", "Ganador");
//
//
//        //Unhandled exception: com.fasterxml.jackson.core.JsonProcessingException
//        String jsonContent = objectMapper.writeValueAsString(apuestaDtoPost);
//
//        when(loteriaService.postApuesta(apuestaDtoPost)).thenReturn(responseApuestaDtoPost);
//
//        MockHttpServletResponse response = this.mockMvc.perform(post(URL_POST)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(jsonContent))
//                        .andReturn().getResponse();
//
//        assertEquals(response.getStatus(), HttpStatus.OK.value());
//    }
}
