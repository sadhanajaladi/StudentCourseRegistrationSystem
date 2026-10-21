import { useEffect, useState } from "react";
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useNavigate,
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import StudentDashboard from "./pages/StudentDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import api from "./api/axiosConfig";
import CourseDetails from "./pages/CourseDetails";

function ProtectedRoute({ user, allowedRole, children }) {
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRole && user.role !== allowedRole) {
    return (
      <Navigate
        to={user.role === "ADMIN" ? "/admin" : "/student"}
        replace
      />
    );
  }

  return children;
}

function AppRoutes() {
  const [user, setUser] = useState(null);
  const [checkingAuth, setCheckingAuth] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    if (!token) {
      setCheckingAuth(false);
      return;
    }

    api.get("/auth/me")
      .then((response) => {
        setUser(response.data);
      })
      .catch(() => {
        localStorage.removeItem("jwtToken");
        setUser(null);
      })
      .finally(() => {
        setCheckingAuth(false);
      });
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);

    if (userData.role === "ADMIN") {
      navigate("/admin", { replace: true });
    } else {
      navigate("/student", { replace: true });
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    setUser(null);
    navigate("/login", { replace: true });
  };

  if (checkingAuth) {
    return <p>Checking your login...</p>;
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={
          user ? (
            <Navigate
              to={user.role === "ADMIN" ? "/admin" : "/student"}
              replace
            />
          ) : (
            <Login
              onLogin={handleLogin}
              onRegisterClick={() => navigate("/register")}
            />
          )
        }
      />

      <Route
        path="/register"
        element={
          user ? (
            <Navigate
              to={user.role === "ADMIN" ? "/admin" : "/student"}
              replace
            />
          ) : (
            <Register
              onBackToLogin={() => navigate("/login")}
            />
          )
        }
      />

      <Route
        path="/student"
        element={
          <ProtectedRoute user={user} allowedRole="STUDENT">
            <StudentDashboard
              user={user}
              onLogout={handleLogout}
            />
          </ProtectedRoute>
        }
      />

      
      <Route
        path="/student/courses/:courseId"
        element={
          <ProtectedRoute user={user} allowedRole="STUDENT">
            <CourseDetails user={user} />
          </ProtectedRoute>
        }
      />


      <Route
        path="/admin"
        element={
          <ProtectedRoute user={user} allowedRole="ADMIN">
            <AdminDashboard
              user={user}
              onLogout={handleLogout}
            />
          </ProtectedRoute>
        }
      />

      <Route
        path="*"
        element={
          <Navigate
            to={
              user
                ? user.role === "ADMIN"
                  ? "/admin"
                  : "/student"
                : "/login"
            }
            replace
          />
        }
      />
    </Routes>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  );
}

export default App;
