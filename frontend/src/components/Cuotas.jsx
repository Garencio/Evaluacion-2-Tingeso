import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

function Cuotas() {
    const [estudiante, setEstudiante] = useState({});
    const [cuotas, setCuotas] = useState([]);
    const [tipoSeleccionado, setTipoSeleccionado] = useState(null);

    const { id } = useParams();

    useEffect(() => {
        fetch(`http://localhost:8080/cuotas/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al obtener datos');
                }
                return response.json();
            })
            .then(data => {
                setEstudiante(data.estudiante);
                setCuotas(data.cuotas);
            })
            .catch(error => console.error('Hubo un error al obtener los datos:', error));
        
    }, [id]);

    const matriculaPagada = cuotas.some(cuota => cuota.tipo === 'Matricula' && cuota.estado);

    function pagar(tipo) {
        let url;
        let data;

        if (tipo === 'matricula') {
            url = `http://localhost:8080/cuotas/pagar-matricula/${id}`;
            data = { method: 'POST' };
        } else {
            url = `http://localhost:8080/cuotas/pagar-cuota`;
            data = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ idEstudiante: id, tipo: tipoSeleccionado })
            };
        }

        fetch(url, data)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al pagar');
                }
                return response.json();
            })
            .then(() => {
                
                fetch(`http://localhost:8080/cuotas/${id}`)
                    .then(response => response.json())
                    .then(data => {
                        setEstudiante(data.estudiante);
                        setCuotas(data.cuotas);
                    });
            })
            .catch(error => console.error('Error:', error));
    }

    return (
        <div>
            <h1 className="titulo-formulario">Detalles del estudiante</h1>
            <h2>Información del Estudiante</h2>
            <p>Rut: {estudiante.rut}</p>
            <p>Apellidos: {estudiante.apellidos}</p>
            <p>Nombres: {estudiante.nombres}</p>

            <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <a href={`/resumen/${estudiante.id}`} className="btnResumen">Ver Resumen</a>
            </div>

            <h2>Cuotas</h2>
            <table border="1">
                <thead>
                    <tr>
                        <th>Tipo De Cuota</th>
                        <th>Monto</th>
                        <th>Pagada</th>
                        <th>Vencimiento</th>
                        <th>Fecha de Pago</th>
                    </tr>
                </thead>
                <tbody>
                    {cuotas.map(cuota => (
                        <tr key={cuota.id}>
                            <td>{cuota.tipo}</td>
                            <td>{cuota.monto}</td>
                            <td>{cuota.estado}</td>
                            <td>{cuota.vencimiento}</td>
                            <td>{cuota.fechapago}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {!matriculaPagada && (
                <button onClick={() => pagar('matricula')}>Pagar Matrícula</button>
            )}

            <div>
                <select onChange={e => setTipoSeleccionado(e.target.value)}>
                    {cuotas.map(cuota => (
                        <option key={cuota.id} value={cuota.tipo}>{cuota.tipo}</option>
                    ))}
                </select>
                <button onClick={() => pagar('cuota')}>Pagar Cuota Seleccionada</button>
            </div>
        </div>
    );
}

export default Cuotas;
