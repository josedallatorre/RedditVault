import React, { useEffect, useState } from "react";
import styles from '../assets/css/SavedPost.module.css'

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
    return <p className={styles.loading}>Loading...</p>;

  if (error)
    return <p className={`${styles.loading} ${styles.error}`}>Error: {error}</p>;

  return (
    <div className={styles.container}>
      {videos.length === 0 && (
        <p className={styles.noVideos}>No videos saved.</p>
      )}
      {videos.map((video, idx) => (
        <div key={idx} className={styles.card}>
          <h3 className={styles.title}>{video.title}</h3>
          <p className={styles.info}>Subreddit: {video.subreddit || "N/A"}</p>
          <p className={styles.info}>
            Author: {video.author_fullname || "Unknown"}
          </p>
          {video.secure_media?.reddit_video?.fallback_url ? (
            <video
              className={styles.video}
              controls
              width={480}
              src={video.secure_media.reddit_video.fallback_url}
            />
          ) : (
            <p className={styles.info} style={{ fontStyle: "italic", color: "#888" }}>
              No video available
            </p>
          )}
        </div>
      ))}
    </div>
  );
}

export default SavedPost;
