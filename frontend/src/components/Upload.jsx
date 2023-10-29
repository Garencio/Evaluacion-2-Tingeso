import React, { useState } from 'react';

function Upload() {
    const [mensaje, setMensaje] = useState(null);
    const [selectedFile, setSelectedFile] = useState(null);

    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
    
        const formData = new FormData();
        formData.append('archivo', selectedFile);
    
        try {
            const response = await fetch('http://localhost:8080/examenes/upload', {
                method: 'POST',
                body: formData,
            });
    
            if (!response.ok) {
                throw new Error('Error al subir el archivo');
            }
    
            setMensaje('Archivo subido con éxito');
    
        } catch (error) {
            console.error('Hubo un error:', error);
            setMensaje('Hubo un error al subir el archivo. Por favor, inténtalo de nuevo.');
        }
    };    

    return (
        <div>
            <h2 className="titulo-formulario">Subir archivo de exámenes</h2>

            {mensaje && <p>{mensaje}</p>}

            <form onSubmit={handleSubmit} encType="multipart/form-data">
                <input type="file" name="archivo" onChange={handleFileChange} />
                <button type="submit">Subir</button>
            </form>
        </div>
    );
}

export default Upload;

