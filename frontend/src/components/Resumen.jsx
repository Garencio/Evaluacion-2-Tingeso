import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

function Resumen() {
    const [resumen, setResumen] = useState({});
    const { id } = useParams();

    useEffect(() => {
        fetch(`http://localhost:8080/examenes/resumen/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al obtener datos del resumen');
                }
                return response.json();
            })
            .then(data => {
                setResumen(data);
            })
            .catch(error => console.error('Hubo un error al obtener el resumen:', error));
    }, [id]);

    return (
        <div>
            <h1 className="titulo-formulario">Resumen del Estudiante</h1>
            <p><strong>RUT:</strong> {resumen.rut}</p>
            <p><strong>Nombre:</strong> {resumen.nombre}</p>
            <p><strong>Nro. exámenes rendidos:</strong> {resumen.numeroExamenesRendidos}</p>
            <p><strong>Promedio Examenes:</strong> {resumen.promedioPuntajeExamenes}</p>
            <p><strong>Monto total a pagar:</strong> {resumen.montoTotalAPagar}</p>
            <p><strong>Tipo de Pago:</strong> {resumen.tipoPago}</p>
            <p><strong>Nro. de cuotas:</strong> {resumen.numeroTotalCuotasPactadas}</p>
            <p><strong>Nro. cuotas pagadas:</strong> {resumen.numeroCuotasPagadas}</p>
            <p><strong>Monto total pagado:</strong> {resumen.montoTotalPagado}</p>
            <p><strong>Fecha ultimo pago:</strong> {resumen.fechaUltimoPago}</p>
            <p><strong>Saldo por pagar:</strong> {resumen.saldoPorPagar}</p>
            <p><strong>Nro.Cuotas retrasadas:</strong> {resumen.numeroCuotasConRetraso}</p>
            <button onClick={() => window.history.back()}>Regresar</button>
        </div>
    );
}

export default Resumen;
