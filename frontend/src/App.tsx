import './App.css'
import { Routes, Route } from 'react-router-dom';
import Profile from './Pages/Profile';
import Home from './Pages/Home';
import SavedPost from './Pages/SavedPost';
import Layout from './components/Layout';
import Vite from './Pages/ViteIndex';
import Subreddit from './Pages/Subreddit';


function App() {

  return (
    <Routes>
      <Route path="/" element={< Layout />}>
        <Route index element={<Home />} />
        <Route path="/profile" element={< Profile />} />
        <Route path="/saved" element={< SavedPost />} />
        <Route path="/subreddit" element={< Subreddit />} />
        <Route path="/vite" element={ <Vite /> } />
      </Route>
    </Routes>
  )
}

export default App
