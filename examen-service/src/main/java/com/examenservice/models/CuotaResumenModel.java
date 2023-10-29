package com.examenservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuotaResumenModel {

    private Double MontoTotal;
    private int numeroCuotasPagadas;
    private Double montoTotalPagado;
    private LocalDate fechaUltimoPago;
    private Double saldoPorPagar;
    private Long numeroCuotasConRetraso;

}
