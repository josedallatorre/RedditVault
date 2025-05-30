// Layout.tsx
import { Outlet, Link } from 'react-router-dom';

const Layout = () => {
  return (
    <div className="min-h-screen flex flex-col bg-gray-100">
      <header className="bg-blue-600 text-white p-4 shadow-md">
        <nav className="flex gap-6 text-lg font-medium">
          <Link to="/">Home</Link>
          <Link to="/profile">Profile</Link>
          <Link to="/saved">Saved Posts</Link>
          <Link to="/vite">Vite index page</Link>
        </nav>
      </header>

      <main className="flex-1 p-8">
        <Outlet />
      </main>

      <footer className="bg-gray-800 text-white text-center p-4 text-sm">
        &copy; {new Date().getFullYear()} Reddit Vault. All rights reserved.
      </footer>
    </div>
  );
};

export default Layout;