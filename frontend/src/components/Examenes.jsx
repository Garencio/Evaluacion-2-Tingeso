import React, { useState, useEffect } from 'react';

function Examenes() {
    const [examenes, setExamenes] = useState([]);

    useEffect(() => {
        fetch('http://localhost:8080/examenes')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al obtener datos');
                }
                return response.json();
            })
            .then(data => {
                setExamenes(data);
            })
            .catch(error => {
                console.error('Hubo un error al obtener los datos:', error);
            });
    }, []);

    return (
        <div>
            <h2 className="titulo-formulario">Listado de exámenes</h2>

            <table border="1">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Rut</th>
                    <th>Fecha examen</th>
                    <th>Puntaje</th>
                </tr>
                </thead>
                <tbody>
                {examenes.map(examen => (
                    <tr key={examen.id}>
                        <td>{examen.id}</td>
                        <td>{examen.rut}</td>
                        <td>{examen.fecha_examen}</td>
                        <td>{examen.puntaje}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default Examenes;

