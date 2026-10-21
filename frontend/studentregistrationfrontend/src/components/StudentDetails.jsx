
function StudentDetails({ student, onClose }) {
  if (!student) return null;

  return (
    <section className="admin-section student-details">
      <h2>Student Details</h2>

      <p><strong>ID:</strong> {student.id}</p>
      <p><strong>Name:</strong> {student.name}</p>
      <p><strong>Email:</strong> {student.email}</p>
      <p>
        <strong>Created At:</strong>{" "}
        {student.createdAt
          ? new Date(student.createdAt).toLocaleString()
          : "Not available"}
      </p>

      <button type="button" onClick={onClose}>
        Close Details
      </button>
    </section>
  );
}

export default StudentDetails;
