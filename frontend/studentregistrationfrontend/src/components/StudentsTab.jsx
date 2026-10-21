
import { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import StudentDetails from "./StudentDetails";
import "../styles/StudentTab.css";
function StudentsTab({ onMessage, onError }) {
  const [students, setStudents] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);

  const studentsPerPage = 5;

  const loadStudents = async () => {
    setLoading(true);
    onError("");

    try {
      const response = await api.get("/admin/students");
      setStudents(response.data);
    } catch (err) {
      onError(
        err.response?.data?.message || "Unable to load students."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStudents();
  }, []);

  // Calculate pagination values
  const totalPages = Math.max(
    1,
    Math.ceil(students.length / studentsPerPage)
  );

  const startIndex = (currentPage - 1) * studentsPerPage;

  const currentStudents = students.slice(
    startIndex,
    startIndex + studentsPerPage
  );

  const viewStudent = async (id) => {
    setBusy(true);
    onError("");
    setSelectedStudent(null);

    try {
      const response = await api.get(`/admin/students/${id}`);
      setSelectedStudent(response.data);
    } catch (err) {
      onError(
        err.response?.data?.message ||
          "Unable to load student details."
      );
    } finally {
      setBusy(false);
    }
  };

  const deleteStudent = async (student) => {
    const confirmed = window.confirm(
      `Delete student ${student.name} (${student.email})? This cannot be undone.`
    );

    if (!confirmed) return;

    setBusy(true);
    onError("");
    onMessage("");

    try {
      await api.delete(`/admin/students/${student.id}`);

      const updatedStudents = students.filter(
        (item) => item.id !== student.id
      );

      setStudents(updatedStudents);

      if (selectedStudent?.id === student.id) {
        setSelectedStudent(null);
      }

      // Move back if the current page becomes empty
      const updatedTotalPages = Math.max(
        1,
        Math.ceil(updatedStudents.length / studentsPerPage)
      );

      if (currentPage > updatedTotalPages) {
        setCurrentPage(updatedTotalPages);
      }

      onMessage(`Student "${student.name}" deleted successfully.`);
    } catch (err) {
      onError(
        err.response?.data?.message || "Unable to delete student."
      );
    } finally {
      setBusy(false);
    }
  };

  if (loading) return <p>Loading students...</p>;

  return (
    <>
      <section className="admin-section">
        <h2>Registered Students</h2>

        {students.length === 0 ? (
          <p>No students found.</p>
        ) : (
          <>
            <div className="admin-table-wrapper">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Created At</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {currentStudents.map((student) => (
                    <tr key={student.id}>
                      <td>{student.id}</td>
                      <td>{student.name}</td>
                      <td>{student.email}</td>
                      <td>
                        {student.createdAt
                          ? new Date(
                              student.createdAt
                            ).toLocaleString()
                          : "—"}
                      </td>
                      <td>
                        <button
                          type="button"
                          disabled={busy}
                          onClick={() => viewStudent(student.id)}
                        >
                          View Details
                        </button>{" "}
                        <button
                          type="button"
                          disabled={busy}
                          onClick={() => deleteStudent(student)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="pagination">
              <p>
                Showing {startIndex + 1}–
                {Math.min(
                  startIndex + studentsPerPage,
                  students.length
                )}{" "}
                of {students.length} students
              </p>

              <div className="pagination-buttons">
                <button
                  type="button"
                  disabled={currentPage === 1 || busy}
                  onClick={() =>
                    setCurrentPage((page) => page - 1)
                  }
                >
                  Previous
                </button>

                <span>
                  Page {currentPage} of {totalPages}
                </span>

                <button
                  type="button"
                  disabled={currentPage === totalPages || busy}
                  onClick={() =>
                    setCurrentPage((page) => page + 1)
                  }
                >
                  Next
                </button>
              </div>
            </div>
          </>
        )}
      </section>

      <StudentDetails
        student={selectedStudent}
        onClose={() => setSelectedStudent(null)}
      />
    </>
  );
}

export default StudentsTab;
