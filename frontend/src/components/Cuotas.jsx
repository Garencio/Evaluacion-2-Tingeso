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
                if (data.cuotas.length === 1) {
                    setTipoSeleccionado(data.cuotas[0].tipo);
                }
                else if (data.cuotas.length  > 0) {
                    setTipoSeleccionado(data.cuotas[1].tipo);
                }
            })
            .catch(error => console.error('Hubo un error al obtener los datos:', error));
        
    }, [id]);

    const matriculaPagada = cuotas.some(cuota => cuota.tipo === 'Matricula' && cuota.estado);
    

    function pagar(tipo) {
        let url;
        let data;

        if (!tipoSeleccionado) {
            console.error('Tipo de cuota no seleccionado.');
            return;
        }
    
        if (tipo === 'matricula') {
            url = `http://localhost:8080/cuotas/pagar-matricula/${id}`;
            data = { method: 'POST' };
        } else {
            url = `http://localhost:8080/cuotas/pagar-cuota?idEstudiante=${id}&tipo=${tipoSeleccionado}`;
            data = {
                method: 'POST'
            };
        }
        
        console.log("Datos enviados:", { idEstudiante: id, tipo: tipoSeleccionado });

        fetch(url, data)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al pagar');
                }
                return response.json();
            })
            .then(() => {
                return fetch(`http://localhost:8080/cuotas/${id}`);
            })
            .then(data => {
                setEstudiante(data.estudiante);
                setCuotas(data.cuotas);
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
                <a href={`/examenes/resumen/${id}`} className="btnResumen">Ver Resumen</a>
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
                {cuotas.filter(cuota => !cuota.estado).length > 0 ? (
                    <>
                        <select onChange={e => setTipoSeleccionado(e.target.value)}>
                            {cuotas.filter(cuota => !cuota.estado).map(cuota => (
                                <option key={cuota.id} value={cuota.tipo}>{cuota.tipo}</option>
                            ))}
                        </select>
                        <button onClick={() => pagar('cuota')}>Pagar Cuota Seleccionada</button>
                    </>
                ) : (
                    <p>No hay cuotas pendientes de pago.</p>
                )}
            </div>
        </div>
    );
}

export default Cuotas;
