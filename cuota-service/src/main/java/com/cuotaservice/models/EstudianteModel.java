package com.cuotaservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteModel {

    private String rut;
    private String apellidos;
    private String nombres;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date nacimiento;

    private String tipocolegio;
    private String nombrecolegio;
    private String añoegresocolegio;
    private String tipodepago;
    private int cantidad_cuotas;

}
