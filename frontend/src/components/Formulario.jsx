import React, { useState } from 'react';


function Formulario() {

    const [formData, setFormData] = useState({
        rut: '',
        apellidos: '',
        nombres: '',
        nacimiento: '',
        tipocolegio: '',
        nombrecolegio: '',
        añoegresocolegio: '',
        tipodepago: '',
        cantidad_cuotas: 0
    });

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const validarRUT = (rut) => {
        const regex = /^\d{2}\.\d{3}\.\d{3}-[\dkK]$/;
        return regex.test(rut);
    }

    const handleSubmit = async (event) => {
        event.preventDefault();
        
        if (!validarRUT(formData.rut)) {
            alert('Formato de RUT inválido. Debe ser del formato xx.xxx.xxx-x');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/estudiantes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                throw new Error('Error al guardar el estudiante');
            }


            alert('Estudiante guardado con éxito');
            setFormData({
                rut: '',
                apellidos: '',
                nombres: '',
                nacimiento: '',
                tipocolegio: '',
                nombrecolegio: '',
                añoegresocolegio: '',
                tipodepago: '',
                cantidad_cuotas: 0
            });

        } catch (error) {
            console.error('Hubo un error:', error);
            alert('Hubo un error al guardar el estudiante. Por favor, inténtalo de nuevo.');
        }

    
    };

    const handleTipoColegioChange = () => {
        let maxCuotas;

        switch(formData.tipocolegio) {
            case 'Municipal':
                maxCuotas = 10;
                break;
            case 'Subvencionado':
                maxCuotas = 7;
                break;
            case 'Privado':
                maxCuotas = 4;
                break;
            default:
                maxCuotas = 0;
                break;
        }

        const cuotasOptions = [];
        for (let i = 1; i <= maxCuotas; i++) {
            cuotasOptions.push(i);
        }

        return cuotasOptions;
    };

    const handleTipoDePagoChange = () => {
        if (formData.tipodepago === 'Contado') {
            setFormData(prevState => ({
                ...prevState,
                cantidad_cuotas: 0
            }));
        } else {
            handleTipoColegioChange();
        }
    };


    return (
        <div>
            <div className="titulo-formulario">Formulario estudiante</div>
            <form onSubmit={handleSubmit}>
                <label htmlFor="rut">Rut:</label>
                <input type="text" 
                       id="rut" 
                       name="rut" 
                       value={formData.rut} 
                       onChange={handleInputChange} 
                       pattern="\d{2}\.\d{3}\.\d{3}-[\dkK]" 
                       title="El RUT debe tener el formato xx.xxx.xxx-x" 
                       required /><br />

                <label htmlFor="apellidos">Apellidos:</label>
                <input type="text" id="apellidos" name="apellidos" value={formData.apellidos} onChange={handleInputChange} required /><br />

                <label htmlFor="nombres">Nombres:</label>
                <input type="text" id="nombres" name="nombres" value={formData.nombres} onChange={handleInputChange} required /><br />

                <label htmlFor="nacimiento">Fecha de Nacimiento:</label>
                <input type="date" id="nacimiento" name="nacimiento" value={formData.nacimiento} onChange={handleInputChange} required /><br />

                <label htmlFor="tipocolegio">Tipo de Colegio:</label>
                <select id="tipocolegio" name="tipocolegio" value={formData.tipocolegio} onChange={(e) => { handleInputChange(e); handleTipoColegioChange(); }} required>
                    <option value="Municipal">Municipal</option>
                    <option value="Subvencionado">Subvencionado</option>
                    <option value="Privado">Privado</option>
                </select><br />

                <label htmlFor="nombrecolegio">Nombre del Colegio:</label>
                <input type="text" id="nombrecolegio" name="nombrecolegio" value={formData.nombrecolegio} onChange={handleInputChange} required /><br />

                <label htmlFor="añoegresocolegio">Año de egreso del colegio:</label>
                <input type="text" id="añoegresocolegio" name="añoegresocolegio" value={formData.añoegresocolegio} onChange={handleInputChange} required /><br />

                <label htmlFor="tipodepago">Tipo de Pago:</label>
                <select id="tipodepago" name="tipodepago" value={formData.tipodepago} onChange={(e) => { handleInputChange(e); handleTipoDePagoChange(); }} required>
                    <option value="Contado">Contado</option>
                    <option value="Cuotas">Cuotas</option>
                </select><br />

                {formData.tipodepago !== 'Contado' &&
                <select id="cantidad_cuotas" name="cantidad_cuotas" value={formData.cantidad_cuotas} onChange={handleInputChange}>
                    {handleTipoColegioChange().map((cuota, index) => (
                        <option key={index} value={cuota}>{cuota}</option>
                    ))}
                </select>
                }

                <input type="submit" value="guardar" />
            </form>
        </div>
    );
}

export default Formulario;
