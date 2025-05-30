import React, { useEffect, useState } from "react";

type RedditVideo = {
  fallback_url: string | null;
};

type PostData = {
  title: string;
  selftext: string;
  subreddit: string;
  author_fullname: string | null;
  saved: boolean;
  secure_media: {
    reddit_video: RedditVideo | null;
  };
};

type ApiChild = {
  data: PostData;
};

type ApiResponse = {
  kind: string;
  data: {
    after: string | null;
    dist: number;
    modhash: string | null;
    geo_filter: string;
    children: ApiChild[];
  };
};

function SavedPost() {
  const [videos, setVideos] = useState<PostData[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    async function fetchVideos() {
      try {
        const token = "";
        const res = await fetch("http://localhost:8080/saved", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: token,
          },
          body: JSON.stringify({
            username: "33prova33",
          }),
        });

        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }

        const json: ApiResponse = await res.json();

        if (json.data && Array.isArray(json.data.children)) {
          const posts = json.data.children.map((child) => child.data);
          setVideos(posts);
        } else {
          throw new Error("Unexpected API response format");
        }
      } catch (err: any) {
        setError(err.message || "Failed to fetch videos");
      } finally {
        setLoading(false);
      }
    }

    fetchVideos();
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
            Subreddit: {video.subreddit || "N/A"}
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
  );
}

export default SavedPost;
