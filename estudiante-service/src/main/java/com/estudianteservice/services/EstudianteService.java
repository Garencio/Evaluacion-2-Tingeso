package com.estudianteservice.services;

import com.estudianteservice.entities.EstudianteEntity;
import com.estudianteservice.repositories.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;

@Service
public class EstudianteService {

    @Autowired
    EstudianteRepository estudianteRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ArrayList<EstudianteEntity> obtenerTodosLosEstudiantes(){
        return (ArrayList<EstudianteEntity>) estudianteRepository.findAll();
    }

    public EstudianteEntity findEstudianteById(Long id_estudiante) {
        return estudianteRepository.findById(id_estudiante).orElse(null);
    }

    public EstudianteEntity guardarEstudiante(EstudianteEntity estudiante) {
        EstudianteEntity estudianteGuardado = estudianteRepository.save(estudiante);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Long> entity = new HttpEntity<>(estudianteGuardado.getId(), headers);
        restTemplate.exchange("http://cuota-service/cuotas/generarMatricula", HttpMethod.POST, entity, Void.class);

        return estudianteGuardado;
    }
}
