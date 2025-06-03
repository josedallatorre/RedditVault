import React, { useState, useEffect } from "react";

type RedditUser = {
  name: string;
  id: string;
  icon_img?: string;
  created?: number;
  link_karma?: number;
  comment_karma?: number;
};

function Profile() {
  const redditUsername = localStorage.getItem("redditUsername");
  const [user, setUser] = useState<RedditUser | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("http://localhost:8080/me", {
      method: "POST",
      headers: {
        "Content-type": "application/json",
        Authorization: redditUsername || "",
      },
      body: JSON.stringify({
        username: redditUsername, // or separate username if needed
      }),
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
  }, []);

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
