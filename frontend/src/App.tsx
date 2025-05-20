import React, {useState, useEffect} from 'react';
import logo from './logo.svg';
import './App.css';


function App() {
  const [data, setData] = useState(null);
  const token = ""

  useEffect(() => {
    fetch('http://localhost:8080/me',{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': `Bearer ${token}`, // notice the Bearer before your token
      }})
      .then(response => response.json())
      .then(json => setData(json))
      .catch(error => console.error(error));
  }, []);


  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        <a
          className="Reddit-auth-link"
          href="http://localhost:8080/auth"
          target="_blank"
          rel="noopener noreferrer"
        >
          Login with Reddit
        </a>
        <div>
          {data ? <pre>{JSON.stringify(data, null, 2)}</pre> : 'Loading...'}
        </div>
      </header>
    </div>
    
  );
}

export default App;
