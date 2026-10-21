function StudentHeader({ user, onLogout }) {
  return (
    <header className="dashboard-header">
      <div>
        <h1>Student Dashboard</h1>
        <p>Welcome, {user?.name || "Student"}!</p>
      </div>

      <button
        type="button"
        className="logout-button"
        onClick={onLogout}
      >
        Logout
      </button>
    </header>
  );
}

export default StudentHeader;