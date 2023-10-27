package com.estudianteservice.services;

import com.estudianteservice.entities.EstudianteEntity;
import com.estudianteservice.repositories.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
public class EstudianteService {

    @Autowired
    EstudianteRepository estudianteRepository;

    public ArrayList<EstudianteEntity> obtenerTodosLosEstudiantes(){
        return (ArrayList<EstudianteEntity>) estudianteRepository.findAll();
    }

    public EstudianteEntity findEstudianteById(Long id_estudiante) {
        return estudianteRepository.findById(id_estudiante).orElse(null);
    }

    public EstudianteEntity guardarEstudiante(EstudianteEntity estudiante) {
        return estudianteRepository.save(estudiante);

    }
}
