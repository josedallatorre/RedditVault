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
    );
}

export default Subreddit;
