import { useEffect, useState } from "react";


function Home() {
    const [username, setUsername] = useState<string | null>(null);
    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const usernameFromQuery = params.get("username");
        const error = params.get("error");

        if (usernameFromQuery) {
            setUsername(usernameFromQuery);
            localStorage.setItem("redditUsername", usernameFromQuery);
        }

        if (error) {
            alert("OAuth failed: " + error);
        }
    }, []);

    return (
        <div className="font-sans text-gray-900 min-h-screen flex flex-col justify-start bg-gray-100 p-5 max-w-3xl mx-auto">
            <header className="py-10 text-center">
                <h1 className="text-6xl font-extrabold mb-2 text-orange-600">Reddit Vault</h1>
                <p className="text-lg mx-auto mb-7 max-w-xl text-gray-600">
                    Securely archive and access all your saved Reddit posts and comments in one easy-to-use vault.
                </p>
            </header>
            <div>

                {username ? (
                    <p>Welcome, {username}!</p>
                ) : (
                    <a
                        href="http://localhost:8080/auth"
                        //target="_blank"
                        rel="noopener noreferrer"
                        className="bg-orange-600 text-white rounded-md px-7 py-3 text-lg font-semibold transition-colors duration-300 hover:bg-orange-700 inline-block"
                        aria-label="Get started with Reddit Vault"
                    >
                        Get Started
                    </a>
                )}
            </div>

            <section aria-label="Features" className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3">
                <div className="bg-white rounded-lg shadow p-5">
                    <h2 className="text-xl font-semibold mb-2 text-gray-900">Automatic Backup</h2>
                    <p className="text-gray-600">
                        Keep your saved posts and comments backed up effortlessly with automated sync.
                    </p>
                </div>
                <div className="bg-white rounded-lg shadow p-5">
                    <h2 className="text-xl font-semibold mb-2 text-gray-900">Offline Access</h2>
                    <p className="text-gray-600">
                        View your saved Reddit content anytime, even without internet connection.
                    </p>
                </div>
                <div className="bg-white rounded-lg shadow p-5">
                    <h2 className="text-xl font-semibold mb-2 text-gray-900">Privacy Focused</h2>
                    <p className="text-gray-600">
                        Your data is stored securely and never shared with third parties.
                    </p>
                </div>
            </section>
        </div>
    );
};

export default Home;