import React, {useState, useEffect} from 'react';

type RedditUser = {
  name: string;
  id: string;
  icon_img?: string;
  created?: number;
  link_karma?: number;
  comment_karma?: number;
};


function Profile() {
  const token = "";
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

export default Profile;