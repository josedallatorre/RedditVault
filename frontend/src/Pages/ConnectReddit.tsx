import { useState } from "react";

const ConnectReddit = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const connectReddit = async () => {
        try {
            setLoading(true);
            setError(null);

            const token = localStorage.getItem("token");

            if (!token) {
                throw new Error("Please login first.");
            }

            const response = await fetch(
                "http://localhost:8080/api/v1/reddit/auth",
                {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) {
                throw new Error("Failed to generate Reddit auth URL");
            }

            const data = await response.json();

            // Redirect browser to Reddit
            window.location.href = data.url;
        } catch (err: any) {
            setError(err.message);
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
                <h1 className="text-3xl font-bold mb-4 text-center text-orange-600">
                    Connect Reddit
                </h1>

                <p className="text-gray-600 mb-6 text-center">
                    Link your Reddit account to start backing up saved posts and comments.
                </p>

                {error && (
                    <div className="bg-red-100 text-red-700 p-3 rounded mb-4">
                        {error}
                    </div>
                )}

                <button
                    onClick={connectReddit}
                    disabled={loading}
                    className="w-full bg-orange-600 text-white py-3 rounded hover:bg-orange-700 disabled:opacity-50"
                >
                    {loading ? "Redirecting..." : "Connect Reddit"}
                </button>
            </div>
        </div>
    );
};

export default ConnectReddit;