import { Routes, Route } from 'react-router-dom';
import './App.css';
import Profile from './Pages/Profile';
import Home from './Pages/Home';
import SavedPost from './Pages/SavedPost';

function App() {
  return (
    <>
    <Routes>
      <Route path="/" element={< Home />} />
      <Route path="/profile" element={< Profile />} />
      <Route path="/saved" element={< SavedPost />} />
    </Routes>
    </>
  );
}

export default App;