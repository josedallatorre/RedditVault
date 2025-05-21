import { Routes, Route } from 'react-router-dom';
import './App.css';
import Profile from './Pages/Profile';
import Home from './Pages/Home';

function App() {
  return (
    <>
    <Routes>
      <Route path="/" element={< Home />} />
      <Route path="/profile" element={< Profile />} />
    </Routes>
    </>
  );
}

export default App;