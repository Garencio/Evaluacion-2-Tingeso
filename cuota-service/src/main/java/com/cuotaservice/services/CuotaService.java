package com.cuotaservice.services;

import com.cuotaservice.entities.CuotaEntity;
import com.cuotaservice.repositories.CuotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class CuotaService {

    @Autowired
    CuotaRepository cuotaRepository;

    public CuotaEntity guardarCuota(CuotaEntity cuota){

        return cuotaRepository.save(cuota);
    }

    public List<CuotaEntity> obtenerCuotasEstudiante(Long id_estudiante) {
        return cuotaRepository.findById_estudiante(id_estudiante);
    }

    @Transactional
    public void pagarMatricula(Long id_estudiante){
        CuotaEntity cuota = cuotaRepository.findByIdAndTipoNativeQuery(id_estudiante, "Matricula");

        if(cuota == null) {
            System.out.println("No se encontró la cuota de matrícula para el estudiante con ID: " + id_estudiante);
            return;
        }
        else{
            System.out.println("Si se encontro");
        }
        if(!cuota.getEstado()) {
            cuota.setEstado(Boolean.TRUE);
            cuotaRepository.save(cuota);

        }
    }

    @Transactional
    public void generarCuotas(Long id_estudiante, int cuotas, String Tipo_Pago, String AñoEgresoColegio, String TipoColegio){

        double arancel = 1500000.0;
        double descuento = 0.0;

        if(Tipo_Pago.equals("Contado")){
            CuotaEntity cuota = new CuotaEntity();
            cuota.setTipo("Unica Cuota");
            cuota.setId_estudiante(id_estudiante);
            cuota.setMonto(arancel/2);
            cuota.setMontoBase(arancel/2);
            cuota.setEstado(false);
            cuota.setVencimiento(null);
            cuota.setFechapago(null);
            cuotaRepository.save(cuota);
        }
        else{
            switch (TipoColegio) {
                case "Municipal" -> {
                    descuento += 0.20;
                }
                case "Subvencionado" -> {
                    descuento += 0.10;
                }
            }

            int AñoActual = LocalDate.now().getYear();
            int AñoEgreso = AñoActual - Integer.parseInt(AñoEgresoColegio);

            if(AñoEgreso < 1) {
                descuento += 0.15;
            } else if(AñoEgreso <= 2) {
                descuento += 0.08;
            } else if(AñoEgreso <= 4) {
                descuento += 0.04;
            }

            double arancelFinal = arancel * (1 - descuento);
            double valorCuota = arancelFinal / cuotas;
            LocalDate fechaActual = LocalDate.now();

            for(int i = 0; i < cuotas; i++) {
                CuotaEntity cuota = new CuotaEntity();
                cuota.setTipo(String.format("Cuota %d", i + 1));
                cuota.setId_estudiante(id_estudiante);
                cuota.setMonto(valorCuota);
                cuota.setMontoBase(valorCuota);
                cuota.setEstado(false);
                LocalDate vencimiento = fechaActual.withDayOfMonth(10).plusMonths(i+1);
                cuota.setVencimiento(vencimiento);
                cuota.setFechapago(null);
                cuotaRepository.save(cuota);
            }
        }
    }

    public Double calcularInteres(CuotaEntity cuota){
        if(!cuota.getEstado()){
            final double interes_1 = 0.03;
            final double interes_2 = 0.06;
            final double interes_3 = 0.09;
            final double interes_maximo = 0.15;

            LocalDate fechaActual = LocalDate.now();
            LocalDate vencimiento = cuota.getVencimiento();

            long atraso = ChronoUnit.MONTHS.between(vencimiento, fechaActual);

            if (atraso <= 0){
                return cuota.getMontoBase();
            }
            if(atraso == 1){
                double interes = cuota.getMontoBase() * interes_1;
                return cuota.getMontoBase() + interes;
            }
            if (atraso == 2){
                double interes = cuota.getMontoBase() * interes_2;
                return cuota.getMontoBase() + interes;
            }
            if(atraso == 3){
                double interes = cuota.getMontoBase() * interes_3;
                return cuota.getMontoBase() + interes;
            }

            double interes = cuota.getMontoBase() * interes_maximo;
            return cuota.getMontoBase() + interes;
        }
        return cuota.getMontoBase();
    }

    @Transactional
    public void pagarCuota(Long id_estudiante, String tipo) {
        CuotaEntity cuota = cuotaRepository.findByIdAndTipoNativeQuery(id_estudiante, tipo);

        if (cuota != null && !cuota.getEstado()) {
            if (!cuota.getTipo().equals("Unica Cuota")) {
                Double montoConInteres = calcularInteres(cuota);
                cuota.setMonto(montoConInteres);
            }
            cuota.setEstado(Boolean.TRUE);
            cuota.setFechapago(LocalDate.now());
            cuotaRepository.save(cuota);
        }
    }

    public Double calcularDescuento(Double puntajePromedio) {
        if (puntajePromedio >= 950 && puntajePromedio <= 1000) {
            return 0.10;
        } else if (puntajePromedio >= 900 && puntajePromedio < 950) {
            return 0.05;
        } else if (puntajePromedio >= 850 && puntajePromedio < 900) {
            return 0.02;
        } else {
            return 0.0;
        }
    }

    public List<CuotaEntity> obtenerCuotasConInteres(Long id_estudiante, Double puntajePromedio) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(id_estudiante);
        Double descuento = calcularDescuento(puntajePromedio);

        cuotas.forEach(cuota -> {
            if(!cuota.getTipo().equals("Matricula")){
                if(!cuota.getTipo().equals("Unica Cuota")){
                    if(!cuota.getEstado()){
                        cuota.setMonto(calcularInteres(cuota));
                        cuota.setMonto(cuota.getMonto() * (1 - descuento));
                    }
                }
            }
        });

        return cuotas;
    }

    public Double MontoTotal(Long id_estudiante, Double puntajePromedio){
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(id_estudiante);
        Double descuento = calcularDescuento(puntajePromedio);
        double[] MontoTotalArr = {0.0};

        cuotas.forEach(cuota -> {
            if(!cuota.getTipo().equals("Matricula")){
                if(!cuota.getEstado()){
                    cuota.setMonto(calcularInteres(cuota));
                }
                cuota.setMonto(cuota.getMonto() * (1 - descuento));
                MontoTotalArr[0] = MontoTotalArr[0] + cuota.getMonto();
            }
        });

        return MontoTotalArr[0];
    }

    public int numeroCuotasPagadas(Long id_estudiante) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(id_estudiante);
        return (int) cuotas.stream().filter(CuotaEntity::getEstado).count();
    }

    public Double montoTotalPagado(Long id_estudiante) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(id_estudiante);
        return cuotas.stream().filter(CuotaEntity::getEstado).mapToDouble(CuotaEntity::getMonto).sum();
    }

    public LocalDate fechaUltimoPago(Long id_estudiante) {
        List<CuotaEntity> cuotasPagadas = obtenerCuotasEstudiante(id_estudiante).stream()
                .filter(CuotaEntity::getEstado)
                .filter(cuota -> cuota.getFechapago() != null)
                .toList();

        return cuotasPagadas.stream()
                .max(Comparator.comparing(CuotaEntity::getFechapago))
                .map(CuotaEntity::getFechapago)
                .orElse(null);
    }


    public Double saldoPorPagar(Long id_estudiante) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(id_estudiante);
        return cuotas.stream().filter(cuota -> !cuota.getEstado()).mapToDouble(CuotaEntity::getMonto).sum();
    }

    public Long numeroCuotasConRetraso(Long id_estudiante) {
        LocalDate today = LocalDate.now();
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(id_estudiante);
        return cuotas.stream()
                .filter(cuota -> !cuota.getEstado() && !cuota.getTipo().equals("Matricula")
                        && cuota.getVencimiento().isBefore(today))
                .count();
    }
}
