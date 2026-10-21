import { useState } from "react";
import api from "../api/axiosConfig";
import "../styles/Login.css";

function Login({ onLogin, onRegisterClick }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");
    setLoading(true);

    try {
      const response = await api.post("/auth/login", {
        email,
        password,
      });

      const data = response.data;

      if (!data.token) {
        setError(
          "Login succeeded, but no JWT token was returned."
        );
        return;
      }

      // Save the JWT for protected API requests.
      localStorage.setItem("jwtToken", data.token);

      onLogin(data);
    } catch (err) {
      if (err.response) {
        setError(
          err.response.data?.message ||
            "Login failed. Please check your credentials."
        );
      } else {
        setError(
          "Cannot connect to the backend. Check whether Spring Boot is running."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>Student Portal</h1>

        <p className="login-subtitle">
          Sign in to your account
        </p>

        <form onSubmit={handleLogin}>
          <label htmlFor="email">Email Address</label>
          <input
            id="email"
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
            autoComplete="email"
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            autoComplete="current-password"
          />

          <button type="submit" disabled={loading}>
            {loading ? "Signing in..." : "Login"}
          </button>
        </form>

        {message && (
          <p className="success-message">{message}</p>
        )}

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        <p className="signup-text">
          Don't have an account?{" "}
          <button
            type="button"
            className="signup-link"
            onClick={onRegisterClick}
          >
            Sign Up
          </button>
        </p>
      </div>
    </div>
  );
}

export default Login;