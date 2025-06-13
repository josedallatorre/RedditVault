import React, { useEffect, useState } from "react";
//import Pagination from '@mui/material/Pagination';
//import Stack from '@mui/material/Stack';

type Subreddit = {
    name: string;
}

function Subreddit() {
    const [subreddits, setSubreddits] = useState<Subreddit[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        async function fetchSubreddits() {
            const redditUsername = localStorage.getItem("redditUsername");
            try {
                const res = await fetch("http://localhost:8080/api/v1/subreddit/subreddits", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: redditUsername || "",
                    },
                    body: JSON.stringify({
                        username: redditUsername, // or separate username if needed
                    }),
                });

                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }

                const json: Subreddit[] = await res.json();

                if (Array.isArray(json)) {
                    console.log(subreddits);
                    setSubreddits(json);
                } else {
                    throw new Error("Unexpected API response format");
                }
                console.log(json, subreddits);
            } catch (err: any) {
                setError(err.message || "Failed to fetch videos");
            } finally {
                setLoading(false);
            }
        }

        fetchSubreddits();
    }, []);

    if (loading)
        return (
            <p className="text-center mt-10 text-lg text-gray-700 font-medium">
                Loading...
            </p>
        );

    if (error)
        return (
            <p className="text-center mt-10 text-lg text-red-600 font-semibold">
                Error: {error}
            </p>
        );

    return (
        <div>
            {
                subreddits.map(
                    subreddit => (
                        <p>{subreddit.name}</p>
                    )
                )
            }

        </div>
        /*
        <div className="max-w-3xl mx-auto my-5 p-5 bg-gray-50 rounded-xl shadow-md font-sans">
          {videos.length === 0 && (
            <p className="text-center text-gray-600 italic mt-8">No videos saved.</p>
          )}
          {videos.map((video, idx) => (
            <div
              key={idx}
              className="bg-white rounded-lg shadow-sm p-4 mb-5 cursor-pointer transition-transform hover:scale-[1.02]"
            >
              <h3 className="text-lg font-semibold text-gray-900 mb-2 break-words">
                {video.title}
              </h3>
              <p className="text-sm text-gray-600 mb-1 break-words">
                Subreddit: {video.subreddit.name || "N/A"}
              </p>
              <p className="text-sm text-gray-600 mb-3 break-words">
                Author: {video.author_fullname || "Unknown"}
              </p>
              {video.secure_media?.reddit_video?.fallback_url ? (
                <video
                  className="rounded-lg max-w-full shadow-md"
                  controls
                  width={480}
                  src={video.secure_media.reddit_video.fallback_url}
                />
              ) : (
                <p className="text-sm text-gray-500 italic">No video available</p>
              )}
            </div>
          ))}
        </div>
            */
    );
}

export default Subreddit;
