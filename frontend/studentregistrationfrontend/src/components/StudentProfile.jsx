function StudentProfile({ user }) {
  return (
    <section className="profile-card">
      <div className="profile-avatar">
        {user?.name?.charAt(0)?.toUpperCase() || "S"}
      </div>

      <div className="profile-details">
        <h2>My Profile</h2>

        <p>
          <strong>Name:</strong>{" "}
          {user?.name || "Not available"}
        </p>

        <p>
          <strong>Email:</strong>{" "}
          {user?.email || "Not available"}
        </p>

        <p>
          <strong>Role:</strong>{" "}
          {user?.role || "STUDENT"}
        </p>
      </div>
    </section>
  );
}

export default StudentProfile;