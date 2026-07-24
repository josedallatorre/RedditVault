// src/api/auth.ts
export async function Authenticate(data: {
    email: string;
    password: string;
}) {
    const response = await fetch('http://localhost:8080/api/v1/auth/authenticate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    });

    if (!response.ok) {
        throw new Error('Registration failed');
    }

    const result = await response.json();
    return result.token; // JWT
}

export default Authenticate;
