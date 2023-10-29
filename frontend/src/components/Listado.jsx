import React, { useState, useEffect } from 'react';

function Listado() {
    const [estudiantes, setEstudiantes] = useState([]);

    useEffect(() => {
        // Suponiendo que tu API para obtener los estudiantes está en esta URL
        fetch('http://localhost:8080/estudiantes/listado')
            .then(response => response.json())
            .then(data => setEstudiantes(data))
            .catch(error => console.error('Hubo un error al obtener los estudiantes:', error));
    }, []);

    return (
        <div>
            <div className="titulo-formulario">
                <h1>Listado de Estudiantes</h1>
            </div>
            <ul>
                {estudiantes.map(estudiante => (
                    <li key={estudiante.id}>
                        <a href={`/cuotas/${estudiante.id}`}>
                            {estudiante.nombres} {estudiante.apellidos}
                        </a>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default Listado;
