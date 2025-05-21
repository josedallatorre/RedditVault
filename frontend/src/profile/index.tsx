import React, {useState, useEffect} from 'react';
import logo from './logo.svg';
import './App.css';

type RedditUser = {
  name: string;
  id: string;
  icon_img?: string;
  created?: number;
  link_karma?: number;
  comment_karma?: number;
};


function profile() {
  const token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlNIQTI1NjpzS3dsMnlsV0VtMjVmcXhwTU40cWY4MXE2OWFFdWFyMnpLMUdhVGxjdWNZIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNzQ3ODM5NTI4LjU1NTAyNCwiaWF0IjoxNzQ3NzUzMTI4LjU1NTAyMywianRpIjoiQ1RvN0FEVlFiTzJlNlBzOWxvUUVDeWRSX1JkaWZBIiwiY2lkIjoidTBhUmZ5c2ZETmtCdEJQN0xVaWVnUSIsImxpZCI6InQyX2N3YzUxZThoIiwiYWlkIjoidDJfY3djNTFlOGgiLCJsY2EiOjE2MjQ0NzcyNTgwMDAsInNjcCI6ImVKeUtWc3BNU2MwcnlTeXBWTkpSeXNnc0xza3ZxbFNLQlFRQUFQX19Xam9INlEiLCJyY2lkIjoiN3A2cUNxWUNsNUN5Yi04NXhyaTl4Wnc3SzZkR2h4eU1MVnh0M0wxdkJVVSIsImZsbyI6OH0.hmSzWsheujuj0cFM52ixnBu4vHoVgaCkabaOy_kD1eAYG9SVBBX-CBnGI2L6-JSsFTsjiBff_FF_wJ5cMxxMdkHKn8iuQ57LHQqLR97WBpurd5DSfiwk3hy3fNHU3KQYYyanX90x9YtWZ5q_uQElgbGkFth8u5BHdbP2JzT0nsYxmyJTc5hycaVlIB3GVWgjEqvT9wifGiSHV4Hv3SqZZNAjTgPFoDERWI4WRILncSh91NVRgYMF6grJl6e13B4jA-nqc55uIuTiKQZQwNaRKMfl0UQ1EhBBXsWQI6OvmnrvCBi7-ECzOBmztvm-UgmpPUeQ1GtkwfFml_wYBg892w"
  const [user, setUser] = useState<RedditUser | null>(null);
  const [error, setError] = useState<string | null>(null);


  useEffect(() => {
    fetch('http://localhost:8080/me', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': token,
      }
    })
      .then(async response => {
        if (!response.ok) {
          const text = await response.text();
          throw new Error(`HTTP ${response.status}: ${text}`);
        }
        return response.json();
      })
      .then(json => setUser(json))
      .catch(err => setError(err.message));
  }, []);
  
    return (
    <div className="App">
      <header className="App-header">
        <h1>Reddit User Info</h1>

        {error && <div className="error">❌ {error}</div>}

        {!user && !error && <div className="loading">Loading...</div>}

        {user && (
          <div className="user-card">
            {user.icon_img && (
              <img className="avatar" src={user.icon_img.split("?")[0]} alt="avatar" />
            )}
            <h2>{user.name}</h2>
            <p><strong>ID:</strong> {user.id}</p>
            <p><strong>Account Created:</strong> {user.created ? new Date(user.created * 1000).toLocaleDateString() : "N/A"}</p>
            <p><strong>Link Karma:</strong> {user.link_karma ?? "N/A"}</p>
            <p><strong>Comment Karma:</strong> {user.comment_karma ?? "N/A"}</p>
          </div>
        )}
      </header>
    </div>
  );
}

export default profile;