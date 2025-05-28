import { Outlet, Link } from 'react-router-dom';

const Layout = () => {
    return (
        <div className="min-h-screen flex flex-col">
            <header className="bg-blue-600 text-white p-4">
                <nav className="flex gap-4">
                    <Link to="/">Home</Link>
                </nav>
            </header>

            <main className="flex-1 p-6">
                <Outlet />
            </main>

            <footer className="bg-gray-800 text-white p-4 text-center">
                &copy; 2025 Your App
            </footer>
        </div>
    );
};

export default Layout;
