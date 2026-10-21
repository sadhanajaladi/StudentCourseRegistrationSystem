
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axiosConfig";
import "../styles/StudentDashboard.css";

function CourseDetails({ user }) {
  const { courseId } = useParams();
  const navigate = useNavigate();

  const [course, setCourse] = useState(null);
  const [enrolled, setEnrolled] = useState(false);
  const [loading, setLoading] = useState(true);
  const [enrolling, setEnrolling] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    const loadCourseDetails = async () => {
      setLoading(true);
      setError("");

      try {
        const [courseResponse, enrollmentResponse] =
          await Promise.all([
            api.get(`/courses/${courseId}`),
            api.get("/student/enrollments"),
          ]);

        setCourse(courseResponse.data);

        setEnrolled(
          enrollmentResponse.data.some(
            (item) =>
              item.courseId === courseResponse.data.id &&
              item.status === "ENROLLED"
          )
        );
      } catch (err) {
        setError(
          err.response?.data?.message ||
            "Unable to load course details. Please try again."
        );
      } finally {
        setLoading(false);
      }
    };

    loadCourseDetails();
  }, [courseId]);

  const handleEnroll = async () => {
    setError("");
    setMessage("");
    setEnrolling(true);

    try {
      await api.post("/student/enrollments", {
        courseId: course.id,
      });

      setEnrolled(true);
      setMessage("Successfully enrolled in this course!");
    } catch (err) {
      const status = err.response?.status;

      if (!err.response) {
        setError("Cannot connect to the server. Please try again.");
      } else if (status === 401) {
        setError("Your session may have expired. Please log in again.");
      } else if (status === 403) {
        setError("You don't have permission to enroll in this course.");
      } else {
        setError(
          err.response?.data?.message ||
            "Enrollment failed. You may already be enrolled."
        );
      }
    } finally {
      setEnrolling(false);
    }
  };

  if (loading) {
    return <p className="dashboard-loading">Loading course details...</p>;
  }

  if (error && !course) {
    return (
      <div className="dashboard-page">
        <p className="dashboard-error">{error}</p>
        <button type="button" onClick={() => navigate("/student")}>
          Back to Dashboard
        </button>
      </div>
    );
  }

  if (!course) {
    return <p>Course not found.</p>;
  }

  return (
    <div className="dashboard-page">
      <header className="dashboard-header">
        <div>
          <h1>Course Details</h1>
          <p>Welcome, {user?.name || "Student"}!</p>
        </div>

        <button
          type="button"
          className="logout-button"
          onClick={() => navigate("/student")}
        >
          Back to Dashboard
        </button>
      </header>

      <section className="dashboard-section">
        <article className="course-card">
          <h2>{course.title}</h2>

          <p>
            <strong>Course ID:</strong> {course.id}
          </p>

          <p>
            <strong>Course Code:</strong> {course.courseCode}
          </p>

          <p>
            <strong>Description:</strong>{" "}
            {course.description || "No description available."}
          </p>

          <p>
            <strong>Credits:</strong> {course.credits}
          </p>

          <p>
            <strong>Enrollment Status:</strong>{" "}
            {enrolled ? "Enrolled" : "Not enrolled"}
          </p>

          {message && (
            <p className="dashboard-success">{message}</p>
          )}

          {error && (
            <p className="dashboard-error" role="alert">
              {error}
            </p>
          )}

          <button
            type="button"
            disabled={enrolled || enrolling}
            onClick={handleEnroll}
          >
            {enrolling
              ? "Enrolling..."
              : enrolled
                ? "Already Enrolled"
                : "Enroll Now"}
          </button>
        </article>
      </section>
    </div>
  );
}

export default CourseDetails;
