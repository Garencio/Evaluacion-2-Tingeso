package com.cuotaservice.services;

import com.cuotaservice.entities.CuotaEntity;
import com.cuotaservice.models.EstudianteModel;
import com.cuotaservice.repositories.CuotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class CuotaService {

    @Autowired
    CuotaRepository cuotaRepository;

    @Autowired
    private RestTemplate restTemplate;

    public void guardarCuota(CuotaEntity cuota){

        cuotaRepository.save(cuota);
    }

    public List<CuotaEntity> obtenerCuotasEstudiante(Long idEstudiante) {
        return cuotaRepository.findByIdEstudiante(idEstudiante);
    }

    public EstudianteModel buscarEstudiante(Long idEstudiante) {
        try {
            return restTemplate.getForObject("http://estudiante-service/estudiantes/" + idEstudiante, EstudianteModel.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el estudiante con ID: " + idEstudiante, e);
        }
    }

    public Double obtenerPuntajePromedio(String rut){
        try {
            return restTemplate.getForObject("http://examen-service/examenes/puntaje-promedio/" + rut, Double.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el puntaje de: " + rut, e);
        }
    }

    @Transactional
    public void generarMatricula(Long idEstudiante){
        CuotaEntity cuota = new CuotaEntity();
        cuota.setIdEstudiante(idEstudiante);
        cuota.setMonto(70000.0);
        cuota.setMontoBase(70000.0);
        cuota.setEstado(false);
        cuota.setTipo("Matricula");
        guardarCuota(cuota);
    }

    @Transactional
    public void pagarMatricula(Long idEstudiante){
        CuotaEntity cuota = cuotaRepository.findByIdAndTipoNativeQuery(idEstudiante, "Matricula");

        if(cuota == null) {
            System.out.println("No se encontró la cuota de matrícula para el estudiante con ID: " + idEstudiante);
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
    public void generarCuotas(Long idEstudiante){
        EstudianteModel estudiante = buscarEstudiante(idEstudiante);

        double arancel = 1500000.0;
        double descuento = 0.0;
        int cuotas = estudiante.getCantidad_cuotas();

        if(estudiante.getTipodepago().equals("Contado")){
            CuotaEntity cuota = new CuotaEntity();
            cuota.setTipo("Unica Cuota");
            cuota.setIdEstudiante(idEstudiante);
            cuota.setMonto(arancel/2);
            cuota.setMontoBase(arancel/2);
            cuota.setEstado(false);
            cuota.setVencimiento(null);
            cuota.setFechapago(null);
            cuotaRepository.save(cuota);
        }
        else{
            switch (estudiante.getTipocolegio()) {
                case "Municipal" -> {
                    descuento += 0.20;
                }
                case "Subvencionado" -> {
                    descuento += 0.10;
                }
            }

            int AñoActual = LocalDate.now().getYear();
            int AñoEgreso = AñoActual - Integer.parseInt(estudiante.getAñoegresocolegio());

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
                cuota.setIdEstudiante(idEstudiante);
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
    public void pagarCuota(Long idEstudiante, String tipo) {
        CuotaEntity cuota = cuotaRepository.findByIdAndTipoNativeQuery(idEstudiante, tipo);

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


    public List<CuotaEntity> obtenerCuotasConInteres(Long idEstudiante) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(idEstudiante);
        EstudianteModel estudiante = buscarEstudiante(idEstudiante);
        Double puntajePromedio = obtenerPuntajePromedio(estudiante.getRut());
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

    public Double MontoTotal(Long idEstudiante){
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(idEstudiante);
        EstudianteModel estudiante = buscarEstudiante(idEstudiante);
        Double puntajePromedio = obtenerPuntajePromedio(estudiante.getRut());
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

    public int numeroCuotasPagadas(Long idEstudiante) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(idEstudiante);
        return (int) cuotas.stream().filter(CuotaEntity::getEstado).count();
    }

    public Double montoTotalPagado(Long idEstudiante) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(idEstudiante);
        return cuotas.stream().filter(CuotaEntity::getEstado).mapToDouble(CuotaEntity::getMonto).sum();
    }

    public LocalDate fechaUltimoPago(Long idEstudiante) {
        List<CuotaEntity> cuotasPagadas = obtenerCuotasEstudiante(idEstudiante).stream()
                .filter(CuotaEntity::getEstado)
                .filter(cuota -> cuota.getFechapago() != null)
                .toList();

        return cuotasPagadas.stream()
                .max(Comparator.comparing(CuotaEntity::getFechapago))
                .map(CuotaEntity::getFechapago)
                .orElse(null);
    }


    public Double saldoPorPagar(Long idEstudiante) {
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(idEstudiante);
        return cuotas.stream().filter(cuota -> !cuota.getEstado()).mapToDouble(CuotaEntity::getMonto).sum();
    }

    public Long numeroCuotasConRetraso(Long idEstudiante) {
        LocalDate today = LocalDate.now();
        List<CuotaEntity> cuotas = obtenerCuotasEstudiante(idEstudiante);
        return cuotas.stream()
                .filter(cuota -> !cuota.getEstado() && !cuota.getTipo().equals("Matricula")
                        && cuota.getVencimiento().isBefore(today))
                .count();
    }

}
