// src/components/RegisterForm.tsx
import { useState, useEffect } from "react";
import Register  from '../api/register.tsx';
import { useNavigate } from 'react-router-dom';


export default function RegisterForm() {
  const [form, setForm] = useState({
    firstname: "",
    lastname: "",
    email: "",
    password: "",
    role: "USER", // or "ADMIN", depending on your backend
  });
    const [formData, setFormData] = useState('');
    const navigate = useNavigate();

    const [message, setMessage] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = await Register(form);
      localStorage.setItem("token", token);
      setMessage("Registered successfully!");
      setFormData('Form Submitted');
    } catch (err) {
      setMessage("Registration failed.");
    }
  };
  // TODO: check this. I don't think the logic is correct
    useEffect(() => {
        if (formData) {
            navigate('/profile');
        }
    }, [formData, navigate]);


    return (
    <form onSubmit={handleSubmit} className="max-w-md mx-auto p-4 border rounded space-y-4">
      <h2 className="text-xl font-semibold">Register</h2>

      <input
        type="text"
        name="firstname"
        placeholder="First Name"
        value={form.firstname}
        onChange={handleChange}
        className="w-full p-2 border rounded"
        required
      />

      <input
        type="text"
        name="lastname"
        placeholder="Last Name"
        value={form.lastname}
        onChange={handleChange}
        className="w-full p-2 border rounded"
        required
      />

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
        Register
      </button>

      {message && <p className="text-center text-sm">{message}</p>}
    </form>
  );
}
