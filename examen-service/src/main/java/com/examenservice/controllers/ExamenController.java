package com.examenservice.controllers;

import com.examenservice.entities.ExamenEntity;
import com.examenservice.services.ExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/examenes")
public class ExamenController {

    @Autowired
    private ExamenService examenService;

    @GetMapping
    public ResponseEntity<List<ExamenEntity>> listar() {
        ArrayList<ExamenEntity> examenEntities = examenService.obtenerData();
        return ResponseEntity.ok(examenEntities);
    }

    @GetMapping("/puntaje-promedio/{rut}")
    public ResponseEntity<Double> obtenerPuntajePromedio(@PathVariable String rut){
        Double puntaje = examenService.calcularPuntajePromedio(rut);
        return ResponseEntity.ok(puntaje);
    }

    @PostMapping("/upload")
    public void upload(@RequestParam("archivo") MultipartFile archivo, RedirectAttributes redirectAttributes) {
        examenService.guardar(archivo);
        redirectAttributes.addFlashAttribute("mensaje", "Archivo cargado correctamente");
        examenService.leerCsv("Examenes.csv");
    }
    
}
