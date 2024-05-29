import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './App.css';
import Login from './pages/Login';
import ChatPage from './pages/ChatPage';
import Goodbye from './pages/Goodbye';

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Login />} />
          <Route path='/chat' element={<ChatPage />} />
          <Route path='/goodbye' element={<Goodbye />} />
          {/* <Route path=':userId' element={<ChatPage />} />
              </Route> */}
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
