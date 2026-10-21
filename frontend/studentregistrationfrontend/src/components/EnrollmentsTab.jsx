
import { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import "../styles/EnrollmentsTab.css";
function EnrollmentsTab({ onMessage, onError }) {
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  const loadEnrollments = async () => {
    setLoading(true);
    onError("");

    try {
      const response = await api.get("/admin/enrollments");
      setEnrollments(response.data);
    } catch (err) {
      onError(
        err.response?.data?.message ||
          "Unable to load enrollments."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEnrollments();
  }, []);

  const deleteEnrollment = async (enrollment) => {
    const confirmed = window.confirm(
      `Delete enrollment for ${enrollment.studentName} in ${enrollment.courseTitle}?`
    );

    if (!confirmed) return;

    setDeletingId(enrollment.id);
    onError("");
    onMessage("");

    try {
      await api.delete(`/admin/enrollments/${enrollment.id}`);

      setEnrollments((current) =>
        current.filter((item) => item.id !== enrollment.id)
      );

      onMessage("Enrollment deleted successfully.");
    } catch (err) {
      onError(
        err.response?.data?.message ||
          "Unable to delete enrollment."
      );
    } finally {
      setDeletingId(null);
    }
  };

  const normalizedSearch = searchTerm.trim().toLowerCase();

  const filteredEnrollments = enrollments.filter((item) =>
    [
      item.studentName,
      item.studentEmail,
      item.courseTitle,
      item.courseCode,
      item.status,
    ].some((value) =>
      String(value ?? "").toLowerCase().includes(normalizedSearch)
    )
  );

  const enrolledCount = enrollments.filter(
    (item) => item.status === "ENROLLED"
  ).length;

  const otherCount = enrollments.length - enrolledCount;

  if (loading) return <p>Loading enrollments...</p>;

  return (
    <div className="enrollments-page">
      <section className="admin-section">
        <div className="enrollment-heading">
          <div>
            <h2>Student Enrollments</h2>
            <p>View and manage student course registrations.</p>
          </div>
          <span className="enrollment-total">
            {enrollments.length} Total
          </span>
        </div>

        <div className="enrollment-summary">
          <div className="enrollment-summary-card">
            <span>Total Enrollments</span>
            <strong>{enrollments.length}</strong>
          </div>

          <div className="enrollment-summary-card">
            <span>Enrolled</span>
            <strong className="enrollment-active-count">
              {enrolledCount}
            </strong>
          </div>

          <div className="enrollment-summary-card">
            <span>Other Statuses</span>
            <strong>{otherCount}</strong>
          </div>
        </div>

        <div className="enrollment-toolbar">
          <input
            type="search"
            className="enrollment-search"
            placeholder="Search student, email, course, or status..."
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            aria-label="Search enrollments"
          />

          <span className="enrollment-results">
            {filteredEnrollments.length} result(s)
          </span>
        </div>

        {filteredEnrollments.length === 0 ? (
          <div className="enrollment-empty">
            <h3>
              {enrollments.length === 0
                ? "No enrollments yet"
                : "No matching enrollments"}
            </h3>
            <p>
              {enrollments.length === 0
                ? "Student course registrations will appear here."
                : "Try searching with a different name, email, or course."}
            </p>
          </div>
        ) : (
          <div className="admin-table-wrapper">
            <table className="admin-table enrollment-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Student</th>
                  <th>Email</th>
                  <th>Course</th>
                  <th>Course Code</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {filteredEnrollments.map((item) => (
                  <tr key={item.id}>
                    <td>#{item.id}</td>
                    <td className="enrollment-student-name">
                      {item.studentName}
                    </td>
                    <td>{item.studentEmail}</td>
                    <td>{item.courseTitle}</td>
                    <td>
                      <span className="enrollment-course-code">
                        {item.courseCode}
                      </span>
                    </td>
                    <td>
                      <span
                        className={`enrollment-status status-${String(
                          item.status ?? "unknown"
                        ).toLowerCase()}`}
                      >
                        {item.status || "Unknown"}
                      </span>
                    </td>
                    <td>
                      <button
                        type="button"
                        className="enrollment-delete-button"
                        disabled={deletingId !== null}
                        onClick={() => deleteEnrollment(item)}
                      >
                        {deletingId === item.id
                          ? "Deleting..."
                          : "Delete"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

export default EnrollmentsTab;
