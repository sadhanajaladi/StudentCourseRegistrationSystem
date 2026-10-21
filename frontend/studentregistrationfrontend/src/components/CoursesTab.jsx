
import { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import "../styles/CourseTab.css";
const initialForm = {
  title: "",
  courseCode: "",
  description: "",
  credits: "3",
};

function CoursesTab({ onMessage, onError }) {
  const [courses, setCourses] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const loadCourses = async () => {
      try {
        const response = await api.get("/courses");
        setCourses(response.data);
      } catch (err) {
        onError(
          err.response?.data?.message || "Unable to load courses."
        );
      } finally {
        setLoading(false);
      }
    };

    loadCourses();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    onError("");
    onMessage("");

    const credits = Number(form.credits);

    if (!Number.isInteger(credits) || credits <= 0) {
      onError("Credits must be a positive whole number.");
      return;
    }

    setSaving(true);

    try {
      const response = await api.post("/courses", {
        title: form.title.trim(),
        courseCode: form.courseCode.trim(),
        description: form.description.trim(),
        credits,
      });

      setCourses((current) => [
        response.data,
        ...current.filter(
          (course) => course.id !== response.data.id
        ),
      ]);

      setForm(initialForm);
      onMessage(
        `Course "${response.data.title}" created successfully.`
      );
    } catch (err) {
      onError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to create course. Check the course code and required fields."
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="courses-page">
      <section className="admin-section course-form-section">
        <div className="course-section-heading">
          <div>
            <h2>Add New Course</h2>
            <p>Create and publish a course for students to enroll in.</p>
          </div>
          <span className="course-heading-icon">+</span>
        </div>

        <form className="admin-course-form" onSubmit={handleSubmit}>
          <div className="course-form-grid">
            <div className="course-field">
              <label htmlFor="course-title">Course Title</label>
              <input
                id="course-title"
                name="title"
                value={form.title}
                onChange={handleChange}
                placeholder="e.g. Java Programming"
                maxLength={150}
                required
              />
            </div>

            <div className="course-field">
              <label htmlFor="course-code">Course Code</label>
              <input
                id="course-code"
                name="courseCode"
                value={form.courseCode}
                onChange={handleChange}
                placeholder="e.g. CS101"
                maxLength={50}
                required
              />
            </div>

            <div className="course-field">
              <label htmlFor="course-credits">Credits</label>
              <input
                id="course-credits"
                name="credits"
                type="number"
                min="1"
                step="1"
                value={form.credits}
                onChange={handleChange}
                required
              />
            </div>

            <div className="course-field course-description-field">
              <label htmlFor="course-description">Description</label>
              <textarea
                id="course-description"
                name="description"
                value={form.description}
                onChange={handleChange}
                maxLength={1000}
                rows={4}
                placeholder="Describe the course topics and learning objectives..."
              />
              <span className="course-character-count">
                {form.description.length}/1000 characters
              </span>
            </div>
          </div>

          <div className="course-form-actions">
            <button type="submit" disabled={saving}>
              {saving ? "Saving Course..." : "+ Add Course"}
            </button>
          </div>
        </form>
      </section>

      <section className="admin-section available-courses-section">
        <div className="course-section-heading">
          <div>
            <h2>Available Courses</h2>
            <p>Manage the courses available for student registration.</p>
          </div>
          {!loading && (
            <span className="course-count">
              {courses.length}{" "}
              {courses.length === 1 ? "Course" : "Courses"}
            </span>
          )}
        </div>

        {loading ? (
          <p>Loading courses...</p>
        ) : courses.length === 0 ? (
          <div className="course-empty-state">
            <h3>No courses yet</h3>
            <p>Add your first course using the form above.</p>
          </div>
        ) : (
          <div className="course-management-grid">
            {courses.map((course) => (
              <article className="course-management-card" key={course.id}>
                <div className="course-card-top">
                  <span className="course-code-badge">
                    {course.courseCode}
                  </span>
                  <span className="course-id">
                    ID: {course.id}
                  </span>
                </div>

                <h3>{course.title}</h3>

                <p className="course-card-description">
                  {course.description || "No description provided."}
                </p>

                <div className="course-card-footer">
                  <span>
                    <strong>{course.credits}</strong>{" "}
                    {course.credits === 1 ? "Credit" : "Credits"}
                  </span>
                  <span className="course-availability">
                    Available
                  </span>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

export default CoursesTab;
