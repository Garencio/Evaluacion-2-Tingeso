import React from 'react';

function Header() {
    return (
        <header>
            <nav>
                <a href="/estudiantes">Ingresar Estudiante</a>
                <a href="/estudiantes/listado">Estudiantes</a>
                <a href="/examenes">Examenes</a>
                <a href="/examenes/upload">Subir Examenes</a>

            </nav>
        </header>
    );
}

export default Header;
