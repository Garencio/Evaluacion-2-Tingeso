import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import './styles.css';
import Header from './components/Header.jsx';
import Formulario from './components/Formulario.jsx';
import Listado from './components/Listado.jsx';
import Cuotas from './components/Cuotas.jsx';
import Examenes from './components/Examenes.jsx';
import Upload from './components/Upload.jsx';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        
        <Routes>
          <Route path="/estudiantes" element={<Formulario />} />
          <Route path="/estudiantes/listado" element={<Listado/>} />
          <Route path="/cuotas/:id" element={<Cuotas/>} />
          <Route path="/examenes" element={<Examenes/>} />
          <Route path="/examenes/upload" element={<Upload/>} />
        </Routes>
        
      </div>
    </Router>
  );
}

export default App;
