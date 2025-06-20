import React, { useEffect, useState } from "react";

type ApiResponse = RedditMedia[];
type RedditMedia = {
  id: string;
  author: string;
  title: string;
  url: string;
  subreddit: string;
};

function getMediaType(url: string): "video" | "gif" | "image" | "gallery" | "unknown" {
  if (url.includes("v.redd.it")) return "video";
  if (url.match(/\.(gif)$/i)) return "gif";
  if (url.match(/\.(jpe?g|png|webp)$/i)) return "image";
  if (url.includes("reddit.com/gallery")) return "gallery";
  return "unknown";
}

function SavedPost() {
  const [posts, setMedia] = useState<RedditMedia[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    async function fetchPosts() {
      const redditUsername = localStorage.getItem("redditUsername");
      try {
        const res = await fetch("http://localhost:8080/api/v1/redditclient/saved", {
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

        const json: ApiResponse = await res.json();
        console.log(json);

        if (json && Array.isArray(json)) {
          const posts = json.map((child) => child);
          console.log(posts);
          setMedia(posts);
        } else {
          throw new Error("Unexpected API response format");
        }
      } catch (err: any) {
        setError(err.message || "Failed to fetch videos");
      } finally {
        setLoading(false);
      }
    }

    fetchPosts();
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
    <div className="my-5 p-5 bg-gray-50 rounded-xl font-sans">
      {posts.length === 0 && (
        <p className="text-center text-gray-600 italic mt-8">No videos saved.</p>
      )}
      {posts.map((post, idx) => {
        const mediaType = getMediaType(post.url);
        return (
          <div
            key={idx}
            className="bg-white rounded-lg shadow-sm p-4 mb-5 cursor-pointer transition-transform hover:scale-[1.02]"
          >
            <h3 className="text-lg font-semibold text-gray-900 mb-2 break-words">
              {post.title}
            </h3>
            <p className="text-sm text-gray-600 mb-1 break-words">
              Subreddit: {post.subreddit || "N/A"}
            </p>
            <p className="text-sm text-gray-600 mb-3 break-words">
              Author: {post.author || "Unknown"}
            </p>
            <p className="text-sm text-gray-600 mb-3 break-words">
              URL: {post.url || "Unknown"}
            </p>

            {(() => {
              switch (mediaType) {
                case "video":
                  return (
                    <video
                      className="rounded-lg max-w-full shadow-md"
                      controls
                      width={480}
                      src={post.url}
                    />
                  );
                case "gif":
                case "image":
                  return (
                    <div className="max-w-full lg:max-w-lg border border-red-500">
                    <img
                      className="rounded-lg sm:max-size-20  shadow-md"
                      src={post.url}
                      alt={post.title}
                    />
                    </div>
                  );
                case "gallery":
                  return (
                    <p className="text-sm text-blue-600 italic">
                      Reddit gallery: not directly supported.
                    </p>
                  );
                default:
                  return (
                    <p className="text-sm text-gray-500 italic">
                      No media available
                    </p>
                  );
              }
            })()}
          </div>
        );
      })}
    </div>
  );
}

export default SavedPost;
