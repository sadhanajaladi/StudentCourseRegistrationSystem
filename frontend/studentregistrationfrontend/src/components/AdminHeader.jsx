
function AdminHeader({ user, onLogout }) {
  return (
    <header className="admin-header">
      <div>
        <h1>Admin Dashboard</h1>
        <p>Welcome, {user?.name || "Administrator"}</p>
      </div>

      <button
        type="button"
        className="admin-logout"
        onClick={onLogout}
      >
        Logout
      </button>
    </header>
  );
}

export default AdminHeader;
