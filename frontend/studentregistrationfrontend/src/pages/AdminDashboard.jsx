import { useState } from "react";
import AdminHeader from "../components/AdminHeader";
import AdminNavigation from "../components/AdminNavigation";
import StudentsTab from "../components/StudentsTab";
import EnrollmentsTab from "../components/EnrollmentsTab";
import CoursesTab from "../components/CoursesTab";
import "../styles/AdminDashboard.css";
import Notification from "../components/Notification";
function AdminDashboard({ user, onLogout }) {
  const [activeTab, setActiveTab] = useState("students");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setMessage("");
    setError("");
  };

  const clearMessages = () => {
    setMessage("");
    setError("");
  };

  const showMessage = (text) => {
    setError("");
    setMessage(text);
  };

  const showError = (text) => {
    setMessage("");
    setError(text);
  };

  return (
    <div className="admin-page">
      <AdminHeader user={user} onLogout={onLogout} />

      <AdminNavigation
        activeTab={activeTab}
        onTabChange={handleTabChange}
      />

      <Notification message={message} type="success" />
<Notification message={error} type="error" />

      {activeTab === "students" && (
        <StudentsTab
          key="students"
          onMessage={showMessage}
          onError={showError}
        />
      )}

      {activeTab === "enrollments" && (
        <EnrollmentsTab
          key="enrollments"
          onMessage={showMessage}
          onError={showError}
        />
      )}

      {activeTab === "courses" && (
        <CoursesTab
          key="courses"
          onMessage={showMessage}
          onError={showError}
        />
      )}
    </div>
  );
}

export default AdminDashboard;
