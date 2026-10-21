
function AdminNavigation({ activeTab, onTabChange }) {
  const tabs = [
    { id: "students", label: "Students" },
    { id: "enrollments", label: "Enrollments" },
    { id: "courses", label: "Courses" },
  ];

  return (
    <nav className="admin-navigation">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          type="button"
          className={activeTab === tab.id ? "active" : ""}
          onClick={() => onTabChange(tab.id)}
        >
          {tab.label}
        </button>
      ))}
    </nav>
  );
}

export default AdminNavigation;
