// src/components/AuthenticateForm.tsx
import { useState } from "react";
import Authenticate  from '../api/authenticate.tsx';

export default function AuthenticateForm() {
    const [form, setForm] = useState({
        email: "",
        password: "",
    });

    const [message, setMessage] = useState("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const token = await Authenticate(form);
            localStorage.setItem("token", token);
            setMessage("Authenticated successfully!");
        } catch (err) {
            setMessage("Authentication failed.");
        }
    };

    return (
        <form onSubmit={handleSubmit} className="max-w-md mx-auto p-4 border rounded space-y-4">
            <h2 className="text-xl font-semibold">Authenticate</h2>


            <input
                type="email"
                name="email"
                placeholder="Email"
                value={form.email}
                onChange={handleChange}
                className="w-full p-2 border rounded"
                required
            />

            <input
                type="password"
                name="password"
                placeholder="Password"
                value={form.password}
                onChange={handleChange}
                className="w-full p-2 border rounded"
                required
            />

            <button type="submit" className="w-full bg-blue-600 text-black py-2 rounded">
                Authenticate
            </button>

            {message && <p className="text-center text-sm">{message}</p>}
        </form>
    );
}
