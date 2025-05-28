import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import Profile from './Pages/Profile';
import Home from './Pages/Home';
import SavedPost from './Pages/SavedPost';
import Layout from './components/Layout';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={< Layout />} />
        <Route index element={<Home />} />
        <Route path="/profile" element={< Profile />} />
        <Route path="/saved" element={< SavedPost />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;