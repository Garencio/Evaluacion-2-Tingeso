package com.cuotaservice.controllers;

import com.cuotaservice.entities.CuotaEntity;
import com.cuotaservice.models.EstudianteModel;
import com.cuotaservice.services.CuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cuotas")
public class CuotaController {

    @Autowired
    CuotaService cuotaService;

    @PostMapping("/generarMatricula")
    public ResponseEntity<Void> generarMatricula(@RequestBody Long idEstudiante){
        cuotaService.generarMatricula(idEstudiante);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{idEstudiante}")
    public ResponseEntity<Map<String, Object>> mostrarCuotas(@PathVariable Long idEstudiante) {

        List<CuotaEntity> cuotas = cuotaService.obtenerCuotasConInteres(idEstudiante);
        EstudianteModel estudiante = cuotaService.buscarEstudiante(idEstudiante);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estudiante", estudiante);
        respuesta.put("cuotas", cuotas);

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/pagar-matricula/{idEstudiante}")
    public String pagarMatricula(@PathVariable Long idEstudiante){
        cuotaService.pagarMatricula(idEstudiante);
        cuotaService.generarCuotas(idEstudiante);
        return "redirect:/cuotas/" + idEstudiante;
    }

    @PostMapping("/pagar-cuota")
    public String pagarCuota(@RequestParam Long idEstudiante, @RequestParam String tipo){
        cuotaService.pagarCuota(idEstudiante, tipo);
        return "redirect:/cuotas/" + idEstudiante;
    }

}
