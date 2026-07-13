import React, { useState, useEffect } from "react";
import {Navigate} from "react-router-dom";
import { Link } from "react-router-dom";

type RedditUser = {
  name: string;
  id: string;
  icon_img?: string;
  created?: number;
  link_karma?: number;
  comment_karma?: number;
};

function Profile() {
  const params = new URLSearchParams(window.location.search);
  const usernameFromQuery = params.get("username");
  const redditUsername = localStorage.getItem("redditUsername");
  const [username, setUsername] = useState<string | null>(null);
  const token = localStorage.getItem("token");
  const [user, setUser] = useState<RedditUser | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [other, setOther] = useState<string | null>(null);


  useEffect(() => {
    /*
    if(redditUsername){
      setUsername(redditUsername);
    }
    else if (usernameFromQuery) {
      localStorage.setItem("redditUsername", usernameFromQuery);
      setUsername(usernameFromQuery);
    }
    if (!token) {
      return <Navigate to="/" />;
    }
    if (error) {
      alert("OAuth failed: " + error);
    }
     */
    if(redditUsername){
    fetch("http://localhost:8080/api/v1/redditclient/me/"+redditUsername, {
      credentials: "include",
      method: "GET",
      headers: {
        "Content-type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then(async (response) => {
        if (!response.ok) {
          const text = await response.text();
          throw new Error(`HTTP ${response.status}: ${text}`);
        }
        return response.json();
      })
      .then((json) => setUser(json))
      .catch((err) => setError(err.message));
  }else{
      fetch("http://localhost:8080/api/v1/redditclient/me", {
        credentials: "include",
        method: "GET",
        headers: {
          "Content-type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      })
          .then(async (response) => {
            if (!response.ok) {
              const text = await response.text();
              throw new Error(`HTTP ${response.status}: ${text}`);
            }
            return response.text();
          })
          .then((text) => setOther(text))
          .catch((err) => setError(err.message))
    }}, []);

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-6">
      <header className="bg-white rounded-lg shadow-md p-8 max-w-md w-full text-center">
        <h1 className="text-3xl font-bold mb-6 text-gray-900">Reddit User Info</h1>

        {error && (
          <div className="text-red-600 font-semibold mb-4">❌ {error}</div>
        )}

        {!user && !error && (
          <div className="text-lg text-gray-700 mb-4">Loading...</div>
        )}
        <div>

        {
        redditUsername ? (
                    <p>Welcome, {redditUsername}!</p>
                ) : (
                    <p>No username</p>)}
        </div>
        <div className="flex flex-row justify-center p-3">

          {username ? (
              <p>Welcome, {username}!</p>
          ) : (
              <Link
                  to="/connect-reddit"
                  className="bg-orange-600 text-white rounded-md px-7 py-3 text-lg font-semibold transition-colors duration-300 hover:bg-orange-700 inline-block"
                  aria-label="Get started with Reddit Vault"
              >
                Connect Reddit
              </Link>
          )}
        </div>
        <div className="flex flex-row justify-center p-3">

          {other ? (
              <p>Other: {other}!</p>
          ) : (
              <p>No other</p>
          )}
        </div>


        {user && (
          <div className="user-card">
            {user.icon_img && (
              <img
                className="mx-auto rounded-full w-24 h-24 mb-4 shadow-md"
                src={user.icon_img.split("?")[0]}
                alt="avatar"
              />
            )}
            <h2 className="text-2xl font-semibold mb-2">{user.name}</h2>
            <p className="mb-1">
              <strong>ID:</strong> {user.id}
            </p>
            <p className="mb-1">
              <strong>Account Created:</strong>{" "}
              {user.created
                ? new Date(user.created * 1000).toLocaleDateString()
                : "N/A"}
            </p>
            <p className="mb-1">
              <strong>Link Karma:</strong> {user.link_karma ?? "N/A"}
            </p>
            <p>
              <strong>Comment Karma:</strong> {user.comment_karma ?? "N/A"}
            </p>
          </div>
        )}
      </header>
    </div>
  );
}

export default Profile;
