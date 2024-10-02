package ar.edu.utn.frc.tup.lciii.clients;

import ar.edu.utn.frc.tup.lciii.repositories.SorteoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SorteoClient {

    @Autowired
    private SorteoRepository sorteoRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RestTemplate restTemplate;

    public List<SorteoResponse> getSorteosByFecha(LocalDate fecha) {

        //http://localhost:8082/sorteos?fecha=2024-01-16
        String url = "";

        if (fecha != null){
            url = "http://localhost:8082/sorteos?fecha=" + fecha;
        } else {
            url = "http://localhost:8082/sorteos";
        }

        ResponseEntity<List<SorteoResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SorteoResponse>>() {}
        );

        return response.getBody();
    }

}
