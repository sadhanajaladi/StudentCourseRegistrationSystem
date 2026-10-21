function MyEnrollments({ enrollments }) {
  const activeEnrollments = enrollments.filter(
    (enrollment) => enrollment.status === "ENROLLED"
  );

  return (
    <section className="dashboard-section">
      <h2>My Enrollments</h2>

      {activeEnrollments.length === 0 ? (
        <p>You have not enrolled in any courses yet.</p>
      ) : (
        <div className="enrollment-list">
          {activeEnrollments.map((enrollment) => (
            <div
              className="enrollment-item"
              key={enrollment.id}
            >
              <span>{enrollment.courseTitle}</span>

              <span>{enrollment.courseCode}</span>

              <span className="enrollment-status">
                Enrolled
              </span>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}

export default MyEnrollments;