package com.estudianteservice.controllers;

import com.estudianteservice.entities.EstudianteEntity;
import com.estudianteservice.services.EstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping
public class EstudianteController {

    @Autowired
    EstudianteService estudianteService;


    @PostMapping("/guardar")
    public String guardarEstudiante(@ModelAttribute EstudianteEntity estudiante) {
        estudianteService.guardarEstudiante(estudiante);
        return "redirect:/";
    }

    @GetMapping("/estudiantes")
    public String mostrarEstudiantes(Model model){
        List<EstudianteEntity> estudianteEntities = estudianteService.obtenerTodosLosEstudiantes();
        model.addAttribute("estudiantes", estudianteEntities);
        return "listado-estudiantes";
    }
}
