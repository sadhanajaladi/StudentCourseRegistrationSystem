import { useEffect, useState } from "react";
import api from "../api/axiosConfig";

import StudentHeader from "../components/StudentHeader";
import StudentProfile from "../components/StudentProfile";
import CourseList from "../components/CourseList";
import MyEnrollments from "../components/MyEnrollments";
import Notification from "../components/Notification";
import "../styles/StudentDashboard.css";

function StudentDashboard({ user, onLogout }) {
  const [courses, setCourses] = useState([]);
  const [enrollments, setEnrollments] = useState([]);

  const [loading, setLoading] = useState(true);
  const [enrollingCourseId, setEnrollingCourseId] = useState(null);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  const loadDashboard = async () => {
    setLoading(true);
    setError("");

    try {
      const [courseResponse, enrollmentResponse] =
        await Promise.all([
          api.get("/courses"),
          api.get("/student/enrollments"),
        ]);

      setCourses(courseResponse.data);
      setEnrollments(enrollmentResponse.data);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Could not load dashboard. Please try logging in again."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard();
  }, []);

  const enrollInCourse = async (courseId) => {
    setMessage("");
    setError("");
    setEnrollingCourseId(courseId);

    try {
      await api.post("/student/enrollments", { courseId });

      setMessage("Successfully enrolled in the course!");

      // Refresh the course and enrollment information.
      await loadDashboard();
    } catch (err) {
      const status = err.response?.status;
      const backendMessage = err.response?.data?.message;

      if (!err.response) {
        setError(
          "Cannot connect to the server. Please try again."
        );
      } else if (status === 401) {
        setError(
          "Your login session may have expired. Please log in again."
        );
      } else if (status === 403) {
        setError(
          "You don't have permission to perform this action."
        );
      } else if (status === 400 || status === 409) {
        setError(
          backendMessage ||
            "Enrollment could not be completed. You may already be enrolled."
        );
      } else {
        setError(
          backendMessage ||
            "Something went wrong while enrolling. Please try again."
        );
      }
    } finally {
      setEnrollingCourseId(null);
    }
  };

  const handleLogout = async () => {
    try {
      await api.post("/auth/logout");
    } catch {
      // Clear the token even if the logout request fails.
    } finally {
      localStorage.removeItem("jwtToken");
      onLogout();
    }
  };

  if (loading) {
    return (
      <p className="dashboard-loading">
        Loading dashboard...
      </p>
    );
  }

  return (
    <div className="dashboard-page">
      <StudentHeader
        user={user}
        onLogout={handleLogout}
      />

      <StudentProfile user={user} />

      <Notification message={message} type="success" />
<Notification message={error} type="error" />
      <CourseList
        courses={courses}
        enrollments={enrollments}
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
        onEnroll={enrollInCourse}
        enrollingCourseId={enrollingCourseId}
      />

      <MyEnrollments enrollments={enrollments} />
    </div>
  );
}

export default StudentDashboard;