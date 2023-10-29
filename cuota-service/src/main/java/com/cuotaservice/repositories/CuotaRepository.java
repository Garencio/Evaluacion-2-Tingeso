package com.cuotaservice.repositories;

import com.cuotaservice.entities.CuotaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuotaRepository extends CrudRepository<CuotaEntity, Long> {

    @Query(value = "Select * FROM cuota WHERE idEstudiante = :idEstudiante AND tipo = :tipo", nativeQuery = true)
    CuotaEntity findByIdAndTipoNativeQuery(@Param("idEstudiante") Long idEstudiante, @Param("tipo") String tipo);

    List<CuotaEntity> findByIdEstudiante(Long idEstudiante);
}
