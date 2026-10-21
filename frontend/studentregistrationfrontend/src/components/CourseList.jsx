import { useNavigate } from "react-router-dom";

function CourseList({
  courses,
  enrollments,
  searchTerm,
  onSearchChange,
  onEnroll,
  enrollingCourseId,
}) {
  const navigate = useNavigate();

  const filteredCourses = courses.filter((course) => {
    const search = searchTerm.toLowerCase().trim();

    return (
      course.title?.toLowerCase().includes(search) ||
      course.courseCode?.toLowerCase().includes(search) ||
      course.description?.toLowerCase().includes(search)
    );
  });

  const isEnrolled = (courseId) =>
    enrollments.some(
      (enrollment) =>
        enrollment.courseId === courseId &&
        enrollment.status === "ENROLLED"
    );

  const handleViewDetails = (courseId) => {
    navigate(`/student/courses/${courseId}`);
  };

  return (
    <section className="dashboard-section">
      <h2>Available Courses</h2>

      <input
        type="text"
        className="course-search"
        placeholder="Search by course name, code, or description..."
        value={searchTerm}
        onChange={(event) => onSearchChange(event.target.value)}
      />

      {courses.length === 0 ? (
        <p>No courses are available yet.</p>
      ) : filteredCourses.length === 0 ? (
        <p>No courses match your search.</p>
      ) : (
        <div className="course-grid">
          {filteredCourses.map((course) => {
            const enrolled = isEnrolled(course.id);

            return (
              <article className="course-card" key={course.id}>
                <h3>{course.title}</h3>

                <p>
                  <strong>Code:</strong> {course.courseCode}
                </p>

                <p>
                  {course.description || "No description available."}
                </p>

                <p>
                  <strong>Credits:</strong> {course.credits}
                </p>

                <button
                  type="button"
                  onClick={() => handleViewDetails(course.id)}
                >
                  View Details
                </button>

                <button
                  type="button"
                  disabled={
                    enrolled || enrollingCourseId !== null
                  }
                  onClick={() => onEnroll(course.id)}
                >
                  {enrollingCourseId === course.id
                    ? "Enrolling..."
                    : enrolled
                      ? "Enrolled"
                      : "Enroll Now"}
                </button>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default CourseList;
