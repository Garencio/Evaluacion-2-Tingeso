package com.examenservice.repositories;

import com.examenservice.entities.ExamenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamenRepository extends CrudRepository<ExamenEntity, Long> {

    List<ExamenEntity> findByRut(String rut);
}
